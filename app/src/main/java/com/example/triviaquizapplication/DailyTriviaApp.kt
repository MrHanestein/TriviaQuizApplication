package com.example.triviaquizapplication

import android.app.Application
import com.google.firebase.FirebaseApp

class DailyTriviaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
