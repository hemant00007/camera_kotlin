package com.example.check_camera


import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiInterface {
    @Multipart
    @POST("post")
    fun getpost(
        @Header("token") token : String,
        @Part("name") name: RequestBody,
        @Part("mobile") mobile : RequestBody,
        @Part("email") emailid : RequestBody,
        @Part image : MultipartBody.Part,
        @Part("created_date") datee : RequestBody
    ): Call<Mymodel>

    companion object {

        var BASE_URL = "http://172.20.3.87/daily_visit/test/"



            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
           // return retrofit.create(ApiInterface::class.java)


    }
}