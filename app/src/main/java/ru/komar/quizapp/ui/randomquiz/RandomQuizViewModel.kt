package ru.komar.quizapp.ui.randomquiz

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.komar.quizapp.R
import ru.komar.quizapp.data.model.Question
import ru.komar.quizapp.domain.RandomQuestionUseCase
import ru.komar.quizapp.ui.common.Result
import ru.komar.quizapp.ui.common.contentOrNull
import java.io.IOException

internal class RandomQuizViewModel(
    private val randomQuestionUseCase: RandomQuestionUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(),
    RandomQuizScreen.State,
    RandomQuizScreen.ActionHandler {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if(throwable is IOException) {
            question.value = Result.Error.fromLocalizedResource(R.string.error_network_failure)
        } else {
            val error = (throwable.localizedMessage ?: throwable.message)
                ?.let(Result.Error.Companion::fromString)
                ?: Result.Error.fromLocalizedResource(R.string.error_internal_fackup)
            question.value = error
        }
    }

    private var operation: Job? = null

    // region RandomQuizScreen.State
    override val question = MutableLiveData(
        savedStateHandle.get<Question>(STATE_QUESTION)?.let { Result.Data(it) }
            ?: Result.None
    )

    override val score = savedStateHandle.getLiveData(STATE_SCORE, 0)

    override val questionResult = savedStateHandle.getLiveData(STATE_QUESTION_RESULT, RandomQuizScreen.QuestionResult.NOT_SOLVED_YET)

    override val event = MutableLiveData<RandomQuizScreen.Event>(null)
    // endregion

    // region RandomQuizScreen.ActionHandler
    override fun checkAnswer(answer: String) {
        val question = requireNotNull(question.value?.contentOrNull()) {
            "Question missing. Can't check answer"
        }

        val sanitizedAnswer = answer.trim()
        if(question.answer == sanitizedAnswer) {
            if(question.reward != null) {
                score.value = requireNotNull(score.value) + question.reward
            }
            questionResult.value = RandomQuizScreen.QuestionResult.ANSWERED
        } else {
            event.value = RandomQuizScreen.Event.ShowWrongErrorHint { context ->
                context.getString(R.string.hint_try_again, question.answer.first().uppercaseChar())
            }
        }
    }

    override fun retry() {
        loadQuestion()
    }

    override fun next() {
        loadQuestion()
    }

    override fun skip() {
        loadQuestion()
    }
    // endregion

    // region ViewModel
    override fun onCleared() {
        operation?.cancel()
        super.onCleared()
    }
    // endregion

    init {
        if(question.value !is Result.Data) {
            loadQuestion()
        }
    }

    private fun loadQuestion() {
        questionResult.value = RandomQuizScreen.QuestionResult.NOT_SOLVED_YET
        question.value = Result.Loading
        savedStateHandle.set(STATE_QUESTION, null)

        operation?.cancel()

        operation = viewModelScope.launch(exceptionHandler) {
            val questionInfo = randomQuestionUseCase.getSingleQuestion()
            savedStateHandle.set(STATE_QUESTION, questionInfo)
            question.value = Result.Data(questionInfo)
        }
    }

    companion object {
        private const val STATE_SCORE = "score"
        private const val STATE_QUESTION = "question"
        private const val STATE_QUESTION_RESULT = "question_result"

        @VisibleForTesting
        @JvmStatic
        internal fun MutableMap<String, Any>.saveState(
            score: Int,
            question: Question?,
            questionResult: RandomQuizScreen.QuestionResult
        ) {
            put(STATE_SCORE, score)
            if(question != null) {
                put(STATE_QUESTION, question)
            }
            put(STATE_QUESTION_RESULT, questionResult)
        }

    }
}