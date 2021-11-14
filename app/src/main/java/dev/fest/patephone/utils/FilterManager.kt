package dev.fest.patephone.utils

import dev.fest.patephone.model.Ad
import dev.fest.patephone.model.AdFilter
import java.lang.StringBuilder

object FilterManager {
    fun createFilter(ad: Ad): AdFilter {
        return AdFilter(
            ad.timePublishAd,
            "${ad.country}_${ad.city}_${ad.type}_${ad.timePublishAd}",
            "${ad.country}_${ad.city}_${ad.type}_${ad.price}_${ad.timePublishAd}",
            "${ad.country}_${ad.city}_${ad.timePublishAd}",
            "${ad.country}_${ad.city}_${ad.price}_${ad.timePublishAd}",
            "${ad.country}_${ad.type}_${ad.timePublishAd}",
            "${ad.country}_${ad.type}_${ad.price}_${ad.timePublishAd}",
            "${ad.country}_${ad.timePublishAd}",
            "${ad.country}_${ad.price}_${ad.timePublishAd}",
            "${ad.city}_${ad.type}_${ad.timePublishAd}",
            "${ad.city}_${ad.type}_${ad.price}__${ad.timePublishAd}",
            "${ad.city}_${ad.timePublishAd}",
            "${ad.city}_${ad.price}_${ad.timePublishAd}",
            "${ad.type}_${ad.timePublishAd}",
            "${ad.type}_${ad.price}_${ad.timePublishAd}",
            "${ad.price}_${ad.timePublishAd}"
        )
    }

    fun getFilter(filter: String): String {

        val stringBuilderNode = StringBuilder()
        val stringBuilderFilter = StringBuilder()
        val tempArray = filter.split("_")

        if (tempArray[0] != "empty") {
            stringBuilderNode.append("country_")
            stringBuilderFilter.append("${tempArray[0]}_")
        }
        if (tempArray[1] != "empty") {
            stringBuilderNode.append("city_")
            stringBuilderFilter.append("${tempArray[1]}_")
        }
        if (tempArray[2] != "empty") {
            stringBuilderNode.append("type_")
            stringBuilderFilter.append("${tempArray[2]}_")
        }
        if (tempArray[3] != "empty") {
            stringBuilderNode.append("price_")
            stringBuilderFilter.append("_${tempArray[3]}")
        }

        stringBuilderNode.append("time")

        return "$stringBuilderNode|$stringBuilderFilter"
    }
}