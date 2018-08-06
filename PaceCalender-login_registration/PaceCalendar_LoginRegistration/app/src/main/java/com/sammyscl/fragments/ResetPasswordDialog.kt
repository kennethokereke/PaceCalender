package com.sammyscl.fragments

import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
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
import com.sammyscl.MainActivity
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

class ResetPasswordDialog : DialogFragment() {

    private var mEtEmail: EditText? = null
    private var mEtToken: EditText? = null
    private var mEtPassword: EditText? = null
    private var mBtResetPassword: Button? = null
    private var mTvMessage: TextView? = null
    private var mTiEmail: TextInputLayout? = null
    private var mTiToken: TextInputLayout? = null
    private var mTiPassword: TextInputLayout? = null
    private var mProgressBar: ProgressBar? = null

    private var mSubscriptions: CompositeSubscription? = null

    private var mEmail: String? = null

    private var isInit = true

    private var mListener: Listener? = null

    interface Listener {
        fun onPasswordReset(message: String?)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_reset_password, container, false)
        mSubscriptions = CompositeSubscription()
        initViews(view)
        return view
    }

    private fun initViews(v: View) {
        mEtEmail = v.findViewById<View>(R.id.et_email) as EditText
        mEtToken = v.findViewById<View>(R.id.et_token) as EditText
        mEtPassword = v.findViewById<View>(R.id.et_password) as EditText
        mBtResetPassword = v.findViewById<View>(R.id.btn_reset_password) as Button
        mProgressBar = v.findViewById<View>(R.id.progress) as ProgressBar
        mTvMessage = v.findViewById<View>(R.id.tv_message) as TextView
        mTiEmail = v.findViewById<View>(R.id.ti_email) as TextInputLayout
        mTiToken = v.findViewById<View>(R.id.ti_token) as TextInputLayout
        mTiPassword = v.findViewById<View>(R.id.ti_password) as TextInputLayout

        mBtResetPassword!!.setOnClickListener { view ->
            if (isInit)
                resetPasswordInit()
            else
                resetPasswordFinish()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as MainActivity
    }

    private fun setEmptyFields() {
        mTiEmail!!.error = null
        mTiToken!!.error = null
        mTiPassword!!.error = null
        mTvMessage!!.text = null
    }

    fun setToken(token: String) {
        mEtToken!!.setText(token)
    }

    private fun resetPasswordInit() {
        setEmptyFields()

        mEmail = mEtEmail!!.text.toString()

        var err = 0

        if (!validateEmail(mEmail as String)) {
            err++
            mTiEmail!!.error = "Email Should be Valid !"
        }

        if (err == 0) {

            mProgressBar!!.visibility = View.VISIBLE
            resetPasswordInitProgress(mEmail as String)
        }
    }

    private fun resetPasswordFinish() {
        setEmptyFields()

        val token = mEtToken!!.text.toString()
        val password = mEtPassword!!.text.toString()

        var err = 0

        if (!validateFields(token)) {
            err++
            mTiToken!!.error = "Token Should not be empty !"
        }

        if (!validateFields(password)) {
            err++
            mTiEmail!!.error = "Password Should not be empty !"
        }

        if (err == 0) {
            mProgressBar!!.visibility = View.VISIBLE

            val user = User()
            user.setPassword(password)
            user.setToken(token)
            resetPasswordFinishProgress(user)
        }
    }

    private fun resetPasswordInitProgress(email: String) {
        mSubscriptions!!.add(NetworkUtil.retrofit.resetPasswordInit(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }

    private fun resetPasswordFinishProgress(user: User) {
        mSubscriptions!!.add(NetworkUtil.retrofit.resetPasswordFinish(mEmail as String, user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }

    private fun handleResponse(response: Response) {
        mProgressBar!!.visibility = View.GONE
        if (isInit) {
            isInit = false
            showMessage(response.message)
            mTiEmail!!.visibility = View.GONE
            mTiToken!!.visibility = View.VISIBLE
            mTiPassword!!.visibility = View.VISIBLE
        } else {
            mListener!!.onPasswordReset(response.message)
            dismiss()
        }
    }

    private fun handleError(error: Throwable) {
        mProgressBar!!.visibility = View.GONE
        if (error is HttpException) {
            val gson = GsonBuilder().create()
            try {

                val errorBody = error.response().errorBody().string()
                val response = gson.fromJson<Response>(errorBody, Response::class.java!!)
                showMessage(response.message)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else {
            showMessage("Network Error !")
        }
    }

    private fun showMessage(message: String?) {
        mTvMessage!!.visibility = View.VISIBLE
        mTvMessage!!.text = message
    }

    override fun onDestroy() {
        super.onDestroy()
        mSubscriptions!!.unsubscribe()
    }

    companion object {

        val TAG = ResetPasswordDialog::class.java!!.getSimpleName()
    }
}
