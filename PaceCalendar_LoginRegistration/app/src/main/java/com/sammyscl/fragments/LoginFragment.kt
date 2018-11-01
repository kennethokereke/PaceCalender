package com.sammyscl.fragments

import android.app.Fragment
import android.app.FragmentTransaction
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sammyscl.helpers.SaveSharedPreference
import com.sammyscl.R
import com.sammyscl.helpers.Constants
import java.io.IOException

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.sammyscl.helpers.MySingleton

import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

import com.sammyscl.helpers.Validation
import com.sammyscl.calendar.activities.SplashActivity
import com.sammyscl.network.SessionHandler

import org.json.JSONException
import org.json.JSONObject


class LoginFragment : Fragment() {
    private val KEY_STATUS = "status"
    private val KEY_MESSAGE = "message"
    private val KEY_USERNAME = "username"
    private val KEY_PASSWORD = "password"
    private val KEY_EMPTY = ""

    private var mEtEmail: EditText? = null
    private var mEtPassword: EditText? = null
    private var mBtLogin: Button? = null
    private var mTvRegister: TextView? = null
    private var mTvForgotPassword: TextView? = null
    private var mTiEmail: TextInputLayout? = null
    private var mTiPassword: TextInputLayout? = null
    private var mProgressBar: ProgressBar? = null
    private var pDialog: ProgressDialog? = null

    private val username: String? = null
    private val password: String? = null

    private var login_url : String = "http://192.168.43.72:8888/api/member/login.php"
    private var session = SessionHandler(this.context)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        initViews(view)
        return view
    }

    private fun initViews(v: View) {
        mEtEmail = v.findViewById<View>(R.id.et_email) as EditText
        mTiEmail = v.findViewById<View>(R.id.ti_email) as TextInputLayout
        mEtPassword = v.findViewById<View>(R.id.et_password) as EditText
        mTiPassword = v.findViewById<View>(R.id.ti_password) as TextInputLayout

        mBtLogin = v.findViewById<View>(R.id.btn_login) as Button
        mProgressBar = v.findViewById<View>(R.id.progress) as ProgressBar
        mTvRegister = v.findViewById<View>(R.id.tv_register) as TextView
        mTvForgotPassword = v.findViewById<View>(R.id.tv_forgot_password) as TextView

        mBtLogin!!.setOnClickListener { view -> login() }
        mTvRegister!!.setOnClickListener { view -> goToRegister() }
//        mTvForgotPassword!!.setOnClickListener { view -> showDialog() }
    }

    private fun displayLoader() {
        pDialog = ProgressDialog(this.context)
        pDialog!!.setMessage("Logging In.. Please wait...")
        pDialog!!.setIndeterminate(false)
        pDialog!!.setCancelable(false)
        pDialog!!.show()
    }

    private fun login() {
        displayLoader()
        val request = JSONObject()

        try {
            //Populate the request parameters
            request.put(KEY_USERNAME, username)
            request.put(KEY_PASSWORD, password)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val jsArrayRequest = JsonObjectRequest(Request.Method.POST, login_url, request,
            Response.Listener<JSONObject> { response ->
                pDialog!!.dismiss()
                try {
                    //Check if user got logged in successfully
                    if (response.getInt(KEY_STATUS) == 0) {
                        session.loginUser(username!!)
                        goToRegister()
                    } else {
                        Toast.makeText(this.context, response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                pDialog!!.dismiss()

                //Display error message whenever an error occurs
                Toast.makeText(this.context, error.toString(), Toast.LENGTH_SHORT).show()
            })

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this.context).addToRequestQueue(jsArrayRequest)
    }

    private fun validateInputs() : Boolean {
        if(KEY_EMPTY.equals(username)){
            mEtEmail!!.setError("Username cannot be empty")
            mEtEmail!!.requestFocus()
            return false
        }

        if(KEY_EMPTY.equals(password)){
            mEtPassword!!.setError("Password cannot be empty")
            mEtPassword!!.requestFocus()
            return false
        }
        return true
    }

    private fun goToRegister() {
        val ft = fragmentManager.beginTransaction()
        val fragment = RegisterFragment()
        ft.replace(R.id.fragmentFrame, fragment, RegisterFragment.TAG)
        ft.commit()
    }
//
//    private fun showDialog() {
//        val fragment = ResetPasswordDialog()
//        fragment.show(fragmentManager, ResetPasswordDialog.TAG)
//    }

    companion object {
        val TAG = LoginFragment::class.java!!.getSimpleName()
    }
}
