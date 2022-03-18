package ru.komar.quizapp.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.komar.quizapp.data.QuizService
import ru.komar.quizapp.data.model.Question
import javax.inject.Inject

interface RandomQuestionUseCase {
    suspend fun getSingleQuestion(): Question
}

internal class DefaultRandomQuestionUseCase @Inject constructor(
    private val quizService: QuizService
) : RandomQuestionUseCase {

    override suspend fun getSingleQuestion(): Question {
        return withContext(Dispatchers.IO) {
            quizService.getRandomQuestion(count = 1).first()
        }
    }

}