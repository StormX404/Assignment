package com.example.assignment.data.models

data class CitiesResponse(
    val links: List<Link>,
    val data: List<CityData>,
    val metadata: Metadata
)

data class SimpleCity(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
)

data class Link(
    val rel: String,
    val href: String
)

data class CityData(
    val id: Int,
    val wikiDataId: String?,
    val type: String,
    val city: String,
    val name: String,
    val country: String,
    val countryCode: String,
    val region: String?,
    val regionCode: String?,
    val regionWdId: String?,
    val latitude: Double,
    val longitude: Double,
    val population: Int
)

data class Metadata(
    val currentOffset: Int,
    val totalCount: Int
)

fun CityData.toSimpleCity(): SimpleCity {
    return SimpleCity(
        name = this.name,
        country = this.country,
        latitude = this.latitude,
        longitude = this.longitude
    )
}
