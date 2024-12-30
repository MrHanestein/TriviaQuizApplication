package com.example.triviaquizapplication.Activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.triviaquizapplication.databinding.ActivityLeaderboardBinding
import com.example.triviaquizapplication.viewmodel.LeaderboardViewModel
import com.example.triviaquizapplication.Adapters.LeaderboardAdapter

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()
    private lateinit var leaderboardAdapter: LeaderboardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        leaderboardViewModel.leaderboardLiveData.observe(this) { leaderboard ->
            val sortedList = leaderboard.sortedByDescending { it.score }
            leaderboardAdapter.setLeaderboard(sortedList)
        }

        leaderboardViewModel.fetchLeaderboard()
    }

    private fun setupRecyclerView() {
        leaderboardAdapter = LeaderboardAdapter()
        binding.recyclerViewLeaderboard.apply {
            adapter = leaderboardAdapter
            layoutManager = LinearLayoutManager(this@LeaderboardActivity)
        }
    }
}
