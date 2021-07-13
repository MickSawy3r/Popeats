package de.sixbits.popeat.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.maps.model.LatLng
import de.sixbits.popeat.VenueDataModelFactory
import de.sixbits.popeat.data_model.VenueDataModel
import de.sixbits.popeat.service.AnalyticsService
import de.sixbits.popeat.service.LocationService
import de.sixbits.popeat.service.VenueService
import de.sixbits.popeat.ui.main.state.HomeStateExploration
import de.sixbits.popeat.ui.main.state.HomeStateNavigation
import de.sixbits.popeat.utils.LocationHelper
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.kotlin.any

class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val analyticsService = mock(AnalyticsService::class.java)

    @Before
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun testGetRecommendations_fetchingRecommendationsSuccess() {
        val venueService = mock(VenueService::class.java)
        val locationHelper = mock(LocationHelper::class.java)
        val locationService = mock(LocationService::class.java)

        val homeViewModel =
            HomeViewModel(venueService, locationHelper, locationService, analyticsService)

        `when`(
            venueService.getVenueRecommendations(any())
        ).thenReturn(Observable.just(VenueDataModelFactory.getVenueList()))

        `when`(
            venueService.getVenueDetails(VenueDataModelFactory.getVenueList())
        ).thenReturn(Observable.fromIterable(VenueDataModelFactory.getVenueList()))

        homeViewModel.recommendationsLiveData.observeForever {}

        homeViewModel.homeStateLiveData.postValue(HomeStateExploration(LatLng(12.0, 12.0)))
        homeViewModel.getRecommendations(12.0, 12.0)

        assert(homeViewModel.recommendationsLiveData.value?.size == 2) {
            "Actually there are ${homeViewModel.recommendationsLiveData.value?.size} Recommendations!"
        }
        assert(homeViewModel.recommendationsLiveData.value?.contains(VenueDataModelFactory.getVenueItem1()) == true)
        assert(homeViewModel.recommendationsLiveData.value?.contains(VenueDataModelFactory.getVenueItem2()) == true)
    }

    @Test
    fun whenRequestingUpdates_shouldReplaceMiniItemsWithDetailsItems() {
        val venueService = mock(VenueService::class.java)
        val locationHelper = mock(LocationHelper::class.java)
        val locationService = mock(LocationService::class.java)

        val venue1 = VenueDataModel(
            name = "Old Place",
            geoLocation = LatLng(12.0, 12.0),
            location = "Address",
            category = "Persian Food",
            image = "https://google.com/images.jpg",
            id = "1"
        )

        val venue2 = VenueDataModel(
            name = "New Place",
            geoLocation = LatLng(12.0, 12.0),
            location = "Address",
            category = "Persian Food",
            image = "https://google.com/images.jpg",
            id = "1"
        )

        val homeViewModel =
            HomeViewModel(venueService, locationHelper, locationService, analyticsService)

        `when`(
            venueService.getVenueRecommendations(any())
        ).thenReturn(Observable.just(listOf(venue1)))

        `when`(
            venueService.getVenueDetails(any())
        ).thenReturn(Observable.just(venue2))

        homeViewModel.recommendationsLiveData.observeForever {}

        homeViewModel.homeStateLiveData.postValue(HomeStateExploration(LatLng(12.0, 12.0)))

        // Save the Old Venue in the Live Data,
        // And request it's details
        homeViewModel.getRecommendations(12.0, 12.0)

        assert(homeViewModel.recommendationsLiveData.value?.size == 1)
        assert(homeViewModel.recommendationsLiveData.value?.get(0)?.name == venue1.name)
    }

    @Test
    fun testGetRecommendations_fetchingRecommendationsFail() {
        val venueService = mock(VenueService::class.java)
        val locationHelper = mock(LocationHelper::class.java)
        val locationService = mock(LocationService::class.java)

        val homeViewModel =
            HomeViewModel(venueService, locationHelper, locationService, analyticsService)

        `when`(
            venueService.getVenueRecommendations(any())
        ).thenReturn(Observable.error(Exception("Error")))

        homeViewModel.recommendationsLiveData.observeForever {}
        homeViewModel.errorLiveData.observeForever {}

        homeViewModel.homeStateLiveData.postValue(HomeStateExploration(LatLng(12.0, 12.0)))
        homeViewModel.getRecommendations(12.0, 12.0)

        assert(homeViewModel.recommendationsLiveData.value == null)
        assert(homeViewModel.errorLiveData.value == "Error")
    }

    @Test
    fun testRequestCurrentLocation_locationRequestSuccess() {
        val venueService = mock(VenueService::class.java)
        val locationHelper = mock(LocationHelper::class.java)
        val locationService = mock(LocationService::class.java)

        val homeViewModel =
            HomeViewModel(venueService, locationHelper, locationService, analyticsService)
        val locationSubject = BehaviorSubject.create<LatLng>()

        `when`(locationService.locationSubject)
            .thenReturn(locationSubject)
        `when`(locationService.startRequestingLocation())
            .thenAnswer { locationSubject.onNext(LatLng(12.0, 12.0)) }

        homeViewModel.homeStateLiveData.observeForever {}

        homeViewModel.requestCurrentLocation()

        assert(homeViewModel.homeStateLiveData.value is HomeStateExploration)

        assert(homeViewModel.homeStateLiveData.value != null)
        val exploration = homeViewModel.homeStateLiveData.value as HomeStateExploration
        assert(exploration.marker == LatLng(12.0, 12.0))
    }

    @Test
    fun testRequestCurrentLocation_locationRequestFail() {
        val venueService = mock(VenueService::class.java)
        val locationHelper = mock(LocationHelper::class.java)
        val locationService = mock(LocationService::class.java)

        val homeViewModel =
            HomeViewModel(venueService, locationHelper, locationService, analyticsService)
        val locationSubject = BehaviorSubject.create<LatLng>()

        `when`(locationService.locationSubject)
            .thenReturn(locationSubject)
        `when`(locationService.startRequestingLocation())
            .thenAnswer { locationSubject.onError(Exception("Error")) }

        homeViewModel.homeStateLiveData.observeForever {}
        homeViewModel.errorLiveData.observeForever {}

        homeViewModel.requestCurrentLocation()

        assert(homeViewModel.homeStateLiveData.value == null)
        assert(homeViewModel.errorLiveData.value == "Error")
    }

    @Test
    fun testEnterNavigationMode_validVenue() {
        val venueService = mock(VenueService::class.java)
        val locationHelper = mock(LocationHelper::class.java)
        val locationService = mock(LocationService::class.java)

        val homeViewModel =
            HomeViewModel(venueService, locationHelper, locationService, analyticsService)
        homeViewModel.homeStateLiveData.observeForever {}

        homeViewModel.enterNavigationMode(
            VenueDataModel(
                name = "Place 01",
                geoLocation = LatLng(12.0, 24.0),
                location = "Address",
                category = "Persian Food",
                image = "https://google.com/images.jpg",
                id = "1"
            )
        )

        assert(homeViewModel.homeStateLiveData.value is HomeStateNavigation)
        val navState = homeViewModel.homeStateLiveData.value as HomeStateNavigation

        assert(navState.marker.longitude == 24.0)
        assert(navState.marker.latitude == 12.0)
    }

    @Test
    fun testEnterNavigationMode_invalidVenue() {
        val venueService = mock(VenueService::class.java)
        val locationHelper = mock(LocationHelper::class.java)
        val locationService = mock(LocationService::class.java)

        val homeViewModel =
            HomeViewModel(venueService, locationHelper, locationService, analyticsService)
        homeViewModel.homeStateLiveData.observeForever {}
        homeViewModel.errorLiveData.observeForever {}

        homeViewModel.enterNavigationMode(
            VenueDataModel(
                name = "Place 01",
                geoLocation = LatLng(0.0, 0.0),
                location = "Address",
                category = "Persian Food",
                image = "https://google.com/images.jpg",
                id = "1"
            )
        )

        assert(homeViewModel.homeStateLiveData.value == null)
        assert(homeViewModel.errorLiveData.value == "Error Getting the Value from location Place 01")
    }

    @Test
    fun testEnterExplorationMode() {
        val venueService = mock(VenueService::class.java)
        val locationHelper = mock(LocationHelper::class.java)
        val locationService = mock(LocationService::class.java)

        val homeViewModel =
            HomeViewModel(venueService, locationHelper, locationService, analyticsService)
        homeViewModel.homeStateLiveData.observeForever {}

        val lat = 12.0
        val lon = 24.0
        homeViewModel.injectLastLocation(LatLng(lat, lon))

        homeViewModel.enterExplorationMode()

        assert(homeViewModel.homeStateLiveData.value is HomeStateExploration)
        val navState = homeViewModel.homeStateLiveData.value as HomeStateExploration

        assert(navState.marker.longitude == lon)
        assert(navState.marker.latitude == lat)
    }
}
