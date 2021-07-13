package de.sixbits.popeat.network

class VenueRecommendationsQueryBuilder : PlacesQueryBuilder() {
    private var latitudeLongitude: String? = null
    private var near: String? = null
    private var section: String? = null

    fun setLatitudeLongitude(
        latitude: Double,
        longitude: Double
    ): VenueRecommendationsQueryBuilder {
        this.latitudeLongitude = "$latitude,$longitude"
        return this
    }

    fun setNearLocation(locationName: String): VenueRecommendationsQueryBuilder {
        this.near = locationName
        return this
    }

    fun setSection(query: String): VenueRecommendationsQueryBuilder {
        this.section = query
        return this
    }

    override fun putQueryParams(queryParams: MutableMap<String, String>) {
        latitudeLongitude?.apply { queryParams["ll"] = this }
        near?.apply { queryParams["near"] = this }
        section?.apply { queryParams["section"] = this }
    }
}
