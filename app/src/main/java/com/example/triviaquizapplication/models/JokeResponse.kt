package com.example.triviaquizapplication.models

data class JokeResponse(
    val setup: String?, // For jokes with setup and delivery
    val delivery: String?,
    val joke: String? // For single-line jokes
)
