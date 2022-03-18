package ru.komar.quizapp.ui.randomquiz

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import ru.komar.quizapp.R
import ru.komar.quizapp.databinding.FragmentRandromQuizBinding
import ru.komar.quizapp.ui.common.Result
import ru.komar.quizapp.ui.common.ScreenDIProvider
import ru.komar.quizapp.ui.common.getScreedDIProvider
import ru.komar.quizapp.ui.common.putScreedDIProvider

internal class RandomQuizFragment : Fragment(R.layout.fragment_randrom_quiz) {

    companion object {

        fun newInstance(diProvider: ScreenDIProvider<RandomQuizDIComponent.Factory>): Fragment {
            return RandomQuizFragment().apply {
                arguments = Bundle().also {
                    it.putScreedDIProvider(diProvider = diProvider)
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentRandromQuizBinding.bind(view)

        val diComponent: RandomQuizDIComponent = requireArguments()
            .getScreedDIProvider<RandomQuizDIComponent.Factory>()
            .get(view.context.applicationContext)
            .create(this)

        setupView(binding, diComponent.state, diComponent.actionHandler)
    }

    private fun setupView(
        binding: FragmentRandromQuizBinding,
        state: RandomQuizScreen.State,
        actionHandler: RandomQuizScreen.ActionHandler
    ) {
        setupState(binding, state)
        setupInteractions(binding, actionHandler)
    }

    private fun setupState(
        binding: FragmentRandromQuizBinding,
        state: RandomQuizScreen.State
    ) {
        val context = binding.root.context

        state.question.observe(viewLifecycleOwner) { result ->
            when(result) {
                is Result.Data -> {
                    binding.txtQuestion.text = result.content.content

                    binding.containerQuestionContent.isVisible = true
                    binding.containerQuestionProgress.root.isVisible = false
                    binding.containerQuestionError.root.isVisible = false
                }
                is Result.Error -> {
                    binding.containerQuestionError.txtErrorDescription.text = result.descriptionProducer(context)
                    binding.containerQuestionError.root.isVisible = true
                    binding.containerQuestionProgress.root.isVisible = false
                    binding.containerQuestionContent.isVisible = false
                }
                Result.Loading -> {
                    binding.containerQuestionProgress.root.isVisible = true
                    binding.containerQuestionError.root.isVisible = false
                    binding.containerQuestionContent.isVisible = false

                    binding.containerQuestionError.txtErrorDescription.text = null
                    binding.txtQuestion.text = null
                }
                Result.None -> {
                    binding.containerQuestionProgress.root.isVisible = false
                    binding.containerQuestionError.root.isVisible = false
                    binding.containerQuestionContent.isVisible = false
                }
            }
        }

        state.event.observe(viewLifecycleOwner) { event ->
            if(event != null && !event.isConsumed) {
                when(event) {
                    is RandomQuizScreen.Event.ShowWrongErrorHint -> {
                        Snackbar
                            .make(binding.root, event.hintProducer(context), Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
                event.isConsumed = true
            }
        }

        state.score.observe(viewLifecycleOwner) { score ->
            binding.txtScore.text = score.toString()
        }

        state.questionResult.observe(viewLifecycleOwner) { result ->
            when(requireNotNull(result)) {
                RandomQuizScreen.QuestionResult.ANSWERED -> {
                    binding.edtAnswer.text = null

                    binding.groupAfterAnswer.isVisible = true
                    binding.groupToAnswer.isVisible = false
                    binding.groupAnswer.isVisible = false
                }
                RandomQuizScreen.QuestionResult.NOT_SOLVED_YET -> {
                    binding.groupAfterAnswer.isVisible = false
                    binding.groupToAnswer.isVisible = true
                    binding.groupAnswer.isVisible = true
                }
            }
        }

        binding.btnCheck.isEnabled = binding.edtAnswer.text.isNotEmpty()
        binding.edtAnswer.doAfterTextChanged { editable ->
            binding.btnCheck.isEnabled = editable != null && editable.isNotEmpty()
        }
    }

    private fun setupInteractions(
        binding: FragmentRandromQuizBinding,
        actionHandler: RandomQuizScreen.ActionHandler
    ) {
        binding.containerQuestionError.btnReload.setOnClickListener {
            actionHandler.retry()
        }

        binding.btnCheck.setOnClickListener {
            actionHandler.checkAnswer(binding.edtAnswer.text.toString())
        }

        binding.btnNext.setOnClickListener {
            actionHandler.next()
        }

        binding.btnSkip.setOnClickListener {
            actionHandler.skip()
        }
    }

}