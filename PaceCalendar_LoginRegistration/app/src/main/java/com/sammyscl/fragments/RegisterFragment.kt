package com.sammyscl.fragments

import android.app.Fragment
import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest

import com.sammyscl.helpers.MySingleton
import com.sammyscl.R

import com.sammyscl.network.SessionHandler

import org.json.JSONObject
import org.json.JSONException

class RegisterFragment : Fragment() {
    private val KEY_STATUS = "status"
    private val KEY_MESSAGE = "message"
    private val KEY_FULL_NAME = "full_name"
    private val KEY_USERNAME = "email"
    private val KEY_PASSWORD = "password"
    private val KEY_USERTYPE = "user_type"
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
    private var fullName : String? = null
    private var email : String? = null
    private var password : String? = null
    private var userType : String? = null

    private val register_url = "http://10.0.2.2:8888/member/register.php"
    private var session : SessionHandler ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        session = SessionHandler(this.context)
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

        mBtRegister!!.setOnClickListener {
            //Retrieve the data entered in the edit texts
            fullName = mEtName!!.getText().toString()
            userType = mSpUserType!!.selectedItem.toString()
            email = mEtEmail!!.getText().toString().toLowerCase().trim()
            password = mEtPassword!!.getText().toString().trim()
            if (validateInputs()) {
                registerUser()
            }
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(v.context,
                R.array.userTypes, android.R.layout.simple_spinner_item)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        mSpUserType!!.adapter = adapter
    }

    private fun displayLoader() {
        pDialog = ProgressDialog(this.context)
        pDialog!!.setMessage("Signing Up.. Please wait...")
        pDialog!!.setIndeterminate(false)
        pDialog!!.setCancelable(false)
        pDialog!!.show()
    }

    private fun registerUser() {
        displayLoader()
        val request = JSONObject()

        try {
            //Populate the request parameters
            request.put(KEY_FULL_NAME, fullName)
            request.put(KEY_USERTYPE, userType)
            request.put(KEY_USERNAME, email)
            request.put(KEY_PASSWORD, password)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val jsArrayRequest = JsonObjectRequest(Request.Method.POST, register_url, request,
            Response.Listener<JSONObject> { response ->
                pDialog!!.dismiss()
                try {
                    //Check if user got registered successfully
                    if (response.getInt(KEY_STATUS) == 0) {
                        goToLogin()
                    } else if(response.getInt(KEY_STATUS) == 1) {
                        //Display error message if email is already existing
                        mEtEmail!!.setError("Username already taken!")
                        mEtEmail!!.requestFocus()
                    } else {
                        Toast.makeText(this.context, response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e : JSONException) {
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

    private fun validateInputs(): Boolean {
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

        if(KEY_EMPTY.equals(mEtName)) {
            mEtName!!.setError("Full name cannot be empty")
            mEtName!!.requestFocus()
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

