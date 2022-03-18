package ru.komar.quizapp.ui.randomquiz

import android.content.Context
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.withContext
import ru.komar.quizapp.R
import ru.komar.quizapp.createRandomQuestion
import ru.komar.quizapp.createRandomQuestionUseCase
import ru.komar.quizapp.data.model.Question
import ru.komar.quizapp.domain.RandomQuestionUseCase
import ru.komar.quizapp.ui.common.ScreenDIProvider

internal fun createRandomQuizFragmentScenario(randomQuestionUseCase: RandomQuestionUseCase): FragmentScenario<RandomQuizFragment> {
    val testFragment = RandomQuizFragment.newInstance(object : ScreenDIProvider<RandomQuizDIComponent.Factory> {
        override fun get(applicationContext: Context): RandomQuizDIComponent.Factory {
            return DaggerFakeRootComponent.factory().create(randomQuestionUseCase).randomQuizDIComponentFactory
        }
    })
    return launchFragmentInContainer(
        fragmentArgs = testFragment.arguments,
        themeResId = R.style.Theme_QuizApp
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
internal fun checkInteractionAfterQuestionLoaded(block: (Question, FragmentScenario<RandomQuizFragment>) -> Unit) {
    val question = createRandomQuestion()
    val dispatcher = StandardTestDispatcher()

    val useCase = createRandomQuestionUseCase {
        withContext(dispatcher) {
            question
        }
    }
    createRandomQuizFragmentScenario(useCase).use { scenario ->
        RandomQuizScreenPageObject.checkHeader(0)
        RandomQuizScreenPageObject.ensureLoadingState()

        dispatcher.scheduler.advanceUntilIdle()

        RandomQuizScreenPageObject.checkHeader(0)
        RandomQuizScreenPageObject.ensureDataState(
            questionText = question.content,
            hasAnswer = true,
            answerText = "",
            hasSkipOption = true,
            hasNextOption = false,
            hasCheckOption = true,
            canCheck = false,
            hasSuccessNotification = false
        )

        block(question, scenario)
    }
}