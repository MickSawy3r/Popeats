package de.sixbits.popeat.data_model

import com.google.android.gms.maps.model.LatLng

data class VenueDataModel(
    val id: String,
    val name: String,
    val geoLocation: LatLng,
    val location: String,
    val category: String,
    val image: String,
    val distance: String = "0"
)
