package ru.komar.quizapp.domain

import dagger.Binds
import dagger.Component
import dagger.Module
import ru.komar.quizapp.data.DataDIComponent
import javax.inject.Scope

@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class DomainScope

@DomainScope
@Component(
    dependencies = [
        DataDIComponent::class
    ],
    modules = [
        DomainDIModule::class
    ]
)
internal interface DomainDIComponent {
    val randomQuestionUseCase: RandomQuestionUseCase
}

@Module
internal interface DomainDIModule {

    @DomainScope
    @Binds
    fun buildRandomQuestionUseCase(useCase: DefaultRandomQuestionUseCase): RandomQuestionUseCase

}