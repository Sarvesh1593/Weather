package com.example.weatherforcasteapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import com.example.weatherforcasteapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    private var calendar: Calendar? = null
    private var dateFormat: SimpleDateFormat? = null
    private var date: String? = null
    private lateinit var mFusedLocationClient : FusedLocationProviderClient
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding= ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        supportActionBar?.hide()
        val dateTimeDisplay=binding.tvDtime
        calendar=Calendar.getInstance()
        dateFormat= SimpleDateFormat("d MMMM, h:mm aaa")
        date= dateFormat!!.format(calendar?.getTime())
        dateTimeDisplay.text = date

        mFusedLocationClient=LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocation()


    }

    private fun getCurrentLocation(){
        if(checkPermission()){

            if(isLocationEnabled()){

                mFusedLocationClient.lastLocation.addOnCompleteListener(this){
                    task -> val location =task.result
                    if(location == null){
                        Toast.makeText(this,"Please select location ",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        fetchWeatherLocation(
                            location.latitude.toString(),
                            location.longitude.toString())
                    }
                }

            }
            else{

                // open setting permission here
                Toast.makeText(this,"Turn on location ",Toast.LENGTH_SHORT).show()
                val intent=Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)

            }
        }
        else{

            requestPermissions()
        }
    }

    private fun fetchWeatherLocation(latitude: String, longitude: String) {


    }

    // This function is if permission is granted then we request the permission
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
        arrayOf( android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION),
            permission_request_access_location
        )
    }

    // This function is  for checking the permission and return ture and false
    private fun checkPermission(): Boolean  {
        if(ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
         && ActivityCompat.checkSelfPermission(this,
        android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false


    }

     private fun isLocationEnabled(): Boolean {

         val locationManager : LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
        if (requestCode== permission_request_access_location ){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Granted",Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }
            else{
                Toast.makeText(this,"Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    companion object{
        private const val permission_request_access_location=100
    }



}