package de.sixbits.popeat.ui.main.fragment

import android.Manifest
import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelStore
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.dynamic.IObjectWrapper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import de.sixbits.popeat.VenueDataModelFactory
import de.sixbits.popeat.data_model.VenueDataModel
import de.sixbits.popeat.ui.main.state.HomeState
import de.sixbits.popeat.ui.main.state.HomeStateExploration
import de.sixbits.popeat.ui.main.state.HomeStateNavigation
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
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@LooperMode(LooperMode.Mode.PAUSED)
@ExtendWith(MockitoExtension::class)
class MapFragmentTest {
    @Mock
    private lateinit var homeViewModel: HomeViewModel

    @Mock
    private lateinit var googleMap: GoogleMap

    @Mock
    private lateinit var iCameraUpdateFactoryDelegate: ICameraUpdateFactoryDelegate

    private lateinit var recommendationsLiveData: MutableLiveData<List<VenueDataModel>>
    private lateinit var loadingLiveData: MutableLiveData<Boolean>
    private lateinit var homeStateViewModel: MutableLiveData<HomeState>

    private lateinit var app: ShadowApplication

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        loadingLiveData = MutableLiveData()
        recommendationsLiveData = MutableLiveData()
        homeStateViewModel = MutableLiveData()

        `when`(homeViewModel.homeStateLiveData).thenReturn(homeStateViewModel)
        `when`(homeViewModel.getBitmapDescriptorFrom(any())).thenReturn(null)
        `when`(iCameraUpdateFactoryDelegate.newLatLngZoom(any(), any())).thenReturn(
            object : IObjectWrapper.Stub() {
            })

        val application = ApplicationProvider.getApplicationContext<Application>()
        app = Shadows.shadowOf(application)
    }

    @Test
    fun whenMapReceivesNavigationEvent_ShouldCreateAMarkerAndZoomToIt() {
        app.grantPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET
        )

        val fragment = MapFragment { ViewModelStore() }
        fragment.injectViewModel(homeViewModel)

        launchFragmentInContainer { fragment }.onFragment {
            CameraUpdateFactory.zza(iCameraUpdateFactoryDelegate)

            it.onMapReady(googleMap)

            homeStateViewModel.postValue(
                HomeStateNavigation(
                    marker = LatLng(12.0, 12.0),
                    distance = "3",
                    venue = VenueDataModelFactory.getVenueItem1()
                )
            )

            verify(googleMap, times(1)).animateCamera(any())
            verify(googleMap, times(1)).addMarker(any())
        }
    }

    @Test
    fun whenMapReceivesExplorationEvent_ShouldCreateAMarkerAndZoomToIt() {
        app.grantPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET
        )

        val fragment = MapFragment { ViewModelStore() }
        fragment.injectViewModel(homeViewModel)

        launchFragmentInContainer { fragment }.onFragment {
            CameraUpdateFactory.zza(iCameraUpdateFactoryDelegate)

            it.onMapReady(googleMap)

            homeStateViewModel.postValue(
                HomeStateExploration(
                    marker = LatLng(12.0, 12.0)
                )
            )

            verify(googleMap, times(1)).animateCamera(any())
            verify(googleMap, times(1)).addMarker(any())
        }
    }

    @Test
    fun whenMapReceivesOtherEvents_ShouldDoNothing() {
        val fragment = MapFragment { ViewModelStore() }
        fragment.injectViewModel(homeViewModel)

        launchFragmentInContainer { fragment }.onFragment {
            CameraUpdateFactory.zza(iCameraUpdateFactoryDelegate)

            it.onMapReady(googleMap)

            homeStateViewModel.postValue(
                object : HomeState() {}
            )

            verify(googleMap, times(0)).animateCamera(any())
            verify(googleMap, times(0)).addMarker(any())
        }
    }

    @Test
    fun whenCameraIdle_ShouldRequestRecommendations() {
        val fragment = MapFragment { ViewModelStore() }
        fragment.injectViewModel(homeViewModel)
        fragment.injectMap(googleMap)

        launchFragmentInContainer { fragment }.onFragment {
            CameraUpdateFactory.zza(iCameraUpdateFactoryDelegate)
            `when`(googleMap.cameraPosition).thenReturn(CameraPosition.fromLatLngZoom(LatLng(12.0, 12.0), 12f))

            it.onCameraIdle()

            verify(homeViewModel, times(1)).getRecommendations(any(), any())
            verify(googleMap, times(0)).addMarker(any())
        }
    }
}