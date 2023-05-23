package com.nelsonxilv.geoquizv2

import android.app.Activity
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import com.nelsonxilv.geoquizv2.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val quizViewModel: QuizViewModel by viewModels()

    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            quizViewModel.isCheater =
                result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            quizViewModel.updateNumberOfAttemptsToCheat()
            updateNumberOfTokensTextView()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate")
        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        var unavailableButton = binding.trueButton

        binding.trueButton.setOnClickListener {
            checkAnswer(true, binding.trueButton)
            binding.falseButton.isEnabled = false
            unavailableButton = binding.falseButton
        }

        binding.falseButton.setOnClickListener {
            checkAnswer(false, binding.falseButton)
            binding.trueButton.isEnabled = false
            unavailableButton = binding.trueButton
        }

        binding.cheatsButton.setOnClickListener {
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)
        }

        binding.nextButton.setOnClickListener {
            nextQuestion()
            checkPercent(binding.nextButton)
            buttonAccessibilityUpdate(unavailableButton)
            updateCheatsButton()
        }

        binding.prevButton.setOnClickListener {
            prevQuestion()
            buttonAccessibilityUpdate(unavailableButton)
            updateCheatsButton()
        }

        binding.questionTextView.setOnClickListener {
            nextQuestion()
            checkPercent(binding.nextButton)
            buttonAccessibilityUpdate(unavailableButton)
            updateCheatsButton()
        }

        updateQuestion()

        updateNumberOfTokensTextView()

        updateCheatsButton()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            blurCheatButton()
    }

    private fun buttonAccessibilityUpdate(button: View) {
        button.isEnabled = true
    }

    private fun nextQuestion() {
        quizViewModel.moveToNext()
        updateQuestion()
    }

    private fun prevQuestion() {
        quizViewModel.moveToPrevious()
        updateQuestion()
    }

    private fun checkPercent(button: View) {
        if (quizViewModel.userAnsweredAllQuestions()) {
            val percentCorrect = quizViewModel.percentCorrect()
            val messagePercentAnswer = "${getString(R.string.score_snackbar)} $percentCorrect%"

            Snackbar.make(button, messagePercentAnswer, Snackbar.LENGTH_SHORT)
                .show()

            quizViewModel.zeroingNumbersOfAnswers()
        }
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)
        quizViewModel.updateIsCheater()
    }

    private fun updateNumberOfTokensTextView() {
        val messageNumberOfTokens =
            "${getString(R.string.number_of_remaining_cheat_tokens)} ${quizViewModel.numberOfAttemptsToCheat}"
        binding.numberOfTokensTextView.text = messageNumberOfTokens
    }

    private fun updateCheatsButton() {
        if (quizViewModel.checkNumberOfAttemptsToCheat())
            binding.cheatsButton.isEnabled = false
    }

    private fun checkAnswer(userAnswer: Boolean, button: View) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = when {
            quizViewModel.isCheater -> {
                quizViewModel.updateNumbersOfAnswers(false)
                R.string.judgment_toast
            }
            userAnswer == correctAnswer -> {
                quizViewModel.updateNumbersOfAnswers(true)
                R.string.correct_toast
            }
            else -> {
                quizViewModel.updateNumbersOfAnswers(false)
                R.string.incorrect_toast
            }
        }

        Snackbar.make(button, messageResId, Snackbar.LENGTH_SHORT)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun blurCheatButton() {
        val effect = RenderEffect.createBlurEffect(
            10.0f,
            10.0f,
            Shader.TileMode.CLAMP
        )
        binding.cheatsButton.setRenderEffect(effect)
    }
}
