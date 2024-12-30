package com.example.triviaquizapplication.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.triviaquizapplication.Activities.MainActivity
import com.example.triviaquizapplication.R
import com.example.triviaquizapplication.network.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TriviaWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override suspend fun doWork(): Result {
        return try {
            val currentUser = firebaseAuth.currentUser
            val userId = currentUser?.uid ?: return Result.failure()

            val userDoc = db.collection("users").document(userId).get().await()
            if (!userDoc.exists()) return Result.failure()

            val type = userDoc.getString("type") ?: "multiple"
            val category = userDoc.getLong("category")?.toInt()
            val numberOfQuestions = userDoc.getLong("numberOfQuestions")?.toInt() ?: 10

            val response = RetrofitInstance.api.getQuestions(
                amount = numberOfQuestions,
                category = category,
                type = type
            )

            if (!response.isSuccessful || response.body() == null) {
                return Result.failure()
            }

            // Successfully fetched questions
            sendNotification()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun sendNotification() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "daily_trivia_channel"
        val channelName = "Daily Trivia Notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_trivia)
            .setContentTitle("Daily Trivia Ready!")
            .setContentText("Tap to start your daily trivia challenge.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(1, notification)
    }
}
