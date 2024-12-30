package com.example.triviaquizapplication.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object JokesRetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://v2.jokeapi.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: JokesApiService by lazy {
        retrofit.create(JokesApiService::class.java)
    }
}

