package dev.fest.patephone.remote

import dev.fest.patephone.model.Cities
import retrofit2.Call
import retrofit2.http.GET

interface CityApiService {
    @GET("json/geo/city_list?country=BLR&api_key=ddc4b0b78d64bfaff051940aef29e2ef")
    fun getAllPCities(): Call<List<Cities>>
}