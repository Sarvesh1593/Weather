package com.example.weatherforcasteapp.utility


import com.example.weatherforcasteapp.POJO.ModelClass
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query



    interface ApiInterface {
        @GET("weather")
        fun getCurrentWeatherData(
            @Query("lat") lat: Double,
            @Query("lon") lon: Double,
            @Query("appid") apiKey: String
        ): Call<ModelClass>


        @GET("weather")
        fun getCityWeatherData(
            @Query("q") cityname : String,
            @Query("APPID") api_key :String
        ):Call<ModelClass>

}