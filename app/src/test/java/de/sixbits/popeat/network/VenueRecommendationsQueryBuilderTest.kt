package de.sixbits.popeat.network

import org.junit.Test

class VenueRecommendationsQueryBuilderTest {

    @Test
    fun testQueryBuilder_testQueryParam() {
        // Given I set a query to the QueryBuilder
        val queryMap = VenueRecommendationsQueryBuilder()
            .setSection("food")
            .build()

        // Then I should have 3 keys for default values, and one for additional ones
        assert(queryMap.keys.size == 4)

        // And we the query is food
        assert(queryMap["section"] == "food")
    }

    @Test
    fun testQueryBuilder_testLLParam() {
        // Given I set a query to the QueryBuilder
        val queryMap = VenueRecommendationsQueryBuilder()
            .setLatitudeLongitude(123.0, 123.0)
            .build()

        // Then I should have 3 keys for default values, and one for additional ones
        assert(queryMap.keys.size == 4)

        // And that ll param is correct
        assert(queryMap["ll"] == "123.0,123.0")
    }

    @Test
    fun testQueryBuilder_testMultipleParams() {
        // Given I set 2 queries params to the QueryBuilder
        val queryMap = VenueRecommendationsQueryBuilder()
            .setLatitudeLongitude(123.0, 123.0)
            .setSection("food")
            .build()

        // Then I should have 3 keys for default values, and 2 additional ones
        assert(queryMap.keys.size == 5)

        // And that ll param is correct
        assert(queryMap["ll"] == "123.0,123.0")

        // And we the query is food
        assert(queryMap["section"] == "food")
    }
}