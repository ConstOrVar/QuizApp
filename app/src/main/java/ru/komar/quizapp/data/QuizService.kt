package ru.komar.quizapp.data

import retrofit2.http.GET
import retrofit2.http.Query
import ru.komar.quizapp.data.model.Question

internal interface QuizService {

    @GET("random")
    suspend fun getRandomQuestion(@Query("count") count: Int = 1): List<Question>

}