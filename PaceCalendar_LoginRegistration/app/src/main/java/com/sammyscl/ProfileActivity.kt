package com.sammyscl

import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sammyscl.Helpers.SaveSharedPreference
import com.sammyscl.fragments.ChangePasswordDialog
import com.sammyscl.model.Response
import com.sammyscl.model.User
import com.sammyscl.Helpers.Constants

import java.io.IOException

import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

class ProfileActivity : AppCompatActivity(), ChangePasswordDialog.Listener {

    private var mTvName: TextView? = null
    private var mTvUserType: TextView? = null
    private var mTvEmail: TextView? = null
    private var mTvDate: TextView? = null
    private var mBtChangePassword: Button? = null
    private var mBtLogout: Button? = null

    private var mProgressbar: ProgressBar? = null

    private var mSharedPreferences: SharedPreferences? = null
    private var mToken: String? = null
    private var mEmail: String? = null

    private var mSubscriptions: CompositeSubscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        mSubscriptions = CompositeSubscription()
        initViews()
        initSharedPreferences()
        loadProfile()
    }

    private fun initViews() {
        mTvName = findViewById<View>(R.id.tv_name) as TextView
        mTvUserType = findViewById<View>(R.id.tv_userType) as TextView
        mTvEmail = findViewById<View>(R.id.tv_email) as TextView
        mTvDate = findViewById<View>(R.id.tv_date) as TextView
        mBtChangePassword = findViewById<View>(R.id.btn_change_password) as Button
        mBtLogout = findViewById<View>(R.id.btn_logout) as Button
        mProgressbar = findViewById<View>(R.id.progress) as ProgressBar

        mBtChangePassword!!.setOnClickListener { view -> showDialog() }
        mBtLogout!!.setOnClickListener { view -> logout() }
    }

    private fun initSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        mToken = mSharedPreferences!!.getString(Constants.TOKEN, "")
        mEmail = mSharedPreferences!!.getString(Constants.EMAIL, "")
    }

    private fun logout() {
        val editor = mSharedPreferences!!.edit()
        editor.putString(Constants.EMAIL, "")
        editor.putString(Constants.TOKEN, "")
        editor.apply()
        SaveSharedPreference.setUserName(applicationContext, "")
        finish()
    }

    private fun showDialog() {
        val fragment = ChangePasswordDialog()

        val bundle = Bundle()
        bundle.putString(Constants.EMAIL, mEmail)
        bundle.putString(Constants.TOKEN, mToken)
        fragment.arguments = bundle

        fragment.show(fragmentManager, ChangePasswordDialog.TAG)
    }

    private fun loadProfile() {
        mSubscriptions!!.add(NetworkUtil.getRetrofit(mToken as String).getProfile(mEmail as String)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }

    private fun handleResponse(user: User) {
        mProgressbar!!.visibility = View.GONE
        mTvName!!.text = user.name
        mTvUserType!!.text = user.userType
        mTvEmail!!.text = user.email
        mTvDate!!.text = user.created_at
    }

    private fun handleError(error: Throwable) {
        mProgressbar!!.visibility = View.GONE

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
        Snackbar.make(findViewById(R.id.activity_profile), message!!, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mSubscriptions!!.unsubscribe()
    }

    override fun onPasswordChanged() {
        showSnackBarMessage("Password Changed Successfully !")
    }

    companion object {
        val TAG = ProfileActivity::class.java!!.getSimpleName()
    }
}

