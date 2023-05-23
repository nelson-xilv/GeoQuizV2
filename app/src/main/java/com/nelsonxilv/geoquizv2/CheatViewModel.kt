package com.nelsonxilv.geoquizv2

import androidx.lifecycle.ViewModel

class CheatViewModel : ViewModel() {

    var answerIsTrue = true
    var answerWasClicked = false

    fun getAnswerIsTrue(answerIsTrue: Boolean) {
        this.answerIsTrue = answerIsTrue
    }

    fun setAnswerWasClicked() {
        answerWasClicked = true
    }
}