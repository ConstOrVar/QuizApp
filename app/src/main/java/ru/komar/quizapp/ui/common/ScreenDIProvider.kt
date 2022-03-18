package ru.komar.quizapp.ui.common

import android.content.Context
import android.os.Bundle
import ru.komar.quizapp.app.QuizApp
import ru.komar.quizapp.ui.ScreensDIComponent
import java.io.Serializable

internal interface ScreenDIProvider<T> : Serializable {
    fun get(applicationContext: Context): T
}

internal class DefaultScreenDIProvider<T>(
    private val finder: (ScreensDIComponent) -> T
) : ScreenDIProvider<T> {

    override fun get(applicationContext: Context): T {
        return (applicationContext as QuizApp).screensComponent.let(finder)
    }

}

private const val ARG_DI_PROVIDER = "di_provider"

internal fun <T> Bundle.putScreedDIProvider(key: String = ARG_DI_PROVIDER, diProvider: ScreenDIProvider<T>) {
    putSerializable(key, diProvider)
}

@Suppress("UNCHECKED_CAST")
internal inline fun <reified T> Bundle.getScreedDIProvider(key: String = ARG_DI_PROVIDER): ScreenDIProvider<T> {
    return getSerializable(key) as ScreenDIProvider<T>
}