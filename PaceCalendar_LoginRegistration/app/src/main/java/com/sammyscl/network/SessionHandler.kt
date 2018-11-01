package com.sammyscl.network

import android.content.Context
import android.content.SharedPreferences
import com.sammyscl.model.User

import java.util.Date

/**
 * Created by Abhi on 20 Jan 2018 020.
 */

class SessionHandler(private val mContext: Context) {
    private val mEditor: SharedPreferences.Editor
    private val mPreferences: SharedPreferences

    /**
     * Checks whether user is logged in
     *
     * @return
     */
    /* If shared preferences does not have a value
         then user is not logged in
         *//* Check if session is expired by comparing
        current date and Session expiry date
        */
    val isLoggedIn: Boolean
        get() {
            val currentDate = Date()

            val millis = mPreferences.getLong(KEY_EXPIRES, 0)
            if (millis == 0L) {
                return false
            }
            val expiryDate = Date(millis)
            return currentDate.before(expiryDate)
        }

    /**
     * Fetches and returns user details
     *
     * @return user details
     */
    // Check if user is logged in first
    val userDetails: User?
        get() {
            if (!isLoggedIn) {
                return null
            }
            val user = User()
            user.email = mPreferences.getString(KEY_USERNAME, KEY_EMPTY)
            user.sessionExpiryDate = Date(mPreferences.getLong(KEY_EXPIRES, 0))

            return user
        }

    init {
        mPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        this.mEditor = mPreferences.edit()
    }

    /**
     * Logs in the user by saving user details and setting session
     *
     * @param username
     * @param fullName
     */
    fun loginUser(email: String) {
        mEditor.putString(KEY_USERNAME, email)
        val date = Date()

        //Set user session for next 7 days
        val millis = date.time + 7 * 24 * 60 * 60 * 1000
        mEditor.putLong(KEY_EXPIRES, millis)
        mEditor.commit()
    }

    /**
     * Logs out user by clearing the session
     */
    fun logoutUser() {
        mEditor.clear()
        mEditor.commit()
    }

    companion object {
        private val PREF_NAME = "UserSession"
        private val KEY_USERNAME = "username"
        private val KEY_EXPIRES = "expires"
        private val KEY_FULL_NAME = "full_name"
        private val KEY_EMPTY = ""
    }

}