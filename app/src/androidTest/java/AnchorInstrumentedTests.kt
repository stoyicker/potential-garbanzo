package org.interview.jorge

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test

internal class AnchorInstrumentedTests {
  @get:Rule
  val activityScenarioRule = ActivityScenarioRule(DefaultActivity::class.java)

  @Test
  fun testNonExistentView() {
    onView(withText("I should not exist")).check(doesNotExist())
  }
}
