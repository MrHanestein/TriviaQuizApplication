package com.example.triviaquizapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.triviaquizapplication.Adapters.LeaderboardEntry
import com.google.firebase.firestore.FirebaseFirestore

class LeaderboardViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _leaderboardLiveData = MutableLiveData<List<LeaderboardEntry>>()
    val leaderboardLiveData: LiveData<List<LeaderboardEntry>> = _leaderboardLiveData

    fun fetchLeaderboard() {
        db.collection("users")
            .orderBy("score", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                val leaderboard = mutableListOf<LeaderboardEntry>()
                for (document in documents) {
                    val username = document.getString("username") ?: "Anonymous"
                    val score = document.getLong("score")?.toInt() ?: 0
                    leaderboard.add(LeaderboardEntry(username, score))
                }
                _leaderboardLiveData.value = leaderboard
            }
            .addOnFailureListener {
                _leaderboardLiveData.value = listOf()
            }
    }
}
