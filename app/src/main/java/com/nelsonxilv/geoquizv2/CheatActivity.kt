package com.nelsonxilv.geoquizv2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.nelsonxilv.geoquizv2.databinding.ActivityCheatBinding

const val EXTRA_ANSWER_SHOWN =
    "com.nelsonxilv.geoquizv2.answer_shown"

private const val EXTRA_ANSWER_IS_TRUE =
    "com.nelsonxilv.geoquizv2.answer_is_true"

class CheatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheatBinding
    private val cheatViewModel: CheatViewModel by viewModels()

    private var answerIsTrue = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        cheatViewModel.getAnswerIsTrue(answerIsTrue)

        binding.showAnswerButton.setOnClickListener {
            setAnswerText()
            cheatViewModel.setAnswerWasClicked()
        }

        if (cheatViewModel.answerWasClicked) {
            updateAnswerTextView()
        }

        binding.apiLevelDeviceTextView.append(" ${Build.VERSION.SDK_INT}")
    }

    private fun setAnswerText() {
        updateAnswerTextView()
        setAnswerShownResult(true)
    }

    private fun updateAnswerTextView() {
        val answerText = when(cheatViewModel.answerIsTrue) {
            true -> R.string.true_button
            else -> R.string.false_button
        }
        binding.answerTextView.setText(answerText)
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}