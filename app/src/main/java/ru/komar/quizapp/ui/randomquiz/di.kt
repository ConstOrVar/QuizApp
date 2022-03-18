package ru.komar.quizapp.ui.randomquiz

import androidx.fragment.app.Fragment
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import ru.komar.quizapp.domain.RandomQuestionUseCase
import ru.komar.quizapp.ui.common.ScreenScope

@ScreenScope
@Subcomponent(
    modules = [
        RandomQuizDIModule::class
    ]
)
internal interface RandomQuizDIComponent {
    val state: RandomQuizScreen.State
    val actionHandler: RandomQuizScreen.ActionHandler

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): RandomQuizDIComponent
    }
}

@Module
internal object RandomQuizDIModule {

    @ScreenScope
    @Provides
    @Suppress("UNCHECKED_CAST", "ReplaceGetOrSet")
    fun getViewModel(fragment: Fragment, useCase: RandomQuestionUseCase): RandomQuizViewModel {
        return ViewModelProvider(
            fragment,
            object : AbstractSavedStateViewModelFactory(fragment, null) {

                override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
                    return RandomQuizViewModel(useCase, handle) as T
                }

            }
        ).get(RandomQuizViewModel::class.java)
    }

    @ScreenScope
    @Provides
    fun buildState(viewModel: RandomQuizViewModel): RandomQuizScreen.State {
        return viewModel
    }

    @ScreenScope
    @Provides
    fun buildActionHandler(viewModel: RandomQuizViewModel): RandomQuizScreen.ActionHandler {
        return viewModel
    }
}