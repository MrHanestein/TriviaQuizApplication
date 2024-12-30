package com.example.triviaquizapplication.Activities

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatActivity
import com.example.triviaquizapplication.R
import com.example.triviaquizapplication.databinding.ActivitySettingsBinding
import com.example.triviaquizapplication.viewmodel.SettingsViewModel
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels()

    private val questionTypes = arrayOf("Any", "Multiple Choice", "True/False")
    private val topics = arrayOf("Any", "General Knowledge", "Books", "Film", "Music", "Science & Nature")

    private lateinit var sharedPreferences: SharedPreferences
    private val PREFS_NAME = "settings_prefs"
    private val DARK_MODE_KEY = "dark_mode"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        setupSpinners()
        setupTimePicker()
        setupDarkModeToggle()
        loadDarkModePreference()

        binding.btnSaveSettings.setOnClickListener {
            saveSettings()
        }

        settingsViewModel.settingsSaved.observe(this) { success ->
            if (success) {
                Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, R.string.settings_save_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpinners() {
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, questionTypes)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerQuestionType.adapter = typeAdapter

        val topicAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, topics)
        topicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTopics.adapter = topicAdapter
    }

    private fun setupTimePicker() {
        binding.tvNotificationTimeValue.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                binding.tvNotificationTimeValue.text = String.format("%02d:%02d", selectedHour, selectedMinute)
            }, hour, minute, true).show()
        }
    }

    private fun setupDarkModeToggle() {
        binding.toggleDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveDarkModePreference(true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveDarkModePreference(false)
            }
        }
    }

    private fun loadDarkModePreference() {
        val isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false)
        binding.toggleDarkMode.isChecked = isDarkMode
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun saveDarkModePreference(isDarkMode: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(DARK_MODE_KEY, isDarkMode)
            apply()
        }
    }

    private fun saveSettings() {
        val selectedType = binding.spinnerQuestionType.selectedItem.toString()
        val selectedTopic = binding.spinnerTopics.selectedItem.toString()
        val notificationTime = binding.tvNotificationTimeValue.text.toString()

        val type = when (selectedType) {
            "Multiple Choice" -> "multiple"
            "True/False" -> "boolean"
            else -> null
        }

        val category = when (selectedTopic) {
            "General Knowledge" -> 9
            "Books" -> 10
            "Film" -> 11
            "Music" -> 12
            "Science & Nature" -> 17
            else -> null
        }

        // usea a default or previously selected saved value by user
        val numberOfQuestions = 10 // Default value

        settingsViewModel.saveSettings(type, category, numberOfQuestions, notificationTime)
    }
}
