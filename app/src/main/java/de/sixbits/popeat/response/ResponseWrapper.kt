package de.sixbits.popeat.response

import com.squareup.moshi.Json

data class ResponseWrapper<T>(
    @Json(name = "response")
    val response: T
)