//package com.sammyscl.fragments
//
//import android.app.DialogFragment
//import android.content.Context
//import android.os.Bundle
//import android.support.design.widget.TextInputLayout
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ProgressBar
//import android.widget.TextView
//
//import com.google.gson.Gson
//import com.google.gson.GsonBuilder
//import com.sammyscl.ProfileActivity
//import com.sammyscl.R
//import com.sammyscl.model.Response
//import com.sammyscl.model.User
//import com.sammyscl.network.NetworkUtil
//import com.sammyscl.Helpers.Constants
//
//import java.io.IOException
//
//import retrofit2.adapter.rxjava.HttpException
//import rx.android.schedulers.AndroidSchedulers
//import rx.schedulers.Schedulers
//import rx.subscriptions.CompositeSubscription
//
//import com.sammyscl.Helpers.Validation.validateFields
//
//class ChangePasswordDialog : DialogFragment() {
//
//    private var mEtOldPassword: EditText? = null
//    private var mEtNewPassword: EditText? = null
//    private var mBtChangePassword: Button? = null
//    private var mBtCancel: Button? = null
//    private var mTvMessage: TextView? = null
//    private var mTiOldPassword: TextInputLayout? = null
//    private var mTiNewPassword: TextInputLayout? = null
//    private var mProgressBar: ProgressBar? = null
//
//    private var mSubscriptions: CompositeSubscription? = null
//
//    private var mToken: String? = null
//    private var mEmail: String? = null
//
//    private var mListener: Listener? = null
//
//    interface Listener {
//
//        fun onPasswordChanged()
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.dialog_change_password, container, false)
//        mSubscriptions = CompositeSubscription()
//        getData()
//        initViews(view)
//        return view
//    }
//
//    private fun getData() {
//        val bundle = arguments
//
//        mToken = bundle.getString(Constants.TOKEN)
//        mEmail = bundle.getString(Constants.EMAIL)
//    }
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        mListener = context as ProfileActivity
//    }
//
//    private fun initViews(v: View) {
//        mEtOldPassword = v.findViewById<View>(R.id.et_old_password) as EditText
//        mEtNewPassword = v.findViewById<View>(R.id.et_new_password) as EditText
//        mTiOldPassword = v.findViewById<View>(R.id.ti_old_password) as TextInputLayout
//        mTiNewPassword = v.findViewById<View>(R.id.ti_new_password) as TextInputLayout
//        mTvMessage = v.findViewById<View>(R.id.tv_message) as TextView
//        mBtChangePassword = v.findViewById<View>(R.id.btn_change_password) as Button
//        mBtCancel = v.findViewById<View>(R.id.btn_cancel) as Button
//        mProgressBar = v.findViewById<View>(R.id.progress) as ProgressBar
//
//        mBtChangePassword!!.setOnClickListener { view -> changePassword() }
//        mBtCancel!!.setOnClickListener { view -> dismiss() }
//    }
//
//    private fun changePassword() {
//        setError()
//
//        val oldPassword = mEtOldPassword!!.text.toString()
//        val newPassword = mEtNewPassword!!.text.toString()
//
//        var err = 0
//
//        if (!validateFields(oldPassword)) {
//            err++
//            mTiOldPassword!!.error = "Password should not be empty !"
//        }
//
//        if (!validateFields(newPassword)) {
//            err++
//            mTiNewPassword!!.error = "Password should not be empty !"
//        }
//
//        if (err == 0) {
//            val user = User()
//            user.setPassword(oldPassword)
//            user.setNewPassword(newPassword)
//            changePasswordProgress(user)
//            mProgressBar!!.visibility = View.VISIBLE
//
//        }
//    }
//
//    private fun setError() {
//        mTiOldPassword!!.error = null
//        mTiNewPassword!!.error = null
//    }
//
//    private fun changePasswordProgress(user: User) {
//        mSubscriptions!!.add(NetworkUtil.getRetrofit(mToken as String).changePassword(mEmail as String, user)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleResponse, this::handleError))
//    }
//
//    private fun handleResponse(response: Response) {
//        mProgressBar!!.visibility = View.GONE
//        mListener!!.onPasswordChanged()
//        dismiss()
//    }
//
//    private fun handleError(error: Throwable) {
//        mProgressBar!!.visibility = View.GONE
//
//        if (error is HttpException) {
//
//            val gson = GsonBuilder().create()
//
//            try {
//
//                val errorBody = error.response().errorBody().string()
//                val response = gson.fromJson<Response>(errorBody, Response::class.java!!)
//                showMessage(response.message)
//
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//
//        } else {
//
//            showMessage("Network Error !")
//        }
//    }
//
//    private fun showMessage(message: String?) {
//
//        mTvMessage!!.visibility = View.VISIBLE
//        mTvMessage!!.text = message
//
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mSubscriptions!!.unsubscribe()
//    }
//
//    companion object {
//
//        val TAG = ChangePasswordDialog::class.java!!.getSimpleName()
//    }
//}
