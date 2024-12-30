package com.example.triviaquizapplication.models

import com.google.gson.annotations.SerializedName

data class TriviaResponse(
    @SerializedName("response_code") val responseCode: Int,
    val results: List<Question>
)


data class Question(
    @SerializedName("category") val category: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("difficulty") val difficulty: String?,
    @SerializedName("question") val question: String?,
    @SerializedName("correct_answer") val correctAnswer: String?,
    @SerializedName("incorrect_answers") val incorrectAnswers: List<String>?
)