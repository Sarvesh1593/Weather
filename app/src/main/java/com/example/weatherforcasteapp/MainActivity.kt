package com.example.weatherforcasteapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weatherforcasteapp.POJO.ModelClass
import com.example.weatherforcasteapp.databinding.ActivityMainBinding
import com.example.weatherforcasteapp.utility.ApiInterface
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
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


    }





    private fun getCurrentLocation() {
        if (checkPermission()) {

            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location = task.result
                    if (location == null) {
                        Toast.makeText(this, "Please select location ", Toast.LENGTH_SHORT).show()
                    } else {
                        fetchWeatherLocation(
                            location.latitude.toString(),
                            location.longitude.toString()
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

    val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val apiInterface= retrofit.create(ApiInterface::class.java)


    private fun fetchWeatherLocation(latitude: String, longitude: String) {
        apiInterface.getCurrentWeatherData(latitude, longitude, API_KEY)
            .enqueue(object : Callback<ModelClass>{
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call<ModelClass>, response: Response<ModelClass>) {
                    if(response.isSuccessful){
                        getviewData(response.body())
                    }
                }

                override fun onFailure(call: Call<ModelClass>, t: Throwable) {
                    Toast.makeText(applicationContext,"Error",Toast.LENGTH_SHORT).show()
                }
            })


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getviewData(body: ModelClass?) {

        val sdf = SimpleDateFormat("d MMMM, h:mm aaa")
        val currentDate = sdf.format(Date())
        binding.tvDtime.text = currentDate

        binding.tvMaxTemp.text = "Day " + kelvinToCelcius(body!!.main.temp_max).toString() + "°"
        binding.tvMinTemp.text = "Night " + kelvinToCelcius(body!!.main.temp_min).toString() + "°"
        binding.tvCurrentTemp.text = "" + kelvinToCelcius(body!!.main.temp).toString() + "°"
        binding.tvFeelsLike.text="Feels like "+kelvinToCelcius(body!!.main.feels_like).toString()+"°"
        binding.tvWeatherType.text = body.weather[0].main

        updateUI(body.weather[0].id)


    }
    private fun updateUI(id: Int) {
        when (id) {
            in 200..232 -> {
                binding.gifWeatherView.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.thunderstrom
                )

            }
            in 300..321 -> {
                binding.gifWeatherView.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.drizzle
                )
            }
            in 500..531 -> {
                binding.gifWeatherView.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.rainy_gif
                )
            }
            in 600..620 -> {
                binding.gifWeatherView.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.snow
                )
            }
            in 701..781 -> {
                binding.gifWeatherView.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.fog
                )
            }
            800 -> {
                binding.gifWeatherView.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.clear
                )
            }
            else -> {
                binding.gifWeatherView.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.clear3
                )
            }
        }
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
        const val API_KEY = "77fe8040e5909fc0b725efaac67e5794"
    }


}