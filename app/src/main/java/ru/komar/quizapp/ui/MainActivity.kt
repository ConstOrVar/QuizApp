package ru.komar.quizapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.komar.quizapp.databinding.ActivityMainBinding
import ru.komar.quizapp.ui.common.DefaultScreenDIProvider
import ru.komar.quizapp.ui.randomquiz.RandomQuizFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(savedInstanceState == null) {
            val diProvider = DefaultScreenDIProvider {
                it.randomQuizDIComponentFactory
            }
            val quizFragment = RandomQuizFragment.newInstance(diProvider)

            supportFragmentManager
                .beginTransaction()
                .add(binding.mainContent.id, quizFragment)
                .commit()
        }
    }

}