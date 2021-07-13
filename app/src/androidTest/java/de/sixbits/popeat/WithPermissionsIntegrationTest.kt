package de.sixbits.popeat

import android.Manifest
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
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
class WithPermissionsIntegrationTest {
    @JvmField
    val countingIdlingResource = EspressoIdlingResource.countingIdlingResource

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(countingIdlingResource)
    }

    @Test
    fun testMainFlow() {
        // When I start the activity
        scenario = ActivityScenario.launch(MainActivity::class.java)

        // Then I should see a map and a recycler view
        onView(withId(R.id.map))
            .check(matches(isDisplayed()))
        onView(withId(R.id.rv_venue_recommendations))
            .check(matches(isDisplayed()))

        // And the recyclerview contains few recommendations
        onView(withText("Homeslice"))
            .check(matches(isDisplayed()))
        onView(withText("Al Enam"))
            .check(matches(isDisplayed()))

        // When I click a restaurant
        onView(withText("Al Enam"))
            .perform(click())

        // Then I should see a header with the clicked restaurant
        onView(withId(R.id.tv_header_venue_name))
            .check(matches(withText("Al Enam")))
        onView(withId(R.id.tv_distance))
            .check(matches(withText("1")))

        Espresso.pressBack()
        onView(withId(R.id.tv_header_venue_name))
            .check(doesNotExist())
    }
}