package de.sixbits.popeat.utils

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class LocationHelper @Inject constructor(private val application: Application) {

    /**
     * Check for the three permissions that the application requires to operate
     * This is just a short hand
     */
    val hasLocationPermissions
        get() = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.INTERNET
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED


    fun bitmapDescriptorFromVector(
        vectorResId: Int
    ): BitmapDescriptor? {
        return ContextCompat.getDrawable(application, vectorResId)?.run {
            val width = intrinsicWidth * 2
            val height = intrinsicHeight * 2
            setBounds(0, 0, width, height)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    companion object {
        /**
         * Calculates the distance between 2 points on earth.
         * @return Double Distance In KM
         */
        fun getDistanceFromLatLonInKm(location1: LatLng, location2: LatLng): Double {
            val r = 6371 // Radius of the earth in km
            val dLat = deg2rad(location2.latitude - location1.latitude)  // deg2rad below
            val dLon = deg2rad(location2.longitude - location1.longitude)
            val a =
                sin(dLat / 2) * sin(dLat / 2) +
                        cos(deg2rad(location1.latitude)) * cos(deg2rad(location2.latitude)) *
                        sin(dLon / 2) * sin(dLon / 2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            // Distance in km
            return r * c
        }

        private fun deg2rad(deg: Double): Double {
            return deg * (Math.PI / 180)
        }
    }
}
