package com.vjpro.tindow.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.vjpro.tindow.data.model.Option
import com.vjpro.tindow.data.model.OptionSource
import retrofit2.http.GET
import retrofit2.http.Query

/** Geoapify Places API — free 3K credits/day. Get key at https://myprojects.geoapify.com/ */
interface GeoapifyApiService {

    @GET("v2/places")
    suspend fun getNearbyPlaces(
        @Query("categories") categories: String = "catering.restaurant,catering.cafe,entertainment,tourism",
        @Query("filter") filter: String,  // "circle:{lon},{lat},{radiusMeters}"
        @Query("limit") limit: Int = 10,
        @Query("apiKey") apiKey: String = GeoapifyConfig.API_KEY
    ): GeoapifyResponse
}

/** Replace with your real key from https://myprojects.geoapify.com/ */
object GeoapifyConfig {
    val API_KEY: String = com.vjpro.tindow.BuildConfig.GEOAPIFY_API_KEY
    const val BASE_URL = "https://api.geoapify.com/"

    fun isConfigured(): Boolean = API_KEY.isNotBlank()

    /** Build circle filter for nearby search */
    fun circleFilter(lat: Double, lon: Double, radiusMeters: Int = 5000): String {
        return "circle:$lon,$lat,$radiusMeters"
    }
}

@JsonClass(generateAdapter = false)
data class GeoapifyResponse(
    @Json(name = "features") val features: List<GeoapifyFeature>?
)

@JsonClass(generateAdapter = false)
data class GeoapifyFeature(
    @Json(name = "properties") val properties: GeoapifyProperties
)

@JsonClass(generateAdapter = false)
data class GeoapifyProperties(
    @Json(name = "name") val name: String?,
    @Json(name = "street") val street: String?,
    @Json(name = "city") val city: String?,
    @Json(name = "categories") val categories: List<String>?,
    @Json(name = "datasource") val datasource: GeoapifyDatasource?
)

@JsonClass(generateAdapter = false)
data class GeoapifyDatasource(
    @Json(name = "raw") val raw: Map<String, Any>?
)

/** Convert Geoapify place to app Option */
fun GeoapifyFeature.toOption(): Option? {
    val name = properties.name ?: return null
    val desc = listOfNotNull(
        properties.categories?.firstOrNull()?.substringAfter(".")?.replace("_", " "),
        properties.street,
        properties.city
    ).joinToString(" • ")

    return Option(
        id = "place_${name.hashCode()}",
        title = name,
        description = desc.ifEmpty { null },
        source = OptionSource.PLACE_API
    )
}
