package de.sixbits.popeat.mapper

import de.sixbits.popeat.VenueExploreResponseFactory
import de.sixbits.popeat.response.*
import org.junit.Test

class VenueDataMapperTest {

    @Test
    fun testVenueMapper_shouldMapCorrectly() {
        val venue = VenueExploreResponseFactory
            .getVenueRecommendationsResponse().response.groups[0].items[0]

        val venueDataModel = VenueDataModelMapper.venueToDataModel(venue.venue)

        assert(venueDataModel.name == venue.venue.name)
        assert(venueDataModel.category == venue.venue.categories[0].name)
        assert(venueDataModel.geoLocation.latitude == venue.venue.location.lat)
        assert(venueDataModel.geoLocation.longitude == venue.venue.location.lng)
    }

    @Test
    fun testVenueMapper_withNoCategories_shouldMapCorrectly() {
        val venue = RecommendedItem(
            venue = Venue(
                categories = listOf(),
                id = "123",
                name = "Taco Palacio",
                popularityByGeo = 213.0,
                location = Location(
                    address = "",
                    cc = "",
                    city = "",
                    country = "",
                    crossStreet = "",
                    distance = 12,
                    formattedAddress = listOf(),
                    lat = 21.0,
                    lng = 12.0,
                    postalCode = "123",
                    state = ""
                )
            )
        )

        val venueDataModel = VenueDataModelMapper.venueToDataModel(venue.venue)

        assert(venueDataModel.name == "Taco Palacio")
        assert(venueDataModel.category == "Unknown")
        assert(venueDataModel.geoLocation.latitude == 21.0)
        assert(venueDataModel.geoLocation.longitude == 12.0)
    }
}