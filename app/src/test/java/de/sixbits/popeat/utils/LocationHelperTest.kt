package de.sixbits.popeat.utils

import com.google.android.gms.maps.model.LatLng
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class LocationHelperTest {

    @Test
    @DisplayName("Check Distance Calculator")
    fun getDistanceFromLatLonInKm() {
        val location1 = LatLng(41.878113, 87.629799)
        val location2 = LatLng(40.74224, 73.99386)

        val distance = LocationHelper.getDistanceFromLatLonInKm(location1, location2)

        assert(distance - 1144.0 < 1)
    }
}