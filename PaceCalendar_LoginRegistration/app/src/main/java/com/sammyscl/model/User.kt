package com.sammyscl.model

import java.util.Date

/**
 * Created by Abhi on 20 Jan 2018 020.
 */

class User {
    var username: String
    var fullName: String
    var sessionExpiryDate: Date

    fun setUsername(username: String) {
        this.username = username
    }

    fun setFullName(fullName: String) {
        this.fullName = fullName
    }

    fun setSessionExpiryDate(sessionExpiryDate: Date) {
        this.sessionExpiryDate = sessionExpiryDate
    }

    fun getUsername(): String {
        return username
    }

    fun getFullName(): String {
        return fullName
    }

    fun getSessionExpiryDate(): Date {
        return sessionExpiryDate
    }
}