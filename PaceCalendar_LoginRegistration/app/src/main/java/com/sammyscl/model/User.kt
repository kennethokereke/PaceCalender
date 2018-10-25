package com.sammyscl.model

import java.util.Date

/**
 * Created by Abhi on 20 Jan 2018 020.
 */

class User {
    var email: String? = null
        get() = this.email
        set(value) {
            email = value
        }

    var sessionExpiryDate: Date? = null
        get() = this.sessionExpiryDate
        set(value) {
            this.sessionExpiryDate = value
    }
}