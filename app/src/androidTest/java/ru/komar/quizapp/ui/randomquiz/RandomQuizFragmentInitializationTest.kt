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
class RandomQuizFragmentInitializationTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testViewWhenInitialized() {
        val dispatcher = StandardTestDispatcher()
        val useCase = createRandomQuestionUseCase {
            withContext(dispatcher) {
                throw IllegalStateException("Should never happen!")
            }
        }

        createRandomQuizFragmentScenario(useCase).use {
            RandomQuizScreenPageObject.checkHeader(0)
            RandomQuizScreenPageObject.ensureLoadingState()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testViewWhenLoadFailed() {
        val errorMessage = "Should never happen!"
        val dispatcher = StandardTestDispatcher()

        val useCase = createRandomQuestionUseCase {
            withContext(dispatcher) {
                throw IllegalStateException(errorMessage)
            }
        }
        createRandomQuizFragmentScenario(useCase).use {
            RandomQuizScreenPageObject.checkHeader(0)
            RandomQuizScreenPageObject.ensureLoadingState()

            dispatcher.scheduler.advanceUntilIdle()

            RandomQuizScreenPageObject.checkHeader(0)
            RandomQuizScreenPageObject.ensureErrorState(errorMessage)
        }
    }

    @Test
    fun testViewWhenLoadFinished() {
        checkInteractionAfterQuestionLoaded { _, _-> }
    }

}