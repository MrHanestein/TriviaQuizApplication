package com.example.triviaquizapplication.Adapters

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.triviaquizapplication.databinding.ItemQuestionBinding
import com.example.triviaquizapplication.models.Question

class QuestionAdapter(
    private val onAnswerSelected: (selectedAnswer: String, correctAnswer: String) -> Unit
) : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

    private var questions: List<Question> = listOf()
    private val userSelectedAnswers = mutableMapOf<Int, String?>()

    fun setQuestions(newQuestions: List<Question>) {
        questions = newQuestions
        userSelectedAnswers.clear()
        notifyDataSetChanged()
    }

    fun getUserAnswers(): Map<Int, String?> = userSelectedAnswers
    fun getCurrentQuestions(): List<Question> = questions

    inner class QuestionViewHolder(private val binding: ItemQuestionBinding) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(question: Question, position: Int) {
            // Decode HTML for the question
            val rawQuestionText = question.question ?: "No question available"
            val decodedQuestionText = Html.fromHtml(rawQuestionText, Html.FROM_HTML_MODE_LEGACY).toString()
            binding.tvQuestion.text = "Q${position + 1}: $decodedQuestionText"

            // Combine correct and incorrect answers, decode HTML for each
            val safeIncorrectAnswers = question.incorrectAnswers ?: emptyList()
            val safeCorrectAnswer = question.correctAnswer
            val allAnswersRaw = (safeIncorrectAnswers + listOfNotNull(safeCorrectAnswer))

            // Decode each answer from HTML
            val allAnswers = allAnswersRaw.map { Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY).toString() }.shuffled()

            // Clear old views
            binding.radioGroupAnswers.setOnCheckedChangeListener(null)
            binding.radioGroupAnswers.removeAllViews()

            // Restore previously selected answer if any
            val previouslySelected = userSelectedAnswers[position]

            // Create radio buttons and set checked state
            for (answer in allAnswers) {
                val radioButton = RadioButton(binding.root.context)
                radioButton.text = answer
                radioButton.isChecked = (answer == previouslySelected)
                binding.radioGroupAnswers.addView(radioButton)
            }

            // Set the listener after setting states to avoid false triggers
            binding.radioGroupAnswers.setOnCheckedChangeListener { group, checkedId ->
                val selectedBtn = group.findViewById<RadioButton>(checkedId)
                val selectedAnswer = selectedBtn?.text?.toString()
                userSelectedAnswers[position] = selectedAnswer
                if (safeCorrectAnswer != null && selectedAnswer != null) {
                    onAnswerSelected(selectedAnswer, safeCorrectAnswer)
                }
            }

            // Hide the tvAnswer since we have multiple-choice
            binding.tvAnswer.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questions[position], position)
    }

    override fun getItemCount(): Int = questions.size
}
