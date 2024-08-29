package com.example.motus.network

import retrofit2.http.GET

fun interface WordApi {
    @GET("iys4katchh")
    suspend fun getListOfWords(): String
}