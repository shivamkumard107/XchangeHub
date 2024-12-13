package com.dev.sk.xchangehub.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dev.sk.xchangehub.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainFragmentTest {
    @Before
    fun setup() {
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun testBaseValueEditText() {
        onView(withId(R.id.baseValue))
            .check(matches(isDisplayed()))

        onView(withId(R.id.baseValue))
            .perform(typeText("1023.3123"), closeSoftKeyboard())

        onView(withId(R.id.baseValue))
            .check(matches(withText("1023.3123")))

        onView(withId(R.id.baseValue))
            .perform(clearText())

        onView(withId(R.id.baseValue))
            .check(matches(withText("")))
    }
}
