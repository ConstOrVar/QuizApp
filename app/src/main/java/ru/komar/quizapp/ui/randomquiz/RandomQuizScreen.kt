package ru.komar.quizapp.ui.randomquiz

import android.content.Context
import androidx.lifecycle.LiveData
import ru.komar.quizapp.data.model.Question
import ru.komar.quizapp.ui.common.Result

internal class RandomQuizScreen {

    interface State {
        val question: LiveData<Result<Question>>
        val score: LiveData<Int>
        val questionResult: LiveData<QuestionResult>
        val event: LiveData<Event?>
    }

    interface ActionHandler {
        fun checkAnswer(answer: String)
        fun retry()
        fun next()
        fun skip()
    }

    sealed class Event(var isConsumed: Boolean = false) {

        class ShowWrongErrorHint(
            val hintProducer: (Context) -> String
        ) : Event()

    }

    enum class QuestionResult {
        ANSWERED,
        NOT_SOLVED_YET
    }

}