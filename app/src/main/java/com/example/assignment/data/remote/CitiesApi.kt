package com.example.assignment.data.remote

import com.example.assignment.data.models.CitiesResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface CitiesApi {

    @Headers(
        "x-rapidapi-host: wft-geo-db.p.rapidapi.com",
        "x-rapidapi-key: 3a6b659258msh9d0e6c70343266dp1957abjsn85d9b630d21a"
    )
    @GET("cities")
    suspend fun getCities(
        @Query("namePrefix") namePrefix: String,
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0
    ): CitiesResponse
}
