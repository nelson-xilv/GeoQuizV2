package com.nelsonxilv.geoquizv2

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"
const val IS_CHEATER_KEY = "IS_CHEATER_KEY"

class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    var isCheater: Boolean
        get() = savedStateHandle[IS_CHEATER_KEY] ?: false
        set(value) = savedStateHandle.set(IS_CHEATER_KEY, value)

    private var currentIndex: Int
        get() = savedStateHandle[CURRENT_INDEX_KEY] ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)

    var numberOfAttemptsToCheat = 3

    private var numberOfCorrectAnswer = 0

    private var numberOfIncorrectAnswer = 0

    private val questionBankSize: Int
        get() = questionBank.size

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrevious() {
        currentIndex = (currentIndex + questionBank.size - 1) % questionBank.size
    }

    fun userAnsweredAllQuestions(): Boolean {
        return questionBankSize == numberOfCorrectAnswer + numberOfIncorrectAnswer
    }

    fun percentCorrect(): Int {
        return (numberOfCorrectAnswer * 100) / questionBankSize
    }

    fun updateNumbersOfAnswers(userAnswerIs: Boolean) {
        if (userAnswerIs) {
            numberOfCorrectAnswer++
        } else {
            numberOfIncorrectAnswer++
        }
    }

    fun zeroingNumbersOfAnswers() {
        numberOfCorrectAnswer = 0
        numberOfIncorrectAnswer = 0
    }

    fun updateIsCheater() {
        isCheater = false
    }

    fun updateNumberOfAttemptsToCheat() {
        numberOfAttemptsToCheat -= 1
    }

    fun checkNumberOfAttemptsToCheat(): Boolean {
        return numberOfAttemptsToCheat == 0
    }
}