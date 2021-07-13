package de.sixbits.popeat.network

import de.sixbits.popeat.ResponseFactory
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class VenueRepositoryTest {
    lateinit var webServer: MockWebServer

    @Before
    fun setUp() {
        // Mocking the server
        webServer = MockWebServer()
        webServer.start()
    }

    @After
    fun cleanUp() {
        webServer.shutdown()
    }

    @Test
    fun testGetVenueRecommendations_RepoMapsResponseCorrectly() {
        // Given the Venue Server is up
        val response = MockResponse()
            .setBody(ResponseFactory.getGoodRawResponse())
        webServer.enqueue(response)

        // When Requesting near venues
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(webServer.url("localhost/"))
            .build()
        val venueRepository = retrofit.create(VenueRepository::class.java)
        val request = VenueRecommendationsQueryBuilder()
            .setLatitudeLongitude(1123.0, 123.0)
            .setSection("places")

        // Then I should get a response with appropriate fields
        venueRepository.getVenueRecommendations(request.build())
            .test()
            .assertValue {
                it.response.groups[0].items[0].venue.name == "TWIXI" &&
                        it.response.groups[0].items[0].venue.location.address == "ALHAMRA" &&
                        it.response.groups[0].items[0].venue.location.lat == 34.735410061994706 &&
                        it.response.groups[0].items[0].venue.location.lng == 36.70080006122589
            }
    }

    @Test
    fun testGetVenueRecommendations_RepoHandleError() {
        // Given the Venue Server is up
        val response = MockResponse()
            .setBody(ResponseFactory.getBadRowResponse())
            .setResponseCode(400)

        webServer.enqueue(response)

        // When Requesting near venues
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(webServer.url("localhost/"))
            .build()
        val venueRepository = retrofit.create(VenueRepository::class.java)
        val request = VenueRecommendationsQueryBuilder()
            .setLatitudeLongitude(1123.0, 123.0)
            .setSection("places")

        // Then I should get a response with appropriate fields
        venueRepository.getVenueRecommendations(request.build())
            .test()
            .assertError {
                it.message == "HTTP 400 Client Error"
            }
    }
}
