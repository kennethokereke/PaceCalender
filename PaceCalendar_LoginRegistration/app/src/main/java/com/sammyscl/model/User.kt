package com.sammyscl.model

class User {

    var name: String? = null
    var userType: String? = null
    var email: String? = null
    private var password: String? = null
    val created_at: String? = null
    private var newPassword: String? = null
    private var token: String? = null

    fun setPassword(password: String) {
        this.password = password
    }

    fun setNewPassword(newPassword: String) {
        this.newPassword = newPassword
    }

    fun setToken(token: String) {
        this.token = token
    }
}
