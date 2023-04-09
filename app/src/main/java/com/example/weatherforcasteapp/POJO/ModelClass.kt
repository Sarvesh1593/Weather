package com.example.weatherforcasteapp.POJO

import com.google.gson.annotations.SerializedName

data class ModelClass(
    @SerializedName ("weather") val weather :List<Weather>,
    @SerializedName ("weatherData") val weatherData: WeatherData,
    @SerializedName ("main") val main :Main,
    @SerializedName ("wind") val wind : Wind,
    @SerializedName ("sys") val sys : Sys

)