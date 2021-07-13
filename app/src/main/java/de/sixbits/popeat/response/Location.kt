package de.sixbits.popeat.response

data class Location(
    val address: String = "",
    val cc: String = "",
    val city: String = "",
    val country: String = "",
    val crossStreet: String = "",
    val distance: Int = 0,
    val formattedAddress: List<String> = listOf(),
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val postalCode: String = "123",
    val state: String = "CA"
)