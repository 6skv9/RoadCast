package com.pashu.roadcastsaurabhassignment


import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.pashu.roadcastsaurabhassignment.databinding.ActivityMainBinding
import com.pashu.roadcastsaurabhassignment.service.BatteryService
import com.pashu.roadcastsaurabhassignment.ui.EntryActivity
import com.pashu.roadcastsaurabhassignment.ui.LiveLocationActivity


class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            start()
        }

        binding.apiEntries.setOnClickListener{
            val intent = Intent(this, EntryActivity::class.java)
            startActivity(intent)
        }

        binding.liveLocation.setOnClickListener{
            val intent = Intent(this, LiveLocationActivity::class.java)
            startActivity(intent)
        }

        Toast.makeText(this,"Please Check Notification for Battery and Temperature.",Toast.LENGTH_LONG)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun start(){
        if(!foregroundServiceRunning()){
            val serviceIntent = Intent(this, BatteryService::class.java)
            startForegroundService(serviceIntent)
        }
    }

    fun foregroundServiceRunning(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (BatteryService::class.java.getName() == service.service.className) {
                return true
            }
        }
        return false
    }

}
