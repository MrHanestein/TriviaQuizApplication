package com.example.triviaquizapplication.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.triviaquizapplication.R
import com.example.triviaquizapplication.databinding.ItemLeaderboardBinding

data class LeaderboardEntry(val username: String, val score: Int)

class LeaderboardAdapter : RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    private var leaderboard: List<LeaderboardEntry> = listOf()

    fun setLeaderboard(newLeaderboard: List<LeaderboardEntry>) {
        leaderboard = newLeaderboard
        notifyDataSetChanged()
    }

    inner class LeaderboardViewHolder(private val binding: ItemLeaderboardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: LeaderboardEntry) {
            binding.tvUsername.text = entry.username
            binding.tvScore.text = entry.score.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val binding = ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaderboardViewHolder(binding)
    }


    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val entry = leaderboard[position]
        holder.bind(entry)

        val trophyImage = holder.itemView.findViewById<ImageView>(R.id.imgTrophy)
        trophyImage.visibility = if (position == 0) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int = leaderboard.size
}
