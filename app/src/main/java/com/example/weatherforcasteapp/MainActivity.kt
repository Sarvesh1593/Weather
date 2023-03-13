package com.example.weatherforcasteapp

import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    private var calendar: Calendar? = null
    private var dateFormat: SimpleDateFormat? = null
    private var date: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var dateTimeDisplay=findViewById<TextView>(R.id.tvDtime)
        calendar=Calendar.getInstance()
        dateFormat= SimpleDateFormat("d MMMM, h:mm aaa")
        date= dateFormat!!.format(calendar?.getTime())
        dateTimeDisplay.text = date

        if (!isLocationOn()){
            Toast.makeText(
                this,
                "You Location provider is turned off. Please turned on it",
                Toast.LENGTH_SHORT
            ).show()
            val intent= Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        else{
            Toast.makeText(
                this,
                "Your location provider is already on ",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private  fun isLocationOn(): Boolean{
        // this provides access to system location service
        val locationManager : LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }
}