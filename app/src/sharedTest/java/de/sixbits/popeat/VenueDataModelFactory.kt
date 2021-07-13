package de.sixbits.popeat

import com.google.android.gms.maps.model.LatLng
import de.sixbits.popeat.data_model.VenueDataModel

object VenueDataModelFactory {
    fun getVenueList(): List<VenueDataModel> {
        return listOf(
            getVenueItem1(),
            getVenueItem2()
        )
    }

    fun getVenueItem1(): VenueDataModel {
        return VenueDataModel(
            name = "Place 01",
            geoLocation = LatLng(12.0, 12.0),
            location = "Address",
            category = "Persian Food",
            image = "https://google.com/images.jpg",
            id = "1"
        )
    }

    fun getVenueItem2(): VenueDataModel {
        return VenueDataModel(
            name = "Place 01",
            geoLocation = LatLng(12.0, 12.0),
            location = "Address",
            category = "Persian Food",
            image = "https://google.com/images.jpg",
            id = "2"
        )
    }
}