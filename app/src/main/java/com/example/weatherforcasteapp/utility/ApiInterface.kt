package com.example.weatherforcasteapp.utility

import com.example.weatherforcasteapp.POJO.ModelClass
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query



    interface ApiInterface {
        @GET("weather")
        fun getCurrentWeatherData(
            @Query("lat") lat: String,
            @Query("lon") lon: String,
            @Query("appid") apiKey: String
        ): Call<ModelClass>


    @GET("weather")
    fun getCityWeatherData(
        @Query("q") city_name : String,
        @Query("APPID") api_key :String
    ):Call<ModelClass>
}