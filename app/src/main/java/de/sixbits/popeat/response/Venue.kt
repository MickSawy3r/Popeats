package de.sixbits.popeat.response

data class Venue(
    val id: String,
    val location: Location,
    val name: String,
    val popularityByGeo: Double,
    val categories: List<Category>
)
