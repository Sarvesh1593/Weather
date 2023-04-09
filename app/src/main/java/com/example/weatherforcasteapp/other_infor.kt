package com.example.weatherforcasteapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherforcasteapp.databinding.ActivityOtherInforBinding
import java.text.SimpleDateFormat
import java.util.*

class other_infor : AppCompatActivity() {
    private lateinit var otherbinding: ActivityOtherInforBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        otherbinding = ActivityOtherInforBinding.inflate(layoutInflater)
        setContentView(otherbinding.root)
        supportActionBar?.hide()

        val temperature = WeatherData.temp
        val humidity = WeatherData.humidity
        val pressure =WeatherData.pressure
        val windSpeed= WeatherData.windspeed
        val dewPoint = WeatherData.dewPoint
        val sunrise=unixTime(WeatherData.sunrise)
        val sunset=unixTime(WeatherData.sunset)

        otherbinding.tvHumidity.text= "$humidity%"
        otherbinding.tvPressure.text= "$pressure mBar"
        otherbinding.tvWind.text= "$windSpeed km/h"
        otherbinding.tvDewPoint.text= String.format("%.2f°C",dewPoint)
        otherbinding.tvSunrise.text=sunrise
        otherbinding.tvSunset.text=sunset

    }
    private fun unixTime(timex: Long): String? {
        val date = Date(timex * 1000L )
        val sdf= SimpleDateFormat("HH:mm aaa", Locale.UK)
        sdf.timeZone= TimeZone.getDefault()
        return  sdf.format(date)
    }
}

