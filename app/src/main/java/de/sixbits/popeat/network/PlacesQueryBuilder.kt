package de.sixbits.popeat.network

import de.sixbits.popeat.BuildConfig
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.HashMap
import kotlin.collections.Map
import kotlin.collections.MutableMap
import kotlin.collections.mapOf
import kotlin.collections.set

abstract class PlacesQueryBuilder {
    private val baseQueryParams by lazy {
        mapOf(
            "client_id" to BuildConfig.CLIENT_ID,
            "client_secret" to BuildConfig.CLIENT_SECRET
        )
    }

    fun build(): Map<String, String> {
        val queryParams = HashMap(baseQueryParams)
        queryParams["v"] = dateFormat.format(Date())
        putQueryParams(queryParams)
        return queryParams
    }

    abstract fun putQueryParams(queryParams: MutableMap<String, String>)

    companion object {
        private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.ROOT)
    }
}
