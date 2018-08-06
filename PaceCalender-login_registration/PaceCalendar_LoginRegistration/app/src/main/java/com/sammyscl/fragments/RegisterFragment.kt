package com.sammyscl.fragments

import android.app.Fragment
import android.app.FragmentTransaction
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sammyscl.R
import com.sammyscl.model.Response
import com.sammyscl.model.User
import com.sammyscl.network.NetworkUtil

import java.io.IOException

import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

import com.sammyscl.Helpers.Validation.validateEmail
import com.sammyscl.Helpers.Validation.validateFields

class RegisterFragment : Fragment() {

    private var mEtName: EditText? = null
    private var mSpUserType: Spinner? = null
    private var mEtEmail: EditText? = null
    private var mEtPassword: EditText? = null
    private var mBtRegister: Button? = null
    private var mTvLogin: TextView? = null
    private var mTiName: TextInputLayout? = null
    private var mTiEmail: TextInputLayout? = null
    private var mTiPassword: TextInputLayout? = null
    private var mProgressbar: ProgressBar? = null

    private var mSubscriptions: CompositeSubscription? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_register, container, false)
        mSubscriptions = CompositeSubscription()
        initViews(view)
        return view
    }

    private fun initViews(v: View) {

        mEtName = v.findViewById<View>(R.id.et_name) as EditText
        mSpUserType = v.findViewById<View>(R.id.sp_userType) as Spinner
        mEtEmail = v.findViewById<View>(R.id.et_email) as EditText
        mEtPassword = v.findViewById<View>(R.id.et_password) as EditText
        mBtRegister = v.findViewById<View>(R.id.btn_register) as Button
        mTvLogin = v.findViewById<View>(R.id.tv_login) as TextView
        mTiName = v.findViewById<View>(R.id.ti_name) as TextInputLayout
        mTiEmail = v.findViewById<View>(R.id.ti_email) as TextInputLayout
        mTiPassword = v.findViewById<View>(R.id.ti_password) as TextInputLayout
        mProgressbar = v.findViewById<View>(R.id.progress) as ProgressBar

        mBtRegister!!.setOnClickListener { view -> register() }
        mTvLogin!!.setOnClickListener { view -> goToLogin() }

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(v.context,
                R.array.userTypes, android.R.layout.simple_spinner_item)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        mSpUserType!!.adapter = adapter

        //        mSpUserType.setOnItemSelectedListener(this);

    }

    //    @Override
    //    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    //        // Don't do anything
    //    }
    //    public void onNothingSelected(AdapterView<?> arg0) {
    //        // Don't do anything
    //    }

    private fun register() {
        setError()

        val name = mEtName!!.text.toString()
        val userType = mSpUserType!!.selectedItem.toString()
        val email = mEtEmail!!.text.toString()
        val password = mEtPassword!!.text.toString()

        var err = 0

        if (!validateFields(name)) {

            err++
            mTiName!!.error = "Name should not be empty !"
        }

        if (!validateFields(userType)) {
            err++

        }

        if (!validateEmail(email)) {

            err++
            mTiEmail!!.error = "Email should be valid !"
        }

        if (!validateFields(password)) {

            err++
            mTiPassword!!.error = "Password should not be empty !"
        }

        if (err == 0) {

            val user = User()
            user.name = name
            user.userType = userType
            user.email = email
            user.setPassword(password)

            mProgressbar!!.visibility = View.VISIBLE
            registerProcess(user)

        } else {

            showSnackBarMessage("Enter Valid Details !")
        }
    }

    private fun setError() {
        mTiName!!.error = null
        mTiEmail!!.error = null
        mTiPassword!!.error = null
    }

    private fun registerProcess(user: User) {
        mSubscriptions!!.add(NetworkUtil.retrofit.register(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }

    private fun handleResponse(response: Response) {
        mProgressbar!!.visibility = View.GONE
        showSnackBarMessage(response.message)
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
        if (view != null) {
            Snackbar.make(view!!, message!!, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun goToLogin() {
        val ft = fragmentManager.beginTransaction()
        val fragment = LoginFragment()
        ft.replace(R.id.fragmentFrame, fragment, LoginFragment.TAG)
        ft.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        mSubscriptions!!.unsubscribe()
    }

    companion object {
        //    public class RegisterFragment extends Fragment implements AdapterView.OnItemSelectedListener{

        val TAG = RegisterFragment::class.java!!.getSimpleName()
    }
}

