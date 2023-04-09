package com.example.weatherforcasteapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.example.weatherforcasteapp.POJO.ModelClass
import com.example.weatherforcasteapp.WeatherData.humidity
import com.example.weatherforcasteapp.WeatherData.temp
import com.example.weatherforcasteapp.databinding.ActivityMainBinding
import com.example.weatherforcasteapp.utility.ApiInterface
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ln


class MainActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override  fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        supportActionBar?.hide()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()
        binding.btDays.setOnClickListener{
            val daysIntent= Intent(this,days::class.java)
            startActivity(daysIntent)
        }
        binding.btMoreInfo.setOnClickListener{
            val moreIntent=Intent(this,other_infor::class.java)
            startActivity(moreIntent)
        }

        binding.SearchPlace.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getCityWeather(binding.SearchPlace.text.toString()) {
                    val view = this.currentFocus
                    if (view != null) {
                        val imm: InputMethodManager =
                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                        binding.SearchPlace.clearFocus()
                    }
                }
                true
            } else {
                false
            }

        }


    }
    private val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val apiInterface= retrofit.create(ApiInterface::class.java)



    private fun getCurrentLocation() {
        if (checkPermission()) {

            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location = task.result
                    if (location == null) {
                        Toast.makeText(this, "Please select location ", Toast.LENGTH_SHORT).show()
                    } else {
                        fetchWeatherLocation(
                            location.latitude,
                            location.longitude
                        )
                    }
                }

            } else {

                // open setting permission here
                Toast.makeText(this, "Turn on location ", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)

            }
        } else {

            requestPermissions()
        }
    }




    private fun getCityWeather(cityname: String, function: () -> Unit) {
        apiInterface.getCityWeatherData(cityname, API_KEY).enqueue(object :Callback<ModelClass>{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<ModelClass>, response: Response<ModelClass>) {
                getviewData(response.body())
                updateWeatherIcon(response.body())

                val weatherResponse=response.body()
                if(weatherResponse != null){
                    showWeatherData(weatherResponse)
                }
            }

            override fun onFailure(call: Call<ModelClass>, t: Throwable) {
                Toast.makeText(applicationContext,"Not a Valid City Name",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun fetchWeatherLocation(latitude: Double, longitude: Double) {
        apiInterface.getCurrentWeatherData(latitude, longitude, API_KEY)
            .enqueue(object : Callback<ModelClass>{
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call<ModelClass>, response: Response<ModelClass>) {
                    if(response.isSuccessful){
                        getviewData(response.body())
                        updateWeatherIcon(response.body())

                        val weatherResponse=response.body()
                        if(weatherResponse != null){
                            showWeatherData(weatherResponse)
                        }
                    }
                }

                override fun onFailure(call: Call<ModelClass>, t: Throwable) {
                    Toast.makeText(applicationContext,"Error",Toast.LENGTH_SHORT).show()
                }
            })



  }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showWeatherData(weatherResponse: ModelClass) {
        temp = weatherResponse.main.temp
        humidity = weatherResponse.main.humidity
        WeatherData.pressure = weatherResponse.main.pressure
        WeatherData.windspeed = weatherResponse.wind.speed
        WeatherData.dewPoint = calculateDewPoint(temp, humidity)
        WeatherData.sunrise=weatherResponse.sys.sunrise
        WeatherData.sunset=weatherResponse.sys.sunset

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDewPoint(temp: Double, humidity: Int): Double {
        val a = 17.27
        val b = 237.7
        val alpha = ((a * kelvinToCelcius(temp)) / (b + kelvinToCelcius(temp))) + ln(humidity.toDouble() / 100)
        return (b * alpha) / (a - alpha)
    }


    private fun updateWeatherIcon(weatherData: ModelClass?) {
        val weatherConditionId=weatherData?.weather?.firstOrNull()?.id?:0
        val isDay = weatherData?.isDay()
        val iconUrl= isDay?.let { getIconUrlForCondition(weatherConditionId, it) }
        Glide.with(this).load(iconUrl).into(binding.tvView)

    }

    private fun getIconUrlForCondition(weatherConditionId: Int, isDay: Boolean): String {
        val prefix = if(isDay) "d" else "n"
        return when (weatherConditionId){
            in 200..232 -> "http://openweathermap.org/img/w/11${prefix}.png" // Thunderstorm
            in 300..321 -> "http://openweathermap.org/img/w/09${prefix}.png" // Drizzle
            in 500..531 -> "http://openweathermap.org/img/w/10${prefix}.png" // Rain
            in 600..622 -> "http://openweathermap.org/img/w/13${prefix}.png" // Snow
            800 -> "http://openweathermap.org/img/w/01${prefix}.png" // Clear
            in 801..804 -> "http://openweathermap.org/img/w/03${prefix}.png" // Clouds
            else -> "http://openweathermap.org/img/w/50${prefix}.png" // Mist
        }

    }


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getviewData(body: ModelClass?) {

        val sdf = SimpleDateFormat("d MMMM, HH:mm aaa")
        val currentDate = sdf.format(Date())
        binding.tvDtime.text = currentDate

        binding.tvMaxTemp.text = "Day " + kelvinToCelcius(body!!.main.temp_max).toString() + "°"
        binding.tvMinTemp.text = "Night " + kelvinToCelcius(body!!.main.temp_min).toString() + "°"
        binding.tvCurrentTemp.text = "" +   kelvinToCelcius(body!!.main.temp).toString()+ "°"
        binding.tvFeelsLike.text="Feels like "+ kelvinToCelcius(body!!.main.feels_like).toString() +"°"
        binding.tvWeatherType.text= body.weather[0].main
        val weatherId = body.weather[0].id
        updateThemeGif(weatherId)


    }




    private fun updateThemeGif(weatherId: Int?) {
        val themeGif=when(weatherId){
            in 200..232 -> R.drawable.thunderstrom // thunderstorm
            in 300..321 -> R.drawable.drizzle // drizzle
            in 500..531 -> R.drawable.rainy_gif // rain
            in 600..622 -> R.drawable.snow // snow
            in 701..781 -> R.drawable.fog // mist, smoke, haze, etc.
            800 -> R.drawable.clear3// clear sky // clouds
            in 801..804 ->R.drawable.cloudy // clouds
            else -> R.drawable.clear3
        }
        Glide.with(this@MainActivity).load(themeGif).into(binding.gifWeatherView)
    }





    // This function is use for changing from kelvin to

    @RequiresApi(Build.VERSION_CODES.O)
    private fun kelvinToCelcius(temp: Double): Double {
        var intTemp = temp
        intTemp = intTemp.minus(273)
        return intTemp.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
    }


    // This function is if permission is granted then we request the permission
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permission_request_access_location
        )
    }

    // This function is  for checking the permission and return ture and false
    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false


    }

    private fun isLocationEnabled(): Boolean {

        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    // this function is handle the permission request result weather permission is denied or granted
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permission_request_access_location) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val permission_request_access_location = 100
        const val API_KEY = "1db025ac283a13fd0f776ff93a691693"
    }

    private fun ModelClass.isDay() : Boolean{
        val sunriseTime = sys.sunrise*1000L
        val sunsetTime = sys.sunset*1000L
        val currentTime =Date().time
        return currentTime in sunriseTime until sunsetTime

    }


}
@GlideModule
class MyAppGlideModule : AppGlideModule() {
    // leave this class empty
}
