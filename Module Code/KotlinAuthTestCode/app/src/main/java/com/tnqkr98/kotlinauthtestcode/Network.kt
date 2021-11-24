package com.tnqkr98.kotlinauthtestcode

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Network {
    @GET("/api/sms/verify")
    fun requestAuthMsg(
        @Query("phoneNumber") phoneNumber:String,
        @Query("hashCode") hashCode:String) : Call<Result>

    @GET("/api/sms/confrim")
    fun requestAuthConfirm(
        @Query("phoneNumber") phoneNumber:String,
        @Query("code") code:String) : Call<Result>

    @GET("/api/{id}/confrim")
    fun testRequest(
        @Path("id") pathId:String,
        @Query("code") code:String) : Call<Result>

}