package ru.komar.quizapp.data

import com.squareup.moshi.Moshi
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import okhttp3.HttpUrl
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DataDIModule::class
    ]
)
internal interface DataDIComponent {
    val quizService: QuizService

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance quizUrl: String
        ): DataDIComponent
    }
}

@Module
internal object DataDIModule {

    @Singleton
    @Provides
    @JvmStatic
    fun buildMoshi(): Moshi {
        return Moshi
            .Builder()
            .build()
    }

    @Singleton
    @Provides
    @JvmStatic
    fun buildServiceUrl(quizUrl: String): HttpUrl {
        return HttpUrl.parse(quizUrl)
            ?: throw IllegalStateException("Wrong service URI")
    }

    @Singleton
    @Provides
    @JvmStatic
    fun buildRetrofit(url: HttpUrl, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Singleton
    @Provides
    @JvmStatic
    fun buildQuizService(retrofit: Retrofit): QuizService {
        return retrofit.create(QuizService::class.java)
    }

}