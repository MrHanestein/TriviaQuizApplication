package com.example.triviaquizapplication.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.triviaquizapplication.R
import com.example.triviaquizapplication.databinding.ActivityFinishQuizBinding
import com.example.triviaquizapplication.viewmodel.FinishQuizViewModel

class FinishQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinishQuizBinding
    private lateinit var viewModel: FinishQuizViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(FinishQuizViewModel::class.java)

        val score = intent.getIntExtra("SCORE", 0)
        val totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 10)

        val passingThreshold = 4
        val passMessage = if (score >= passingThreshold) {
            getString(R.string.congratulations_passed)
        } else {
            getString(R.string.sorry_failed)
        }

        val percentage = (score.toDouble() / totalQuestions) * 100
        binding.tvResultMessage.text = passMessage
        binding.tvScoreDetails.text = getString(R.string.score_details, score, totalQuestions, percentage.toInt())

        if (score >= passingThreshold) {
            binding.imageViewResult.setImageResource(R.drawable.ic_success)
        } else {
            binding.imageViewResult.setImageResource(R.drawable.ic_failure)
        }

        // Fetch and display a random joke
        viewModel.joke.observe(this) { joke ->
            binding.tvJoke.text = joke
        }
        viewModel.getJoke()

        binding.btnRetry.setOnClickListener {
            // Retry quiz: go back to MainActivity (or whatever your start screen is)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnMainMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
