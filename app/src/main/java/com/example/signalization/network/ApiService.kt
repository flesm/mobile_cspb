package com.example.signalization.network

import com.example.signalization.data.UnauthAccess
import com.example.signalization.data.MarkAsDecidedRequest
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("active-warning/all-records/")
    fun getAllRecords(): Call<List<UnauthAccess>>

    @GET("active-warning/last-undecided/")
    fun getLastUndecided(): Call<UnauthAccess>

    @POST("active-warning/mark-decided/")
    fun markAsDecided(@Body request: MarkAsDecidedRequest): Call<Map<String, String>>

}
