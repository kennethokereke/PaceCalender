package com.sammyscl.fragments

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.sammyscl.R

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.sammyscl.Helpers.MySingleton
import com.sammyscl.calendar.activities.SplashActivity

import com.sammyscl.network.SessionHandler

import org.json.JSONException
import org.json.JSONObject


class LoginFragment : Fragment() {
    private val KEY_STATUS = "status"
    private val KEY_MESSAGE = "message"
    private val KEY_EMAIL = "email"
    private val KEY_PASSWORD = "password"
    private val KEY_EMPTY = ""

    private var mEtEmail: EditText? = null
    private var mEtPassword: EditText? = null
    private var mBtLogin: Button? = null
    private var mTvRegister: TextView? = null
    private var mTvForgotPassword: TextView? = null
    private var mTiEmail: TextInputLayout? = null
    private var mTiPassword: TextInputLayout? = null

    private var email: String? = null
    private var password: String? = null

    private var login_url: String = "http://10.0.2.2:8888/member/login.php"
    private var session: SessionHandler ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        session = SessionHandler(this.context)
        initViews(view)
        return view
    }

    private fun initViews(v: View) {
        mEtEmail = v.findViewById<View>(R.id.et_email) as EditText
        mTiEmail = v.findViewById<View>(R.id.ti_email) as TextInputLayout
        mEtPassword = v.findViewById<View>(R.id.et_password) as EditText
        mTiPassword = v.findViewById<View>(R.id.ti_password) as TextInputLayout

        mBtLogin = v.findViewById<View>(R.id.btn_login) as Button
        mTvRegister = v.findViewById<View>(R.id.tv_register) as TextView
        mTvForgotPassword = v.findViewById<View>(R.id.tv_forgot_password) as TextView

        mBtLogin!!.setOnClickListener {
            email = mEtEmail!!.getText().toString().toLowerCase().trim()
            password = mEtPassword!!.getText().toString().trim()
            if(validateInputs()) {
                login()
            }
        }
        mTvRegister!!.setOnClickListener { goToRegister() }
//        mTvForgotPassword!!.setOnClickListener { view -> showDialog() }
    }

    private fun login() {
        val request = JSONObject()

        try {
            //Populate the request parameters
            request.put(KEY_EMAIL, email)
            request.put(KEY_PASSWORD, password)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val jsArrayRequest = JsonObjectRequest(Request.Method.POST, login_url, request,
            Response.Listener<JSONObject> { response ->
                try {
                    //Check if user got logged in successfully
                    if (response.getInt(KEY_STATUS) == 0) {
                        session!!.loginUser(email!!)
                        startActivity(Intent(activity, SplashActivity::class.java))
                    } else {
                        Toast.makeText(this.context, response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                //Display error message whenever an error occurs
                Toast.makeText(this.context, error.toString(), Toast.LENGTH_SHORT).show()
            })

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this.context).addToRequestQueue(jsArrayRequest)
    }

    private fun validateInputs() : Boolean {
        if(KEY_EMPTY.equals(email)){
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

    companion object {
        val TAG = LoginFragment::class.java!!.getSimpleName()
    }
}
