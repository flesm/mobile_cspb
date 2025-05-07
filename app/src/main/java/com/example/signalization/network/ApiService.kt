package com.example.signalization.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import com.example.signalization.data.UnauthAccess
import com.example.signalization.data.MarkAsDecidedRequest
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("active-warning/")
    fun getAllRecords(): Call<List<UnauthAccess>>

    @GET("active-warning/last-undecided/")
    fun getLastUndecided(): Call<UnauthAccess>

    @POST("active-warning/mark-decided/")
    fun markAsDecided(@Body request: MarkAsDecidedRequest): Call<Map<String, String>>

    @Multipart
    @POST("upload-people-current-image/")
    fun uploadPeopleImage(
        @Part image: MultipartBody.Part,
        @Part("distance") distance: RequestBody
    ): Call<ResponseBody>

    @Multipart
    @POST("upload-stuff-image/")
    fun uploadStuffImage(
        @Part image: MultipartBody.Part,
        @Part("full_name") fullName: RequestBody
    ): Call<ResponseBody>

}
