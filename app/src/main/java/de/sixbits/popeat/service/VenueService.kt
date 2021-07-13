package de.sixbits.popeat.service

import de.sixbits.popeat.data_model.VenueDataModel
import de.sixbits.popeat.mapper.VenueDataModelMapper
import de.sixbits.popeat.network.VenueRecommendationsQueryBuilder
import de.sixbits.popeat.network.VenueRepository
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

private const val TAG = "VenueService"

class VenueService @Inject constructor(private val venueRepository: VenueRepository) {

    fun getVenueRecommendations(query: VenueRecommendationsQueryBuilder): Observable<List<VenueDataModel>> {
        return venueRepository.getVenueRecommendations(query = query.build())
            .map { response ->
                return@map response.response.groups[0].items.map {
                    VenueDataModelMapper.venueToDataModel(it.venue)
                }
            }
    }

    /**
     * Emits Multiple Events for Multiple Venues Venues
     */
    fun getVenueDetails(
        venues: List<VenueDataModel>
    ): Observable<VenueDataModel> {
        val defaultQuery = VenueRecommendationsQueryBuilder().build()

        return Observable.fromIterable(venues.map { v ->
            v.id
        }).flatMap {
            venueRepository.getVenueDetails(it, defaultQuery)
                .map { response ->
                    VenueDataModelMapper.venueDetailsToDataModel(response.response.venue)
                }
        }
    }
}
