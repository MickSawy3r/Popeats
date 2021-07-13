package de.sixbits.popeat.service

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class AnalyticsService @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) {
    companion object {
        const val NAVIGATION_EVENT = "navigation_event"
    }

    fun createNavigationEvent() {
        firebaseAnalytics.logEvent(NAVIGATION_EVENT, Bundle.EMPTY)
    }
}