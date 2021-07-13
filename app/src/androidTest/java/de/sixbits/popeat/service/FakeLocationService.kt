package de.sixbits.popeat.service

import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class FakeLocationService @Inject constructor() : ILocationService() {

    override fun startRequestingLocation() {
        locationSubject.onNext(LatLng(51.5293759,-0.248224))
    }
}