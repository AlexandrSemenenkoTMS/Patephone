package dev.fest.patephone.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CityApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://geohelper.info/")
//        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val cityApiService = retrofit.create(CityApiService::class.java)
}