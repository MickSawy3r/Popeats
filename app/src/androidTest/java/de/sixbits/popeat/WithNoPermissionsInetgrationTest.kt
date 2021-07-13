package de.sixbits.popeat

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.sixbits.popeat.ui.main.MainActivity
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class WithNoPermissionsInetgrationTest {

    @get:Rule(order = 1)
    val mainScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @JvmField
    val countingIdlingResource = EspressoIdlingResource.countingIdlingResource

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(countingIdlingResource)
    }

    @Test
    fun shouldSeeRequestPermissions() {
        // Given I have fresh started the application
        // Then I should see a view requesting location permissions
        onView(withText(R.string.request_location_permissions))
            .check(matches(isDisplayed()))

        onView(withId(R.id.map))
            .check(matches(not(isDisplayed())))
    }
}