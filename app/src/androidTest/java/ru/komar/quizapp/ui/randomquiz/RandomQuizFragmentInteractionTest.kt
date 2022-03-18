package ru.komar.quizapp.ui.randomquiz

import androidx.fragment.app.testing.withFragment
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.withContext
import org.junit.Test
import org.junit.runner.RunWith
import ru.komar.quizapp.R
import ru.komar.quizapp.createRandomQuestionUseCase
import ru.komar.quizapp.createRandomString

@RunWith(AndroidJUnit4::class)
internal class RandomQuizFragmentInteractionTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testViewWhenReloadAfterLoadFailed() {
        val errorMessage = "Should never happen!"
        val dispatcher = StandardTestDispatcher()

        val useCase = createRandomQuestionUseCase {
            withContext(dispatcher) {
                throw IllegalStateException(errorMessage)
            }
        }

        createRandomQuizFragmentScenario(useCase).use { scenario ->
            RandomQuizScreenPageObject.checkHeader(0)
            RandomQuizScreenPageObject.ensureLoadingState()

            dispatcher.scheduler.advanceUntilIdle()

            RandomQuizScreenPageObject.checkHeader(0)
            RandomQuizScreenPageObject.ensureErrorState(errorMessage)

            RandomQuizScreenPageObject.clickReload()

            RandomQuizScreenPageObject.checkHeader(0)
            RandomQuizScreenPageObject.ensureLoadingState()
        }
    }

    @Test
    fun testViewWhenAnswerEntered() {
        checkInteractionAfterQuestionLoaded { question, _ ->
            val input = "s"
            RandomQuizScreenPageObject.enterAnswer(input)

            RandomQuizScreenPageObject.checkHeader(0)
            RandomQuizScreenPageObject.ensureDataState(
                questionText = question.content,
                hasAnswer = true,
                answerText = input,
                hasSkipOption = true,
                hasNextOption = false,
                hasCheckOption = true,
                hasSuccessNotification = false
            )
        }
    }

    @Test
    fun testViewWhenAnswerCleared() {
        checkInteractionAfterQuestionLoaded { question, _ ->
            val input = "t"
            RandomQuizScreenPageObject.enterAnswer(input)

            RandomQuizScreenPageObject.checkHeader(0)
            RandomQuizScreenPageObject.ensureDataState(
                questionText = question.content,
                hasAnswer = true,
                answerText = input,
                hasSkipOption = true,
                hasNextOption = false,
                hasCheckOption = true,
                hasSuccessNotification = false
            )

            RandomQuizScreenPageObject.clearAnswer()

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
        }
    }

    @Test
    fun testViewWhenQuestionSkipped() {
        checkInteractionAfterQuestionLoaded { question, _ ->
            RandomQuizScreenPageObject.clickSkip()

            RandomQuizScreenPageObject.checkHeader(0)
            RandomQuizScreenPageObject.ensureLoadingState()
        }
    }

    @Test
    fun testViewWhenAnswerApproved() {
        checkInteractionAfterQuestionLoaded { question, _ ->
            val input = question.answer
            RandomQuizScreenPageObject.enterAnswer(input)

            RandomQuizScreenPageObject.clickCheck()

            RandomQuizScreenPageObject.checkHeader(question.reward ?: 0)
            RandomQuizScreenPageObject.ensureDataState(
                questionText = question.content,
                hasAnswer = false,
                answerText = "",
                hasSkipOption = false,
                hasNextOption = true,
                hasCheckOption = false,
                hasSuccessNotification = true
            )
        }
    }

    @Test
    fun testViewWhenAnswerRejected() {
        checkInteractionAfterQuestionLoaded { question, fragmentScenario ->
            val input = createRandomString()
            RandomQuizScreenPageObject.enterAnswer(input)

            RandomQuizScreenPageObject.clickCheck()

            RandomQuizScreenPageObject.checkHeader(0)
            RandomQuizScreenPageObject.ensureDataState(
                questionText = question.content,
                hasAnswer = true,
                answerText = input,
                hasSkipOption = true,
                hasNextOption = false,
                hasCheckOption = true,
                hasSuccessNotification = false
            )

            val notificationText = fragmentScenario.withFragment { requireContext() }
                .getString(R.string.hint_try_again, question.answer.first().uppercaseChar())
            RandomQuizScreenPageObject
                .checkPopupNotification(notificationText)
        }
    }

}