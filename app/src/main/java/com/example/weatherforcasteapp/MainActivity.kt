package com.example.weatherforcasteapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.weatherforcasteapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.w3c.dom.Text
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
        var dateTimeDisplay=binding.tvDtime
        calendar=Calendar.getInstance()
        dateFormat= SimpleDateFormat("d MMMM, h:mm aaa")
        date= dateFormat!!.format(calendar?.getTime())
        dateTimeDisplay.text = date

        mFusedLocationClient=LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocation()


    }

    private fun getCurrentLocation(){
        if(checkPermission()){

//            if(isLocationEnabled()){
//
//            }
//            else{
//
//                // open setting permission here
//
//            }
        }
        else{

            requestPermissions()
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
        arrayOf( android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION),
            permission_request_access_location
        )
    }

    private fun checkPermission(): Boolean  {
        if(ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
         && ActivityCompat.checkSelfPermission(this,
        android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false


    }

//     private fun isLocationEnabled(): Boolean {
//
//     }


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
        }
    }
    companion object{
        private const val permission_request_access_location=100
    }



}