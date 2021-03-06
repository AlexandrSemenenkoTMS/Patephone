package dev.fest.patephone.model

import java.io.Serializable

data class Ad(
    val adKey: String? = null,
    val country: String? = null,
    val city: String? = null,
    val phone: String? = null,
    val type: String? = null,
    val nameInstrument: String? = null,
    val price: String? = null,
    val typeMoney: String? = null,
    val description: String? = null,
    val mainImage: String? = null,
    val image2: String? = null,
    val image3: String? = null,
    val uid: String? = null,
    val email: String? = null,
    val nameAccount: String? = null,
    val avatarAccount: String? = null,
    val timePublishAd: String = "0",

    var favCounter: String = "0",
    var isFavourite: Boolean = false,

    var viewsCounter: String = "0",
    var emailsCounter: String = "0",
    var callsCounter: String = "0"
) : Serializable
