package dev.fest.patephone.model

import java.io.Serializable

data class Resume(
    val keyResume: String? = null,
    val namePersonResume: String? = null,
    val ageResume: String? = null,
    val titleResume: String? = null,
    val countryResume: String? = null,
    val cityResume: String? = null,
    val phoneResume: String? = null,
    val experienceResume: String? = null,
    val descriptionResume: String? = null,
    val imageResume: String? = null,
    val uid: String? = null,

    var favCounter: String = "0",
    var isFav: Boolean = false,

    var viewsCounter: String = "0",
    var emailsCounter: String = "0",
    var callsCounter: String = "0"
) : Serializable
