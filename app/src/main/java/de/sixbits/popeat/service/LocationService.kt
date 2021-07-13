package de.sixbits.popeat.service

import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.annotation.VisibleForTesting
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import de.sixbits.popeat.EspressoIdlingResource
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

private const val updateIntervalMilliseconds: Long = 5000
private const val locationAccuracy = LocationRequest.PRIORITY_HIGH_ACCURACY
private const val TAG = "LocationService"

class LocationService constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val settingsClient: SettingsClient
) : ILocationService() {

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationSettingsRequest: LocationSettingsRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationSettingsResponseTask: Task<LocationSettingsResponse>

    @RequiresPermission(
        anyOf = [
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION"
        ]
    )
    override fun startRequestingLocation() {
        Log.d(TAG, "startRequestingLocation: ")
        EspressoIdlingResource.increment()
        val myLooper = Looper.myLooper()

        createLocationRequest()
        createLocationSettingsRequest()
        createLocationCallback()

        Log.d(TAG, "startRequestingLocation: Assigning Task")
        locationSettingsResponseTask = settingsClient.checkLocationSettings(locationSettingsRequest)

        locationSettingsResponseTask.addOnSuccessListener {
            Log.d(TAG, "startRequestingLocation: Success")
            if (myLooper != null) {
                fusedLocationProviderClient
                    .requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        myLooper
                    )
            } else {
                Log.d(TAG, "startRequestingLocation: Null Looper!")
            }
        }

        locationSettingsResponseTask.addOnFailureListener { e ->
            EspressoIdlingResource.decrement()
            Log.d(TAG, "startRequestingLocation: OnError ${e.message}")
            locationSubject.onError(e)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun createLocationRequest() {
        val locationRequest = LocationRequest.create()
        locationRequest.interval = updateIntervalMilliseconds
        locationRequest.priority = locationAccuracy
        this.locationRequest = locationRequest
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.d(TAG, "onLocationResult: Got Location")
                EspressoIdlingResource.decrement()
                if (locationResult.locations.isEmpty()) {
                    locationSubject.onError(Exception("Can't Find User's Coordinates"))
                } else if (locationResult.lastLocation.latitude == 0.0 && locationResult.lastLocation.longitude == 0.0) {
                    locationSubject.onError(Exception("Can't Find User's Coordinates"))
                } else {
                    locationSubject.onNext(
                        LatLng(
                            locationResult.lastLocation.latitude,
                            locationResult.lastLocation.longitude
                        )
                    )
                }
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            }
        }
    }

    private fun createLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        locationSettingsRequest = builder.build()
    }

    @VisibleForTesting
    fun injectLocationResult(location: LocationResult) {
        locationCallback.onLocationResult(location)
    }
}