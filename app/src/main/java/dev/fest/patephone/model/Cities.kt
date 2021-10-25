package dev.fest.patephone.model


import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Cities(
    @SerializedName("area")
    val area: Int?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("english")
    val english: String?,
    @SerializedName("full_name")
    val fullName: String?,
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Int?,
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("level")
    val level: Int?,
    @SerializedName("longitude")
    val longitude: Double?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("post")
    val post: Int?,
    @SerializedName("rajon")
    val rajon: Int?,
    @SerializedName("telcod")
    val telcod: Int?,
    @SerializedName("time_zone")
    val timeZone: Int?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("vid")
    val vid: Int?
)