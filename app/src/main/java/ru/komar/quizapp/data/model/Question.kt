package ru.komar.quizapp.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Question(
    val id: Long,
    @Json(name = "question") val content: String,
    val answer: String,
    @Json(name = "value") val reward: Int?
) : Parcelable