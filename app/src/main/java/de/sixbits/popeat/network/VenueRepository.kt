package de.sixbits.popeat.network

import de.sixbits.popeat.response.FoursquareDetailsResponse
import de.sixbits.popeat.response.FoursquareExploreResponse
import de.sixbits.popeat.response.ResponseWrapper
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface VenueRepository {
    /**
     * Get venue recommendations.
     *
     * See [the docs](https://developer.foursquare.com/docs/api/venues/explore)
     */
    @GET("venues/explore")
    fun getVenueRecommendations(
        @QueryMap query: Map<String, String>
    ): Observable<ResponseWrapper<FoursquareExploreResponse>>

    @GET("venues/{id}")
    fun getVenueDetails(
        @Path("id") id: String,
        @QueryMap query: Map<String, String>
    ): Observable<ResponseWrapper<FoursquareDetailsResponse>>
}
