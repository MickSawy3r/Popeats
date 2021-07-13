package de.sixbits.popeat.view_model

import androidx.annotation.RequiresPermission
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import de.sixbits.popeat.EspressoIdlingResource
import de.sixbits.popeat.data_model.VenueDataModel
import de.sixbits.popeat.mapper.ErrorMapper
import de.sixbits.popeat.network.VenueRecommendationsQueryBuilder
import de.sixbits.popeat.service.AnalyticsService
import de.sixbits.popeat.service.ILocationService
import de.sixbits.popeat.service.VenueService
import de.sixbits.popeat.ui.main.state.HomeState
import de.sixbits.popeat.ui.main.state.HomeStateExploration
import de.sixbits.popeat.ui.main.state.HomeStateNavigation
import de.sixbits.popeat.utils.LocationHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject
import kotlin.math.roundToInt

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val venueService: VenueService,
    private val locationHelper: LocationHelper,
    private val locationService: ILocationService,
    private val analyticsService: AnalyticsService
) : ViewModel() {
    private lateinit var lastLocation: LatLng

    val hasLocationPermissions get() = locationHelper.hasLocationPermissions

    // For indexing recommendations
    private val recommendationStore = HashMap<String, VenueDataModel>()

    // List Controllers
    var homeStateLiveData = MutableLiveData<HomeState>()
    val recommendationsLiveData = MutableLiveData<List<VenueDataModel>>()
    val errorLiveData = MutableLiveData<String>()
    val loadingLiveData = MutableLiveData(true)

    fun getRecommendations(lat: Double, lon: Double) {
        if (homeStateLiveData.value !is HomeStateExploration) {
            return
        }

        EspressoIdlingResource.increment()

        recommendationStore.clear()

        loadingLiveData.postValue(true)
        // Create the request
        val request = VenueRecommendationsQueryBuilder()
            .setLatitudeLongitude(latitude = lat, longitude = lon)
            .setSection("food")

        // Execute it
        venueService.getVenueRecommendations(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                fetchDetails(it)
            }
            .subscribe(
                {
                    EspressoIdlingResource.decrement()
                    it.forEach { v ->
                        recommendationStore[v.id] = v
                    }
                    loadingLiveData.postValue(false)
                    recommendationsLiveData.postValue(recommendationStore.values.toMutableList())
                },
                {
                    EspressoIdlingResource.decrement()
                    loadingLiveData.postValue(false)
                    if (it is HttpException) {
                        errorLiveData.postValue(ErrorMapper.mapErrorCode(it.code()))
                    } else {
                        errorLiveData.postValue("${it.message}")
                    }
                }
            )
    }

    private fun fetchDetails(venues: List<VenueDataModel>) {
        // Execute it
        venueService.getVenueDetails(venues)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    recommendationStore[it.id] = it
                    recommendationsLiveData.postValue(ArrayList(recommendationStore.values))
                },
                {
                    if (it is HttpException) {
                        errorLiveData.postValue(ErrorMapper.mapErrorCode(it.code()))
                    } else {
                        errorLiveData.postValue("${it.message}")
                    }
                }
            )
    }

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    fun requestCurrentLocation() {
        EspressoIdlingResource.increment()

        locationService.locationSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    EspressoIdlingResource.decrement()
                    lastLocation = it
                    enterExplorationMode()
                },
                {
                    EspressoIdlingResource.decrement()
                    errorLiveData.postValue(it.message)
                }
            )

        locationService.startRequestingLocation()
    }

    fun enterNavigationMode(venue: VenueDataModel) {
        if (venue.geoLocation.latitude == 0.0 && venue.geoLocation.longitude == 0.0) {
            errorLiveData.postValue("Error Getting the Value from location ${venue.name}")
            return
        }

        analyticsService.createNavigationEvent()
        homeStateLiveData.postValue(
            HomeStateNavigation(
                marker = venue.geoLocation,
                venue = venue,
                distance = venue.distance
            )
        )
    }

    fun enterExplorationMode() {
        if (this::lastLocation.isInitialized)
            homeStateLiveData.postValue(
                HomeStateExploration(
                    marker = lastLocation
                )
            )
    }

    fun getBitmapDescriptorFrom(resId: Int): BitmapDescriptor? {
        return locationHelper.bitmapDescriptorFromVector(resId)
    }

    @VisibleForTesting
    fun injectLastLocation(location: LatLng) {
        lastLocation = location
    }
}
