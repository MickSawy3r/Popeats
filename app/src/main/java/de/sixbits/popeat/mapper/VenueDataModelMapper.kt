package de.sixbits.popeat.mapper

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import de.sixbits.popeat.data_model.VenueDataModel
import de.sixbits.popeat.response.Venue
import de.sixbits.popeat.response.VenueDetails
import kotlin.math.roundToInt

private const val TAG = "VenueDataModelMapper"

/**
 * This class map Venue Responses into VenueDataModel.
 * This layer is here for 2 reasons
 * 1. The UI should only get what it needs from the Service/Domain Layer.
 *    It's preferable that no over fetching should happen in the View/ViewModel
 *    Layer. This class create a consolidated simpler view of the data that is ALL
 *    Used inside the View/ViewModel Layer.
 *
 * 2. Even though the current API is fairly stable, we don't want to have
 *    a dependency on the vendor. This is why we should separate any data model
 *    from their response counterpart.
 */
object VenueDataModelMapper {

    fun venueDetailsToDataModel(venueDetails: VenueDetails): VenueDataModel {
        val imageLink = try {
            "${venueDetails.bestPhoto.prefix}original${venueDetails.bestPhoto.suffix}"
        } catch (e: Exception) {
            Log.d(TAG, "toVenueDataModel: ${e.message}")
            ""
        }

        var category = "Unknown"

        if (venueDetails.categories.isNotEmpty()) {
            category = venueDetails.categories[0].name
        }

        val distanceInKM = venueDetails.location.distance / 1000
        Log.d(TAG, "venueDetailsToDataModel: $distanceInKM ${venueDetails.location.distance}")

        return VenueDataModel(
            id = venueDetails.id,
            name = venueDetails.name,
            geoLocation = LatLng(
                venueDetails.location.lat,
                venueDetails.location.lng
            ),
            distance = distanceInKM.toString(),
            location = "${venueDetails.location.address} ",
            category = category,
            image = imageLink
        )
    }

    fun venueToDataModel(venue: Venue): VenueDataModel {
        val imageLink = ""

        var category = "Unknown"

        if (venue.categories.isNotEmpty()) {
            category = venue.categories[0].name
        }

        val distanceInKM = venue.location.distance / 1000

        return VenueDataModel(
            id = venue.id,
            name = venue.name,
            distance = distanceInKM.toString(),
            geoLocation = LatLng(
                venue.location.lat,
                venue.location.lng
            ),
            location = "${venue.location.address} ",
            category = category,
            image = imageLink
        )
    }
}
