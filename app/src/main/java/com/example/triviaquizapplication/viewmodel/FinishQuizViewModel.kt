package com.example.triviaquizapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triviaquizapplication.models.JokeResponse
import com.example.triviaquizapplication.repository.JokesRepository
import kotlinx.coroutines.launch

class FinishQuizViewModel : ViewModel() {
    private val _joke = MutableLiveData<String>()
    val joke: LiveData<String> get() = _joke

    fun getJoke() {
        viewModelScope.launch {
            val response = JokesRepository().fetchRandomJoke()
            if (response.isSuccessful) {
                val jokeResponse = response.body()
                val jokeText = when {
                    jokeResponse?.setup != null && jokeResponse.delivery != null ->
                        "${jokeResponse.setup} ${jokeResponse.delivery}"
                    jokeResponse?.joke != null -> jokeResponse.joke
                    else -> "Couldn't fetch a joke. Try again later!"
                }
                _joke.value = jokeText
            } else {
                _joke.value = "Error fetching joke: ${response.message()}"
            }
        }
    }
}
