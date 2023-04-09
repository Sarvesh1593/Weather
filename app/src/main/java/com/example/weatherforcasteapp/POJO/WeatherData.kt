package com.example.weatherforcasteapp.POJO

import java.io.Serializable

data class WeatherData(
    val temperature: Double,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val dewPoint: Double
): Serializable
