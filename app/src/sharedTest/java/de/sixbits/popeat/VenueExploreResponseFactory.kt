package de.sixbits.popeat

import de.sixbits.popeat.response.*

object VenueExploreResponseFactory {

    fun getVenueRecommendationsResponse(): ResponseWrapper<FoursquareExploreResponse> {
        return ResponseWrapper(
            response = FoursquareExploreResponse(
                groups = listOf(
                    VenueRecommendationGroup(
                        items = getRecommendationList(),
                        name = "Name",
                        type = "Type"
                    )
                )
            )
        )
    }

    private fun getRecommendationList(): List<RecommendedItem> {
        return listOf(
            RecommendedItem(
                venue = Venue(
                    name = "Taco Palacio",
                    id = "1",
                    categories = listOf(
                        Category(
                            id = "1",
                            icon = CategoryIcon(prefix = "", suffix = ""),
                            name = "Food"
                        )
                    ),
                    location = Location(),
                    popularityByGeo = 1.0
                )
            ), RecommendedItem(
                venue = Venue(
                    name = "Taco Maya",
                    id = "2",
                    categories = listOf(
                        Category(
                            id = "1",
                            icon = CategoryIcon(prefix = "", suffix = ""),
                            name = "Food"
                        )
                    ),
                    location = Location(),
                    popularityByGeo = 1.0
                )
            )
        )
    }

}