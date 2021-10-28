package dev.fest.patephone.model

import java.io.Serializable

//раз ты сам даёшь название классу, пусть оно будет как-то более развёрнуто, Ad меня очень смущает
data class Ad(
    //что за ключ? не очевидно, что имеется в виду
    val key: String? = null,

    val country: String? = null,
    val city: String? = null,

    val phone: String? = null,
    // если это тип инструмента, то может, есть какой-то ограниченный набор типов, из которых можно выбрать?
    val type: String? = null,
    val nameInstrument: String? = null,

    val price: String? = null,
    val description: String? = null,

    // а если я хочу загрузить больше 3 фото? (=
    val mainImage: String? = null,
    val image2: String? = null,
    val image3: String? = null,


    val uid: String? = null,
    val email: String? = null,
    // что это за время?
    val time: String = "0",

    var favCounter: String = "0",

    // неоправданное сокращение
    var isFav: Boolean = false,

    var viewsCounter: String = "0",
    var emailsCounter: String = "0",
    var callsCounter: String = "0"

// вообще в модели не хватает ясности, было бы круто разграничить понятие Инструмент и Объявление,
// есть вещи, которые относятся к одному, а есть вещи, которые относятся ко второму
) : Serializable
