package com.example.triviaquizapplication.network

import com.example.triviaquizapplication.models.TriviaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApiService {
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int,
        @Query("category") category: Int? = null,
        @Query("type") type: String? = null
    ): Response<TriviaResponse>
}
