package dev.fest.patephone.utils

import android.content.Context
import android.widget.Toast
import dev.fest.patephone.remote.CityApiClient
import dev.fest.patephone.remote.CityApiService
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

object CityHelper {

    fun getAllCountries(context: Context): ArrayList<String> {
        var tempArray = ArrayList<String>()
        try {
//            val inputStream: InputStream = context.assets.open("citiesOfBelarus.json")
            val inputStream: InputStream = context.assets.open("countriesToCities.json")
            val inputStreamSize: Int = inputStream.available()
            val bytesArray = ByteArray(inputStreamSize)
            inputStream.read(bytesArray)
            val jsonFile = String(bytesArray)
            val jsonObject = JSONObject(jsonFile)
//            jsonObject.getJSONArray("Беларусь")
            jsonObject.getJSONArray("Беларусь")
            val countriesNames = jsonObject.names()
            if (countriesNames != null) {
                for (n in 0 until countriesNames.length()) {
                    tempArray.add(countriesNames.getString(n))
                }
            }
        } catch (e: IOException) {

        }
        return tempArray
    }

    fun getAllCities(country: String, context: Context): ArrayList<String> {
        var tempArray = ArrayList<String>()
        try {
//            val inputStream: InputStream = context.assets.open("citiesOfBelarus.json")
            val inputStream: InputStream = context.assets.open("countriesToCities.json")
            val inputStreamSize: Int = inputStream.available()
            val bytesArray = ByteArray(inputStreamSize)
            inputStream.read(bytesArray)
            val jsonFile = String(bytesArray)
            val jsonObject = JSONObject(jsonFile)
            jsonObject.getJSONArray("Беларусь")
            val cityNames = jsonObject.getJSONArray(country)
            for (n in 0 until cityNames.length()) {
                tempArray.add(cityNames.getString(n))
            }
        } catch (e: IOException) {

        }
        return tempArray
    }

    fun filterListData(list: ArrayList<String>, searchText: String?): ArrayList<String> {
        val tempList = ArrayList<String>()
        tempList.clear()
        if (searchText == null) {
            tempList.add("Нет результата")
            return tempList
        }
        for (selection in list) {
            if (selection.lowercase(Locale.ROOT).startsWith(searchText.lowercase(Locale.ROOT))) {
                tempList.add(selection)
            }
        }
        if (tempList.isEmpty())
            tempList.add("Нет результатов")
        return tempList
    }
}