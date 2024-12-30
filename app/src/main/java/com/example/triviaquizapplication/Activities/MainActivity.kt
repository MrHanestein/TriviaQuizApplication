package com.example.triviaquizapplication.Activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.triviaquizapplication.Adapters.QuestionAdapter
import com.example.triviaquizapplication.NotificationScheduler
import com.example.triviaquizapplication.R
import com.example.triviaquizapplication.databinding.ActivityMainBinding
import com.example.triviaquizapplication.viewmodel.AuthViewModel
import com.example.triviaquizapplication.viewmodel.SettingsViewModel
import com.example.triviaquizapplication.viewmodel.TriviaViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val triviaViewModel: TriviaViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private lateinit var questionAdapter: QuestionAdapter

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var userScore = 0
    private var notificationTime: String = "Not set"
    private var countDownTimer: CountDownTimer? = null

    // Quiz duration (For instance: 10 minutes)
    private val quizDurationMillis = 10 * 60 * 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModels()
        loadUserSettings()

        // Set up buttons
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            authViewModel.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnViewHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        binding.btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_score_text, userScore))
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)))
        }

        binding.btnSubmit.setOnClickListener {
            calculateAndUpdateScore()
        }

        binding.btnLeaderboard.setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }

    }

    private fun observeViewModels() {
        triviaViewModel.questionsLiveData.observe(this) { questions ->
            questionAdapter.setQuestions(questions)
            // Starts the quiz timer once questions are loaded here
            startQuizTimer()
        }

        triviaViewModel.errorLiveData.observe(this) { error ->
            Toast.makeText(this, getString(R.string.error_fetching_questions, error), Toast.LENGTH_LONG).show()
        }

        settingsViewModel.userSettings.observe(this) { settingsMap ->
            val type = settingsMap["type"] as? String ?: "multiple"
            val category = (settingsMap["category"] as? Long)?.toInt()
            val numberOfQuestions = (settingsMap["numberOfQuestions"] as? Long)?.toInt() ?: 10
            notificationTime = settingsMap["notificationTime"] as? String ?: "Not set"

            binding.tvNotificationTimeValue.text = getString(R.string.notification_time_label, notificationTime)

            // Fetch questions with user settings implementation
            triviaViewModel.getQuestions(amount = numberOfQuestions, category = category, type = type)

            // Set up countdown to notification time when set
            if (notificationTime != "Not set") {
                startCountdownToNotification(notificationTime)
            } else {
                binding.tvCountdown.text = getString(R.string.no_notification_time)
            }
        }
    }

    private fun loadUserSettings() {
        settingsViewModel.loadUserSettings()
    }

    private fun setupRecyclerView() {
        questionAdapter = QuestionAdapter { _, _ ->
            // Give optional immediate feedback on answer selection
        }
        binding.recyclerViewQuestions.apply {
            adapter = questionAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun calculateAndUpdateScore() {
        val userAnswers = questionAdapter.getUserAnswers()
        val questions = questionAdapter.getCurrentQuestions()

        var correctCount = 0
        for ((index, answer) in userAnswers) {
            val correct = questions[index].correctAnswer
            if (answer == correct) correctCount++
        }
        saveQuizHistory(correctCount, questions.size) // Save history
        userScore = correctCount
        updateUserScore(correctCount)

        // Once done calculating, gooes to FinishQuizActivity
        val totalQuestions = questions.size
        val intent = Intent(this, FinishQuizActivity::class.java).apply {
            putExtra("SCORE", correctCount)
            putExtra("TOTAL_QUESTIONS", totalQuestions)
        }
        startActivity(intent)
        finish()
    }

    private fun updateUserScore(score: Int) {
        val user = auth.currentUser ?: return
        db.collection("users").document(user.uid)
            .update("score", score)
            .addOnSuccessListener {
                Toast.makeText(this, R.string.score_updated, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, R.string.score_update_failed, Toast.LENGTH_SHORT).show()
            }
    }

    private fun startQuizTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(quizDurationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                binding.tvCountdown.text = getString(R.string.countdown_quiz, minutes, seconds)
            }

            override fun onFinish() {
                Toast.makeText(this@MainActivity, R.string.time_up, Toast.LENGTH_LONG).show()
                calculateAndUpdateScore()
            }
        }.start()
    }

    private fun startCountdownToNotification(notificationTime: String) {
        val parts = notificationTime.split(":")
        if (parts.size != 2) {
            binding.tvCountdown.text = getString(R.string.no_notification_time)
            return
        }

        val targetHour = parts[0].toIntOrNull() ?: return
        val targetMinute = parts[1].toIntOrNull() ?: return

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, targetHour)
            set(Calendar.MINUTE, targetMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (target.before(now)) {
            target.add(Calendar.DATE, 1)
        }

        val diffMillis = target.timeInMillis - now.timeInMillis

        val hours = (diffMillis / 3600000)
        val minutes = (diffMillis % 3600000) / 60000
        val seconds = (diffMillis % 60000) / 1000

        binding.tvCountdown.text = getString(R.string.countdown_format, hours, minutes, seconds)

        scheduleNotification(targetHour, targetMinute)
    }

    private fun saveQuizHistory(score: Int, totalQuestions: Int) {
        val user = auth.currentUser ?: return

        val quizHistory = mapOf(
            "score" to score,
            "totalQuestions" to totalQuestions,
            "timestamp" to com.google.firebase.Timestamp.now() // Uses Timestamp for firestore
        )

        db.collection("users").document(user.uid)
            .collection("history")
            .add(quizHistory)
            .addOnSuccessListener {
                Toast.makeText(this, "History saved!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save history.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun scheduleNotification(hour: Int, minute: Int) {
        NotificationScheduler.scheduleDailyNotification(this, hour, minute)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
