package ru.komar.quizapp.ui.randomquiz

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import ru.komar.quizapp.CoroutineDispatcherRule
import ru.komar.quizapp.createRandomQuestion
import ru.komar.quizapp.createRandomQuestionUseCase
import ru.komar.quizapp.createRandomString
import ru.komar.quizapp.data.model.Question
import ru.komar.quizapp.ui.common.Result
import java.io.IOException
import java.lang.Exception

internal class RandomQuizViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()
    @get:Rule
    val coroutineDispatcherRule = CoroutineDispatcherRule()

    @Test
    fun testStateWhenInitialized() {
        val useCase = createRandomQuestionUseCase {
            throw Exception("should never happen!")
        }
        val viewModel = RandomQuizViewModel(useCase, SavedStateHandle())

        viewModel.assertState(
            expectedScore = 0,
            expectedQuestionResult = RandomQuizScreen.QuestionResult.NOT_SOLVED_YET,
            eventVerifier = Assert::assertNull,
            questionVerifier = { actual -> Assert.assertEquals(Result.Loading, actual) }
        )
    }

    @Test
    fun testStateWhenRestoredWithQuestion() {
        val useCase = createRandomQuestionUseCase {
            throw Exception("should never happen!")
        }

        val savedScore = 666
        val savedQuestion: Question = createRandomQuestion()
        val savedQuestionResult = RandomQuizScreen.QuestionResult.ANSWERED

        val savedState = with(RandomQuizViewModel) {
            mutableMapOf<String, Any>().apply {
                saveState(
                    score = savedScore,
                    question = savedQuestion,
                    questionResult = savedQuestionResult
                )
            }
        }
        val viewModel = RandomQuizViewModel(useCase, SavedStateHandle(savedState))

        viewModel.assertState(
            expectedScore = savedScore,
            expectedQuestionResult = savedQuestionResult,
            eventVerifier = Assert::assertNull,
            questionVerifier = { actual -> Assert.assertEquals(Result.Data(savedQuestion), actual) }
        )
    }

    @Test
    fun testStateWhenRestoredWithNoQuestion() {
        val useCase = createRandomQuestionUseCase {
            throw Exception("should never happen!")
        }

        val savedScore = 666
        val savedQuestionResult = RandomQuizScreen.QuestionResult.NOT_SOLVED_YET

        val savedState = with(RandomQuizViewModel) {
            mutableMapOf<String, Any>().apply {
                saveState(
                    score = savedScore,
                    question = null,
                    questionResult = savedQuestionResult
                )
            }
        }
        val viewModel = RandomQuizViewModel(useCase, SavedStateHandle(savedState))

        viewModel.assertState(
            expectedScore = savedScore,
            expectedQuestionResult = savedQuestionResult,
            eventVerifier = Assert::assertNull,
            questionVerifier = { actual -> Assert.assertEquals(Result.Loading, actual) }
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testStateWhenLoadQuestionFailed() {
        val useCase = createRandomQuestionUseCase {
            throw IOException()
        }

        val viewModel = RandomQuizViewModel(useCase, SavedStateHandle())
        coroutineDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.assertState(
            expectedScore = 0,
            expectedQuestionResult = RandomQuizScreen.QuestionResult.NOT_SOLVED_YET,
            eventVerifier = Assert::assertNull,
            questionVerifier = { actual -> Assert.assertTrue(actual is Result.Error) }
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testStateWhenLoadQuestionFinished() {
        val question = createRandomQuestion()

        val useCase = createRandomQuestionUseCase {
            question
        }

        val viewModel = RandomQuizViewModel(useCase, SavedStateHandle())
        coroutineDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.assertState(
            expectedScore = 0,
            expectedQuestionResult = RandomQuizScreen.QuestionResult.NOT_SOLVED_YET,
            eventVerifier = Assert::assertNull,
            questionVerifier = { actual -> Assert.assertEquals(Result.Data(question), actual) }
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testStateSavedWhenLoadQuestionFinished() {
        val question = createRandomQuestion()

        val useCase = createRandomQuestionUseCase {
            question
        }

        val savedStateHandle = SavedStateHandle()

        val viewModel = RandomQuizViewModel(useCase, savedStateHandle)
        coroutineDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val useCase2 = createRandomQuestionUseCase {
            throw Exception("should never happen!")
        }
        val restoredViewModel = RandomQuizViewModel(useCase2, savedStateHandle)

        restoredViewModel.assertState(
            expectedScore = 0,
            expectedQuestionResult = RandomQuizScreen.QuestionResult.NOT_SOLVED_YET,
            eventVerifier = Assert::assertNull,
            questionVerifier = { actual -> Assert.assertEquals(Result.Data(question), actual) }
        )
    }

    @Test
    fun testStateWhenCheckWrongAnswer() {
        val useCase = createRandomQuestionUseCase {
            throw Exception("should never happen!")
        }

        val savedScore = 666
        val savedQuestion: Question = createRandomQuestion()
        val savedQuestionResult = RandomQuizScreen.QuestionResult.NOT_SOLVED_YET

        val savedState = with(RandomQuizViewModel) {
            mutableMapOf<String, Any>().apply {
                saveState(
                    score = savedScore,
                    question = savedQuestion,
                    questionResult = savedQuestionResult
                )
            }
        }
        val viewModel = RandomQuizViewModel(useCase, SavedStateHandle(savedState))

        viewModel.checkAnswer(createRandomString())

        viewModel.assertState(
            expectedScore = savedScore,
            expectedQuestionResult = savedQuestionResult,
            eventVerifier = { actual ->
                Assert.assertTrue(actual is RandomQuizScreen.Event.ShowWrongErrorHint)
                Assert.assertTrue(!actual!!.isConsumed)
            },
            questionVerifier = { actual -> Assert.assertEquals(Result.Data(savedQuestion), actual) }
        )
    }

    @Test
    fun testStateWhenCheckCorrectAnswer() {
        val useCase = createRandomQuestionUseCase {
            throw Exception("should never happen!")
        }

        val savedScore = 666
        val savedQuestion: Question = createRandomQuestion()
        val savedQuestionResult = RandomQuizScreen.QuestionResult.NOT_SOLVED_YET

        val savedState = with(RandomQuizViewModel) {
            mutableMapOf<String, Any>().apply {
                saveState(
                    score = savedScore,
                    question = savedQuestion,
                    questionResult = savedQuestionResult
                )
            }
        }
        val viewModel = RandomQuizViewModel(useCase, SavedStateHandle(savedState))

        viewModel.checkAnswer(savedQuestion.answer)

        viewModel.assertState(
            expectedScore = savedScore + (savedQuestion.reward ?: 0),
            expectedQuestionResult = RandomQuizScreen.QuestionResult.ANSWERED,
            eventVerifier = Assert::assertNull,
            questionVerifier = { actual -> Assert.assertEquals(Result.Data(savedQuestion), actual) }
        )
    }

    @Test
    fun testStateWhenNextInvoked() {
        val useCase = createRandomQuestionUseCase {
            throw Exception("Should never happen!")
        }

        val savedScore = 777
        val savedQuestion: Question = createRandomQuestion()
        val savedQuestionResult = RandomQuizScreen.QuestionResult.ANSWERED

        val savedState = with(RandomQuizViewModel) {
            mutableMapOf<String, Any>().apply {
                saveState(
                    score = savedScore,
                    question = savedQuestion,
                    questionResult = savedQuestionResult
                )
            }
        }
        val viewModel = RandomQuizViewModel(useCase, SavedStateHandle(savedState))

        viewModel.next()

        viewModel.assertState(
            expectedScore = savedScore,
            expectedQuestionResult = RandomQuizScreen.QuestionResult.NOT_SOLVED_YET,
            eventVerifier = Assert::assertNull,
            questionVerifier = { actual -> Assert.assertEquals(Result.Loading, actual) }
        )
    }

    @Test
    fun testStateWhenSkipInvoked() {
        val useCase = createRandomQuestionUseCase {
            throw Exception("Should never happen!")
        }

        val savedScore = 777
        val savedQuestion: Question = createRandomQuestion()
        val savedQuestionResult = RandomQuizScreen.QuestionResult.ANSWERED

        val savedState = with(RandomQuizViewModel) {
            mutableMapOf<String, Any>().apply {
                saveState(
                    score = savedScore,
                    question = savedQuestion,
                    questionResult = savedQuestionResult
                )
            }
        }
        val viewModel = RandomQuizViewModel(useCase, SavedStateHandle(savedState))

        viewModel.skip()

        viewModel.assertState(
            expectedScore = savedScore,
            expectedQuestionResult = RandomQuizScreen.QuestionResult.NOT_SOLVED_YET,
            eventVerifier = Assert::assertNull,
            questionVerifier = { actual -> Assert.assertEquals(Result.Loading, actual) }
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testStateWhenRetryInvoked() {
        val useCase = createRandomQuestionUseCase {
            throw Exception("Should never happen!")
        }

        val viewModel = RandomQuizViewModel(useCase, SavedStateHandle())
        coroutineDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.assertState(
            expectedScore = 0,
            expectedQuestionResult = RandomQuizScreen.QuestionResult.NOT_SOLVED_YET,
            eventVerifier = Assert::assertNull,
            questionVerifier = { actual -> Assert.assertTrue(actual is Result.Error) }
        )

        viewModel.retry()

        viewModel.assertState(
            expectedScore = 0,
            expectedQuestionResult = RandomQuizScreen.QuestionResult.NOT_SOLVED_YET,
            eventVerifier = Assert::assertNull,
            questionVerifier = { actual -> Assert.assertEquals(Result.Loading, actual) }
        )
    }

    private fun RandomQuizViewModel.assertState(
        expectedScore: Int,
        expectedQuestionResult: RandomQuizScreen.QuestionResult,
        eventVerifier: (RandomQuizScreen.Event?) -> Unit,
        questionVerifier: (Result<Question>) -> Unit
    ) {
        Assert.assertEquals(expectedScore, score.value)
        Assert.assertEquals(expectedQuestionResult, questionResult.value)
        eventVerifier(event.value)
        questionVerifier(question.value!!)
    }

}