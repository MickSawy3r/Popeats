package de.sixbits.popeat.mapper

object ErrorMapper {
    fun mapErrorCode(code: Int): String {
        return when(code) {
            401 -> "Invalid Auth, Please Contact Developer"
            429 -> "Daily Request Quota Exceeded"
            else -> "Unknown Error Code $code"
        }
    }
}