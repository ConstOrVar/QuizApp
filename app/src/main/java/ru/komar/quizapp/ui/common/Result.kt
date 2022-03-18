package ru.komar.quizapp.ui.common

import android.content.Context
import androidx.annotation.StringRes

internal sealed class Result<out T> {

    object None : Result<Nothing>()

    object Loading : Result<Nothing>()

    data class Data<T>(val content: T) : Result<T>()

    class Error(val descriptionProducer: (Context) -> String) : Result<Nothing>() {

        companion object {

            @JvmStatic
            fun fromLocalizedResource(@StringRes stringResId: Int): Error {
                return Error { context -> context.getString(stringResId) }
            }

            @JvmStatic
            fun fromString(message: String): Error {
                return Error { message }
            }

        }
    }

}

internal inline fun <reified T> Result<T>.contentOrNull(): T? {
    return when(this) {
        is Result.Data<*> -> {
            content as T
        }
        else -> {
            null
        }
    }
}