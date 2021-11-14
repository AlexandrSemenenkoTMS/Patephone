package dev.fest.patephone.model

data class AdFilter(
    val time: String? = null,
//    val country : String? = null,
//    val city : String? = null,
//    val type : String? = null,

    val country_city_type_time : String? = null,
    val country_city_type_price_time : String? = null,
    val country_city_time : String? = null,
    val country_city_price_time : String? = null,
    val country_type_time : String? = null,
    val country_type_price_time : String? = null,
    val country_time : String? = null,
    val country_price_time : String? = null,

    val city_type_time : String? = null,
    val city_type_price_time : String? = null,
    val city_time : String? = null,
    val city_price_time : String? = null,

    val type_time : String? = null,
    val type_price_time : String? = null,
    val price_time : String? = null

    )
