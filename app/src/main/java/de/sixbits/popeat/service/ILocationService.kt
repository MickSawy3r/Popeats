package de.sixbits.popeat.service

import androidx.annotation.RequiresPermission
import com.google.android.gms.maps.model.LatLng
import io.reactivex.rxjava3.subjects.BehaviorSubject

abstract class ILocationService {

    val locationSubject: BehaviorSubject<LatLng> = BehaviorSubject.create()

    @RequiresPermission(
        anyOf = [
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION"
        ]
    )
    abstract fun startRequestingLocation()
}