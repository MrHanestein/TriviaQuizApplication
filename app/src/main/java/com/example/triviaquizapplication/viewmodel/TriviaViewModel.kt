package com.example.triviaquizapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triviaquizapplication.models.Question
import com.example.triviaquizapplication.models.TriviaResponse
import com.example.triviaquizapplication.repository.TriviaRepository
import kotlinx.coroutines.launch

class TriviaViewModel : ViewModel() {
    private val repository = TriviaRepository()

    val questionsLiveData = MutableLiveData<List<Question>>()
    val errorLiveData = MutableLiveData<String>()

    fun getQuestions(amount: Int, category: Int?, type: String?) {
        viewModelScope.launch {
            try {
                val response = repository.fetchQuestions(amount, category, type)
                if (response.isSuccessful && response.body() != null) {
                    val triviaResponse = response.body() as TriviaResponse
                    questionsLiveData.postValue(triviaResponse.results)
                } else {
                    errorLiveData.postValue("Error fetching questions: ${response.code()}")
                }
            } catch (e: Exception) {
                errorLiveData.postValue("Exception: ${e.message}")
            }
        }
    }
}
