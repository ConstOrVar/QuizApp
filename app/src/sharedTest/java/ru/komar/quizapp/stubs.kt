package ru.komar.quizapp

import ru.komar.quizapp.data.model.Question
import ru.komar.quizapp.domain.RandomQuestionUseCase
import java.lang.StringBuilder
import kotlin.math.absoluteValue
import kotlin.random.Random

private val rand = Random(System.currentTimeMillis())

internal fun createRandomQuestionUseCase(block: suspend () -> Question): RandomQuestionUseCase {
    return object : RandomQuestionUseCase {
        override suspend fun getSingleQuestion(): Question {
            return block()
        }
    }
}

internal fun createRandomQuestion(): Question {
    return Question(
        id = rand.nextLong().absoluteValue,
        content = createRandomString(),
        answer = createRandomString(),
        reward = rand.nextInt().absoluteValue
    )
}

internal fun createRandomString(): String {
    val stringBuilder = StringBuilder()

    val length = 10 + rand.nextInt() % 10
    repeat(length) {
        stringBuilder.append(rand.nextInt().toChar())
    }
    return stringBuilder.toString()
}