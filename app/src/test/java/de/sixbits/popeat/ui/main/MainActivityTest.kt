package de.sixbits.popeat.ui.main

import android.Manifest
import android.app.Application
import android.os.Build
import android.os.Looper.getMainLooper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import de.sixbits.popeat.ui.main.fragment.MapFragment
import de.sixbits.popeat.ui.main.fragment.RequestPermissionsFragment
import de.sixbits.popeat.ui.main.fragment.VenueListFragment
import de.sixbits.popeat.ui.main.state.HomeState
import de.sixbits.popeat.view_model.HomeViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@LooperMode(LooperMode.Mode.PAUSED)
@ExtendWith(MockitoExtension::class)
class MainActivityTest {
    private lateinit var activity: MainActivity
    private lateinit var activityController: ActivityController<MainActivity>

    @Mock
    private lateinit var homeViewModel: HomeViewModel

    @Mock
    private lateinit var homeStateLiveData: MutableLiveData<HomeState>

    @Mock
    private lateinit var errorLiveData: MutableLiveData<String>

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var app: ShadowApplication

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(MainActivity::class.java)
        activity = activityController.get()

        val application = ApplicationProvider.getApplicationContext<Application>()
        app = shadowOf(application)
    }

    // Start Testing!
    @LooperMode(LooperMode.Mode.PAUSED)
    @Test
    fun inflateRequestPermissionsWhenLocationPermissionsNotGranted() {
        `when`(homeViewModel.hasLocationPermissions).thenReturn(false)
        activity.injectViewModel(homeViewModel)
        activityController.create()

        shadowOf(getMainLooper()).idle()

        assert(
            activity.supportFragmentManager.findFragmentByTag(RequestPermissionsFragment::class.java.name) != null
        )
        assert(
            activity.supportFragmentManager.findFragmentByTag(MapFragment::class.java.name) == null
        )
        assert(
            activity.supportFragmentManager.findFragmentByTag(VenueListFragment::class.java.name) == null
        )
    }

    @LooperMode(LooperMode.Mode.PAUSED)
    @Test
    fun inflateHomeWhenLocationPermissionsGranted_AndRequestUserLocation() {
        app.grantPermissions(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
        )

        `when`(homeViewModel.hasLocationPermissions).thenReturn(true)
        `when`(homeViewModel.errorLiveData).thenReturn(errorLiveData)
        `when`(homeViewModel.homeStateLiveData).thenReturn(homeStateLiveData)
        activity.injectViewModel(homeViewModel)
        shadowOf(getMainLooper()).idle()
        activityController.create()
        shadowOf(getMainLooper()).idle()

        assert(
            activity.supportFragmentManager.findFragmentByTag(RequestPermissionsFragment::class.java.name) == null
        )
        assert(
            activity.supportFragmentManager.findFragmentByTag(MapFragment::class.java.name) != null
        )
        assert(
            activity.supportFragmentManager.findFragmentByTag(VenueListFragment::class.java.name) != null
        )
        verify(homeViewModel, times(1)).requestCurrentLocation()
    }
}