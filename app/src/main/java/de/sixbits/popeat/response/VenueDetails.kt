package de.sixbits.popeat.response

data class VenueDetails(
    val name: String,
    val id: String,
    val bestPhoto: BestPhoto,
    val location: Location,
    val categories: List<Category>
)