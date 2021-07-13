package de.sixbits.popeat.service

import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Tasks
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class LocationServiceTest {

    @Before
    fun setUp() {
        // For the PublishSubject
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun testLocation_gpsWorks_ShouldFindTheLocation() {
        // Given I have access to the GPS
        val fusedLocationProviderClient = mock(FusedLocationProviderClient::class.java)
        val settingsClient = mock(SettingsClient::class.java)
        val locationService = LocationService(fusedLocationProviderClient, settingsClient)

        `when`(settingsClient.checkLocationSettings(any(LocationSettingsRequest::class.java)))
            .thenReturn(Tasks.forResult(LocationSettingsResponse()))

        // And I'm at point 23.0,45.0 in Degrees
        val lat = 23.0
        val lon = 45.0
        val location = object : Location(LocationManager.GPS_PROVIDER) {
            override fun getLatitude(): Double {
                return lat
            }

            override fun getLongitude(): Double {
                return lon
            }
        }
        `when`(fusedLocationProviderClient.lastLocation)
            .thenReturn(Tasks.forResult(location))
        val locationResult = LocationResult.create(listOf(location))


        // When I request my Location from the service
        locationService.startRequestingLocation()

        // And the GPS Sends the app the location
        locationService.injectLocationResult(locationResult)

        // Then I should find these values in last location field
        assert(locationService.locationSubject.hasValue())
        assert(locationService.locationSubject.value.latitude == lat) {
            "lat should be $lat, current ${locationService.locationSubject.value.latitude}"
        }
        assert(locationService.locationSubject.value.longitude == lon) {
            "lon should be $lon, current ${locationService.locationSubject.value.longitude}"
        }
    }

    @Test
    fun testLocation_gpsDoesNotWork_ShouldGetAnError() {
        // Given I have access to the GPS
        val fusedLocationProviderClient = mock(FusedLocationProviderClient::class.java)
        val settingsClient = mock(SettingsClient::class.java)
        val locationService = LocationService(fusedLocationProviderClient, settingsClient)

        `when`(settingsClient.checkLocationSettings(any(LocationSettingsRequest::class.java)))
            .thenReturn(Tasks.forResult(LocationSettingsResponse()))

        // But the GPS is busted
        `when`(fusedLocationProviderClient.lastLocation)
            .thenReturn(Tasks.forResult(null))
        val locationResult = LocationResult.create(listOf())


        // When I request my Location from the service
        locationService.startRequestingLocation()

        // And the GPS Sends the app the location
        locationService.injectLocationResult(locationResult)

        // Then I should find an error explaining the error
        assert(locationService.locationSubject.throwable.message == "Can't Find User's Coordinates")
    }
}
