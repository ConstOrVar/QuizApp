package ru.komar.quizapp.ui

import dagger.Component
import ru.komar.quizapp.domain.DomainDIComponent
import ru.komar.quizapp.ui.randomquiz.RandomQuizDIComponent
import javax.inject.Scope

@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class UIScope

@UIScope
@Component(
    dependencies = [
        DomainDIComponent::class
    ]
)
internal interface ScreensDIComponent {
    val randomQuizDIComponentFactory: RandomQuizDIComponent.Factory
}