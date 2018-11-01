package com.sammyscl.Common

import com.sammyscl.remote.IMYAPI
import com.sammyscl.remote.RetrofitClient

object Common {


    //Localhost according to android emulator was changed to 10.0.2.2
    val BASE_URL = ""

    val api: IMYAPI
            get() = RetrofitClient.getClient(BASE_URL).create(IMYAPI::class.java)

}