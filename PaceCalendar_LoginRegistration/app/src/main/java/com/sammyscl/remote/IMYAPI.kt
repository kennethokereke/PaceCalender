package com.sammyscl.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IMYAPI {
    @FormUrlEncoded
     @POST("register.php")
    fun registerUser(@Field("username") username:String, @Field("fullname") fullname:String, @Field("password") password:String) :Call<okhttp3.Response>

    @FormUrlEncoded
    @POST("login.php")
    fun LoginUser(@Field("username") username:String, @Field("password") password:String) :Call<okhttp3.Response>
}