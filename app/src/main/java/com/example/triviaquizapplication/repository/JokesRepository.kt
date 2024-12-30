package com.example.triviaquizapplication.repository

import com.example.triviaquizapplication.models.JokeResponse
import com.example.triviaquizapplication.network.JokesRetrofitInstance
import retrofit2.Response

class JokesRepository {
    suspend fun fetchRandomJoke(): Response<JokeResponse> {
        return JokesRetrofitInstance.api.getRandomJoke()
    }
}
