package ru.komar.quizapp.ui.randomquiz

import dagger.BindsInstance
import dagger.Component
import ru.komar.quizapp.domain.RandomQuestionUseCase
import ru.komar.quizapp.ui.UIScope

@UIScope
@Component
internal interface FakeRootComponent {
    val randomQuizDIComponentFactory: RandomQuizDIComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance randomQuestionUseCase: RandomQuestionUseCase): FakeRootComponent
    }
}