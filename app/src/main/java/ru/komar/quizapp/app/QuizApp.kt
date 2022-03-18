package ru.komar.quizapp.app

import android.app.Application
import ru.komar.quizapp.data.DaggerDataDIComponent
import ru.komar.quizapp.domain.DaggerDomainDIComponent
import ru.komar.quizapp.ui.DaggerScreensDIComponent

class QuizApp : Application() {
    private val dataComponent by lazy {
        DaggerDataDIComponent.factory()
            .create("https://jservice.io/api/")
    }
    private val domainComponent by lazy {
        DaggerDomainDIComponent.builder()
            .dataDIComponent(dataComponent)
            .build()
    }
    internal val screensComponent by lazy {
        DaggerScreensDIComponent.builder()
            .domainDIComponent(domainComponent)
            .build()
    }

}