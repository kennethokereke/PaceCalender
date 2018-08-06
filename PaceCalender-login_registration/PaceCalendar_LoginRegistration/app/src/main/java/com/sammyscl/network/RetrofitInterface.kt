package com.sammyscl.network

import com.sammyscl.model.Response
import com.sammyscl.model.User

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import rx.Observable

interface RetrofitInterface {

    @POST("users")
    fun register(@Body user: User): Observable<Response>

    @POST("authenticate")
    fun login(): Observable<Response>

    @GET("users/{email}")
    fun getProfile(@Path("email") email: String): Observable<User>

    @PUT("users/{email}")
    fun changePassword(@Path("email") email: String, @Body user: User): Observable<Response>

    @POST("users/{email}/password")
    fun resetPasswordInit(@Path("email") email: String): Observable<Response>

    @POST("users/{email}/password")
    fun resetPasswordFinish(@Path("email") email: String, @Body user: User): Observable<Response>
}
