package com.basis.net

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.*

interface BaseApiService {

    @POST
    fun post(@Url url: String, @retrofit2.http.Body objects: Any): Call<JsonElement>

    @FormUrlEncoded
    @POST("/sys/user/login")
    fun login(@Field("username") username:String, @Field("password")password:String): Call<JsonElement>

}