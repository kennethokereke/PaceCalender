package com.sammyscl

import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import com.sammyscl.helpers.SaveSharedPreference
import com.sammyscl.calendar.activities.SplashActivity
import com.sammyscl.fragments.LoginFragment
//import com.sammyscl.fragments.ResetPasswordDialog

//class MainActivity : AppCompatActivity(), ResetPasswordDialog.Listener {
class MainActivity : AppCompatActivity() {

    private var mLoginFragment: LoginFragment? = null
//    private var mResetPasswordDialog: ResetPasswordDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (SaveSharedPreference.getUserName(this@MainActivity).isEmpty()) {
            if (savedInstanceState == null) {
                loadLoginFragment()
            }
        } else {
            val intent = Intent(applicationContext, SplashActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadLoginFragment() {
        if (mLoginFragment == null) {
            mLoginFragment = LoginFragment()
        }

        fragmentManager.beginTransaction().replace(R.id.fragmentFrame, mLoginFragment, LoginFragment.TAG).commit()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val data = intent.data!!.lastPathSegment
        Log.d(TAG, "onNewIntent: $data")

//        mResetPasswordDialog = fragmentManager.findFragmentByTag(ResetPasswordDialog.TAG) as ResetPasswordDialog
//
//        if (mResetPasswordDialog != null)
//            mResetPasswordDialog!!.setToken(data)
    }

//    override fun onPasswordReset(message: String?) {
//        showSnackBarMessage(message)
//    }

    private fun showSnackBarMessage(message: String?) {
        Snackbar.make(findViewById<View>(R.id.activity_main), message!!, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        val TAG = MainActivity::class.java.getSimpleName()
    }
}
