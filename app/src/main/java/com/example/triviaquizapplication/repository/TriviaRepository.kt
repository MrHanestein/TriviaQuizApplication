package com.example.triviaquizapplication.repository

import com.example.triviaquizapplication.models.TriviaResponse
import com.example.triviaquizapplication.network.RetrofitInstance
import retrofit2.Response

class TriviaRepository {
    suspend fun fetchQuestions(amount: Int, category: Int?, type: String?): Response<TriviaResponse> {
        return RetrofitInstance.api.getQuestions(amount, category, type)
    }
}
