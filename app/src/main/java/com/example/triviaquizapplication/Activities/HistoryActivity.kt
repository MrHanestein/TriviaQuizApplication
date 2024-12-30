package com.example.triviaquizapplication.Activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.triviaquizapplication.Adapters.HistoryAdapter
import com.example.triviaquizapplication.databinding.ActivityHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class HistoryItem(val score: Int, val totalQuestions: Int, val timestamp: Long)

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchHistory()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter()
        binding.recyclerViewHistory.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(this@HistoryActivity)
        }
    }

    private fun fetchHistory() {
        val user = auth.currentUser ?: return

        db.collection("users").document(user.uid)
            .collection("history")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val historyList = querySnapshot.documents.map { doc ->
                    val score = doc.getLong("score")?.toInt() ?: 0
                    val totalQuestions = doc.getLong("totalQuestions")?.toInt() ?: 0
                    val timestamp = doc.getTimestamp("timestamp")?.toDate()?.time ?: 0L
                    HistoryItem(score, totalQuestions, timestamp)
                }
                historyAdapter.submitList(historyList)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch history.", Toast.LENGTH_SHORT).show()
            }
    }
}
