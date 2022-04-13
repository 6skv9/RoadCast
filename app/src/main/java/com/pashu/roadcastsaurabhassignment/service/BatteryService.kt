package com.pashu.roadcastsaurabhassignment.service

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class BatteryService : Service() {

    var notification: Notification.Builder? = null
    var batteryStatus: Intent? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread {
            while (true) {
                try {
                    notification?.let {
                        batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
                            applicationContext.registerReceiver(null, ifilter)
                        }
                        val batteryPct: Int? = batteryStatus?.let { intent ->
                            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                            level * 100 / scale
                        }
                        val batteryTmp: Float? = batteryStatus?.let { intent ->
                            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
                            level/10.toFloat()
                        }
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                        val time = current.format(formatter)
                        val text = "Battery is $batteryPct Percent and Temperature is $batteryTmp Degree Celsius at $time."
                        it.setStyle(Notification.BigTextStyle().bigText(text))
                        it.setContentText(text)
                        startForeground(1001, it.build())
                    }
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()

        val CHANNELID = "Foreground Service ID"
        val channel = NotificationChannel(
            CHANNELID,
            CHANNELID,
            NotificationManager.IMPORTANCE_LOW
        )

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        notification = Notification.Builder(this, CHANNELID)
            .setContentTitle("Roadcast")
            .setSmallIcon(R.drawable.alert_dark_frame)



        return super.onStartCommand(intent, flags, startId)
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}