package com.sammyscl.fragments

import android.app.Fragment
import android.app.FragmentTransaction
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sammyscl.Helpers.SaveSharedPreference
import com.sammyscl.ProfileActivity
import com.sammyscl.R
import com.sammyscl.model.Response
import com.sammyscl.network.NetworkUtil
import com.sammyscl.Helpers.Constants
import java.io.IOException

import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

import com.sammyscl.Helpers.Validation.validateEmail
import com.sammyscl.Helpers.Validation.validateFields
import com.sammyscl.calendar.activities.SplashActivity

class LoginFragment : Fragment() {

    private var mEtEmail: EditText? = null
    private var mEtPassword: EditText? = null
    private var mBtLogin: Button? = null
    private var mTvRegister: TextView? = null
    private var mTvForgotPassword: TextView? = null
    private var mTiEmail: TextInputLayout? = null
    private var mTiPassword: TextInputLayout? = null
    private var mProgressBar: ProgressBar? = null

    private var mSubscriptions: CompositeSubscription? = null
    private var mSharedPreferences: SharedPreferences? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        mSubscriptions = CompositeSubscription()
        initViews(view)
        initSharedPreferences()
        return view
    }

    private fun initViews(v: View) {

        mEtEmail = v.findViewById<View>(R.id.et_email) as EditText
        mEtPassword = v.findViewById<View>(R.id.et_password) as EditText
        mBtLogin = v.findViewById<View>(R.id.btn_login) as Button
        mTiEmail = v.findViewById<View>(R.id.ti_email) as TextInputLayout
        mTiPassword = v.findViewById<View>(R.id.ti_password) as TextInputLayout
        mProgressBar = v.findViewById<View>(R.id.progress) as ProgressBar
        mTvRegister = v.findViewById<View>(R.id.tv_register) as TextView
        mTvForgotPassword = v.findViewById<View>(R.id.tv_forgot_password) as TextView

        mBtLogin!!.setOnClickListener { view -> login() }
        mTvRegister!!.setOnClickListener { view -> goToRegister() }
        mTvForgotPassword!!.setOnClickListener { view -> showDialog() }
    }

    private fun initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
    }

    private fun login() {

        setError()

        val email = mEtEmail!!.text.toString()
        val password = mEtPassword!!.text.toString()

        var err = 0

        if (!validateEmail(email)) {

            err++
            mTiEmail!!.error = "Email should be valid !"
        }

        if (!validateFields(password)) {
            err++
            mTiPassword!!.error = "Password should not be empty !"
        }

        if (err == 0) {
            loginProcess(email, password)
            SaveSharedPreference.setUserName(context, email)
            mProgressBar!!.visibility = View.VISIBLE
        } else {
            showSnackBarMessage("Enter Valid Details !")
        }
    }

    private fun setError() {
        mTiEmail!!.error = null
        mTiPassword!!.error = null
    }

    private fun loginProcess(email: String, password: String) {
        mSubscriptions!!.add(NetworkUtil.getRetrofit(email, password).login()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }

    private fun handleResponse(response: Response) {

        mProgressBar!!.visibility = View.GONE

        val editor = mSharedPreferences!!.edit()
        editor.putString(Constants.TOKEN, response.token)
        editor.putString(Constants.EMAIL, response.message)
        editor.apply()

        mEtEmail!!.setText(null)
        mEtPassword!!.setText(null)

        val intent = Intent(activity, SplashActivity::class.java)
        startActivity(intent)
    }

    private fun handleError(error: Throwable) {

        mProgressBar!!.visibility = View.GONE

        if (error is HttpException) {

            val gson = GsonBuilder().create()

            try {
                val errorBody = error.response().errorBody().string()
                val response = gson.fromJson<Response>(errorBody, Response::class.java!!)
                showSnackBarMessage(response.message)

            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else {
            showSnackBarMessage("Network Error !")
        }
    }

    private fun showSnackBarMessage(message: String?) {
        if (view != null) {
            Snackbar.make(view!!, message!!, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun goToRegister() {
        val ft = fragmentManager.beginTransaction()
        val fragment = RegisterFragment()
        ft.replace(R.id.fragmentFrame, fragment, RegisterFragment.TAG)
        ft.commit()
    }

    private fun showDialog() {

        val fragment = ResetPasswordDialog()

        fragment.show(fragmentManager, ResetPasswordDialog.TAG)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSubscriptions!!.unsubscribe()
    }

    companion object {

        val TAG = LoginFragment::class.java!!.getSimpleName()
    }
}
