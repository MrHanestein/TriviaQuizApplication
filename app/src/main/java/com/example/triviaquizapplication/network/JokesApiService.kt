package com.example.triviaquizapplication.network

import com.example.triviaquizapplication.models.JokeResponse
import retrofit2.Response
import retrofit2.http.GET

interface JokesApiService {
    @GET("joke/Any")
    suspend fun getRandomJoke(): Response<JokeResponse>
}
