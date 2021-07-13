package de.sixbits.popeat.response

data class VenueRecommendationGroup(
    val items: List<RecommendedItem>,
    val name: String,
    val type: String
)