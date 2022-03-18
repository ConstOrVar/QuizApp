package ru.komar.quizapp.ui.randomquiz

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.withContext
import org.junit.Test
import org.junit.runner.RunWith
import ru.komar.quizapp.createRandomQuestionUseCase

@SmallTest
@RunWith(AndroidJUnit4::class)
class RandomQuizFragmentConfigChangeTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testViewRestoreWhenConfigChangeAfterInitialized() {
        val dispatcher = StandardTestDispatcher()
        val useCase = createRandomQuestionUseCase {
            withContext(dispatcher) {
                throw IllegalStateException("Should never happen!")
            }
        }

        createRandomQuizFragmentScenario(useCase).use { scenario ->
            RandomQuizScreenPageObject.checkHeader(0)
            RandomQuizScreenPageObject.ensureLoadingState()

            scenario.recreate()

            RandomQuizScreenPageObject.checkHeader(0)
            RandomQuizScreenPageObject.ensureLoadingState()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testViewRestoreWhenConfigChangeAfterLoadFailed() {
        val message = "Should never happen!"
        val dispatcher = StandardTestDispatcher()
        val useCase = createRandomQuestionUseCase {
            withContext(dispatcher) {
                throw IllegalStateException(message)
            }
        }
        createRandomQuizFragmentScenario(useCase).use { scenario ->
            RandomQuizScreenPageObject.checkHeader(0)
            RandomQuizScreenPageObject.ensureLoadingState()

            dispatcher.scheduler.advanceUntilIdle()

            RandomQuizScreenPageObject.checkHeader(0)
            RandomQuizScreenPageObject.ensureErrorState(message)

            scenario.recreate()

            RandomQuizScreenPageObject.checkHeader(0)
            RandomQuizScreenPageObject.ensureErrorState(message)
        }
    }

    @Test
    fun testViewWhenConfigChangeAfterLoadFinished() {
        checkInteractionAfterQuestionLoaded { question, fragmentScenario ->
            fragmentScenario.recreate()

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
    fun testViewWhenConfigChangeAfterAnswerEntered() {
        checkInteractionAfterQuestionLoaded { question, fragmentScenario ->
            val input = "s"
            RandomQuizScreenPageObject.enterAnswer(input)

            fragmentScenario.recreate()

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

}