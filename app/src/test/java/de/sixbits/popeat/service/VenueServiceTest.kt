package de.sixbits.popeat.service

import de.sixbits.popeat.VenueExploreResponseFactory
import de.sixbits.popeat.network.VenueRecommendationsQueryBuilder
import de.sixbits.popeat.network.VenueRepository
import io.reactivex.rxjava3.core.Observable
import org.junit.Test
import org.mockito.Mockito.*

class VenueServiceTest {

    @Test
    fun testGetVenueRecommendations() {
        val repo = mock(VenueRepository::class.java)
        `when`(repo.getVenueRecommendations(anyMap()))
            .thenReturn(Observable.just(VenueExploreResponseFactory.getVenueRecommendationsResponse()))

        val venueService = VenueService(repo)

        venueService.getVenueRecommendations(VenueRecommendationsQueryBuilder())
            .test()
            .assertValue {
                it.size == 2 &&
                        it[0].name == "Taco Palacio" &&
                        it[1].name == "Taco Maya"
            }
    }
}