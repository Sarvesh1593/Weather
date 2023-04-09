package com.example.weatherforcasteapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcasteapp.POJO.Weather

class days : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var weatherlist : List<Weather>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_days)

//        recyclerView = findViewById(R.id.recyclerview)
//
//        weatherlist=intent.getSerializableExtra("weatherList") as List<Weather>
//
//        val adapter = Weatheradapter(weatherlist)
//
//        recyclerView.adapter=adapter
//        recyclerView.layoutManager=LinearLayoutManager(this)
    }
}