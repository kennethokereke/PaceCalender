package com.sammyscl.fragments

import android.app.Fragment
import android.app.FragmentTransaction
import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sammyscl.Helpers.MySingleton
import com.sammyscl.R
import com.sammyscl.model.Response
import com.sammyscl.model.User

import java.io.IOException

import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

import com.sammyscl.Helpers.Validation.validateEmail
import com.sammyscl.Helpers.Validation.validateFields
import com.sammyscl.network.SessionHandler

import org.json.JSONObject
import org.json.JSONException

class RegisterFragment : Fragment() {
    private val KEY_STATUS = "status"
    private val KEY_MESSAGE = "message"
    private val KEY_FULL_NAME = "full_name"
    private val KEY_USERNAME = "email"
    private val KEY_PASSWORD = "password"
    private val KEY_EMPTY = ""
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
    private var pDialog: ProgressDialog? = null
    private val email : String? = null
    private val password : String? = null

    private val register_url = "http://10.0.0.2:8080/api/member/register.php"
    private var session = SessionHandler(this.context)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)
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
        mTvLogin!!.setOnClickListener { view -> goToLogin() }

        mBtRegister!!.setOnClickListener(
            fun onClick(v: View) {
                //Retrieve the data entered in the edit texts
                email = mEtEmail!!.getText().toString().toLowerCase().trim()
                password = mEtPassword!!.getText().toString().trim()
                if (validateInputs()) {
                    registerUser()
                }
            }
        )
    }

    fun displayLoader() {
        pDialog = ProgressDialog(this.context)
        pDialog!!.setMessage("Signing Up.. Please wait...")
        pDialog!!.setIndeterminate(false)
        pDialog!!.setCancelable(false)
        pDialog!!.show()
    }

    fun registerUser() {
        displayLoader()
        val request = JSONObject()
        try {
            //Populate the request parameters
            request.put(KEY_USERNAME, email)
            request.put(KEY_PASSWORD, password)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsArrayRequest = JsonObjectRequest(Request.Method.POST, register_url, request, Response.Listener<JSONObject> {
                    fun onResponse(response: JSONObject) {
                        pDialog.dismiss()
                        try {
                            //Check if user got registered successfully
                            if (response.getInt(KEY_STATUS) == 0) {
                                //Set the user session
                                session.loginUser(email)
                                goToLogin()

                            } else if(response.getInt(KEY_STATUS) == 1) {
                                //Display error message if email is already existing
                                mEtEmail.setError("Username already taken!")
                                mEtEmail.requestFocus()

                            } else {
                                Toast.makeText(context,
                                        response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show()
                            }
                        } catch (JSONException e) {
                            e.printStackTrace()
                        }
                    }
                }, Response.ErrorListener {
                    fun onErrorResponse(error: VolleyError) {
                        pDialog!!.dismiss()

                        //Display error message whenever an error occurs
                        Toast.makeText(context,
                                error.getMessage(), Toast.LENGTH_SHORT).show()

                    }
                })

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this.context).addToRequestQueue(jsArrayRequest)
    }

    fun validateInputs(): Boolean {
        if (KEY_EMPTY.equals(mEtEmail)) {
            mEtEmail!!.setError("Username cannot be empty")
            mEtEmail!!.requestFocus()
            return false
        }
        if (KEY_EMPTY.equals(mEtPassword)) {
            mEtPassword!!.setError("Password cannot be empty")
            mEtPassword!!.requestFocus()
            return false
        }

        return true
    }

    private fun goToLogin() {
        val ft = fragmentManager.beginTransaction()
        val fragment = LoginFragment()
        ft.replace(R.id.fragmentFrame, fragment, LoginFragment.TAG)
        ft.commit()
    }

    companion object {
        val TAG = RegisterFragment::class.java.getSimpleName()
    }
}

