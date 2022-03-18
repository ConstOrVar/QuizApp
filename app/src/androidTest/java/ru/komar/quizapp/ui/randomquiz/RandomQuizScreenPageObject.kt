package ru.komar.quizapp.ui.randomquiz

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import ru.komar.quizapp.R

internal object RandomQuizScreenPageObject {

    fun checkHeader(score: Int) {
        onView(withId(R.id.txt_score))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(score.toString())
                    )
                )
            )
    }

    fun checkProgress(isVisible: Boolean) {
        onView(withId(R.id.indicator_progress))
            .check(
                matches(
                    if(isVisible) isDisplayed() else not(isDisplayed())
                )
            )
    }

    fun checkNoErrorInfo() {
        onView(withId(R.id.txt_error_description))
            .check(
                matches(
                    allOf(
                        not(isDisplayed()),
                        withText("")
                    )

                )
            )
        onView(withId(R.id.btn_reload))
            .check(
                matches(
                    not(isDisplayed())
                )
            )
    }

    fun checkErrorInfo(errorText: String) {
        onView(withId(R.id.txt_error_description))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(errorText)
                    )
                )
            )
        onView(withId(R.id.btn_reload))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        isClickable()
                    )

                )
            )
    }

    fun checkNoQuestionInfo() {
        onView(withId(R.id.txt_question))
            .check(
                matches(
                    not(isDisplayed())
                )
            )
        onView(withId(R.id.edt_answer))
            .check(
                matches(
                    not(isDisplayed())
                )
            )
        onView(withId(R.id.txt_question_success))
            .check(
                matches(
                    not(isDisplayed())
                )
            )
        onView(withId(R.id.btn_skip))
            .check(
                matches(
                    not(isDisplayed())
                )
            )
        onView(withId(R.id.btn_next))
            .check(
                matches(
                    not(isDisplayed())
                )
            )
        onView(withId(R.id.btn_check))
            .check(
                matches(
                    not(isDisplayed())
                )
            )
    }

    fun checkQuestionInfo(
        questionText: String,
        hasAnswer: Boolean,
        answerText: String,
        hasSkipOption: Boolean,
        canSkip: Boolean = hasSkipOption,
        hasNextOption: Boolean,
        canGoNext: Boolean = hasNextOption,
        hasCheckOption: Boolean,
        canCheck: Boolean = hasCheckOption,
        hasSuccessNotification: Boolean
    ) {
        onView(withId(R.id.txt_question))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(questionText)
                    )
                )
            )
        onView(withId(R.id.edt_answer)).run {
            if(hasAnswer) {
                check(
                    matches(
                        allOf(
                            isDisplayed(),
                            withText(answerText)
                        )
                    )
                )
            } else {
                check(
                    matches(
                        allOf(
                            not(isDisplayed()),
                            withText(answerText)
                        )
                    )
                )
            }
        }
        onView(withId(R.id.txt_question_success))
            .check(
                matches(
                    if(hasSuccessNotification) isDisplayed() else not(isDisplayed())
                )
            )

        onView(withId(R.id.btn_skip)).run {
            if(hasSkipOption) {
                check(
                    matches(
                        allOf(
                            isDisplayed(),
                            if(canSkip) isEnabled() else not(isEnabled())
                        )
                    )
                )
            } else {
                check(
                    matches(
                        not(isDisplayed())
                    )
                )
            }
        }
        onView(withId(R.id.btn_next)).run {
            if(hasNextOption) {
                check(
                    matches(
                        allOf(
                            isDisplayed(),
                            if(canGoNext) isEnabled() else not(isEnabled())
                        )
                    )
                )
            } else {
                check(
                    matches(
                        not(isDisplayed())
                    )
                )
            }
        }
        onView(withId(R.id.btn_check)).run {
            if(hasCheckOption) {
                check(
                    matches(
                        allOf(
                            isDisplayed(),
                            if(canCheck) isEnabled() else not(isEnabled())
                        )
                    )
                )
            } else {
                check(
                    matches(
                        not(isDisplayed())
                    )
                )
            }
        }
    }

    fun checkPopupNotification(text: String) {
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(text)
                    )
                )
            )
    }

    fun clickReload() {
        onView(withId(R.id.btn_reload))
            .perform(click())
    }

    fun clickSkip() {
        onView(withId(R.id.btn_skip))
            .perform(click())
    }

    fun clickCheck() {
        onView(withId(R.id.btn_check))
            .perform(click())
    }

    fun enterAnswer(text: String) {
        onView(withId(R.id.edt_answer))
            .perform(replaceText(text))
    }

    fun clearAnswer() {
        onView(withId(R.id.edt_answer))
            .perform(clearText())
    }

}

internal fun RandomQuizScreenPageObject.ensureLoadingState() {
    checkProgress(isVisible = true)
    checkNoErrorInfo()
    checkNoQuestionInfo()
}

internal fun RandomQuizScreenPageObject.ensureErrorState(errorText: String) {
    checkProgress(isVisible = false)
    checkErrorInfo(errorText)
    checkNoQuestionInfo()
}

internal fun RandomQuizScreenPageObject.ensureDataState(
    questionText: String,
    hasAnswer: Boolean,
    answerText: String,
    hasSkipOption: Boolean,
    canSkip: Boolean = hasSkipOption,
    hasNextOption: Boolean,
    canGoNext: Boolean = hasNextOption,
    hasCheckOption: Boolean,
    canCheck: Boolean = hasCheckOption,
    hasSuccessNotification: Boolean
) {
    checkProgress(isVisible = false)
    checkNoErrorInfo()
    checkQuestionInfo(
        questionText,
        hasAnswer,
        answerText,
        hasSkipOption,
        canSkip,
        hasNextOption,
        canGoNext,
        hasCheckOption,
        canCheck,
        hasSuccessNotification
    )
}