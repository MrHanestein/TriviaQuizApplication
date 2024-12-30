package com.example.triviaquizapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _settingsSaved = MutableLiveData<Boolean>()
    val settingsSaved: LiveData<Boolean> = _settingsSaved

    private val _userSettings = MutableLiveData<Map<String, Any?>>()
    val userSettings: LiveData<Map<String, Any?>> = _userSettings

    private val _numberOfQuestions = MutableLiveData<Int>()
    val numberOfQuestions: LiveData<Int> get() = _numberOfQuestions

    fun updateNumberOfQuestions(newValue: Int) {
        _numberOfQuestions.value = newValue
    }

    /**
     * Save user settings such as question type, category, number of questions, and notification time.
     */
    fun saveSettings(
        type: String?,
        category: Int?,
        numberOfQuestions: Int,
        notificationTime: String
    ) {
        val userId = auth.currentUser?.uid ?: return

        val settings = hashMapOf(
            "type" to type,
            "category" to category,
            "numberOfQuestions" to numberOfQuestions,
            "notificationTime" to notificationTime
        )

        db.collection("users").document(userId)
            .set(settings, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                _settingsSaved.value = true
            }
            .addOnFailureListener {
                _settingsSaved.value = false
            }
    }

    /**
     * Load user settings from Firestore. If no user is signed in or loading fails, this returns an emptyMap.
     */
    fun loadUserSettings() {
        val userId = auth.currentUser?.uid ?: run {
            _userSettings.value = emptyMap()
            return
        }

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                _userSettings.value = document.data ?: emptyMap()
            }
            .addOnFailureListener {
                _userSettings.value = emptyMap()
            }
    }
}
