package com.example.rmas

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Timer
import java.util.TimerTask

class LocationService : Service() {
    private lateinit var serviceHandler: ServiceHandler

    private val job = SupervisorJob()

    companion object ACTIONS {
        const val START = "Start"
        const val STOP = "Stop"
    }

    private val monitoringChannelId = "MonitoringChannelId"
    private val proximityAlertChannelId = "ProximityAlertChannelId"


    private inner class ServiceHandler(
        locationClient: FusedLocationProviderClient,
        scope: CoroutineScope,
        looper: Looper
    ) : Handler(looper) {
        private val timer = Timer()
        private val work: TimerTask = Work(locationClient, scope)

        private var started = false

        override fun handleMessage(msg: Message) {
            if (!started) {
                timer.schedule(work, 0L, 5000L)
                started = true
            }
        }
    }

    private inner class Work(
        private val locationClient: FusedLocationProviderClient,
        private  val scope: CoroutineScope
    ): TimerTask() {
        override fun run() {
            if (ContextCompat.checkSelfPermission(
                this@LocationService,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                this@LocationService,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
                scope.launch {
                    try {
                        val location = locationClient.lastLocation.await()
                        Log.i("LocationService", location.latitude.toString() + " " + location.longitude.toString())
                        showNotification()
                    } catch (e: Exception) {
                        Log.e("LocationService", e.toString())
                    }
                }
            } else {
                Log.e("LocationService", "No location permission")
            }
        }
    }

    override fun onCreate() {
        val handlerThread = HandlerThread(
            "Location service thread",
            Process.THREAD_PRIORITY_BACKGROUND
        )
        handlerThread.start()

        serviceHandler = ServiceHandler(
            locationClient = LocationServices.getFusedLocationProviderClient(this),
            scope = CoroutineScope(Dispatchers.IO + job),
            looper = handlerThread.looper,
        )

        createMonitoringNotificationChannel()
        createProximityAlertNotificationChannel()

        startForeground("MonitoringNotification".hashCode(), createMonitoringNotification())
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i("LocationService", "Service starting")

        Log.i("LocationService", intent.action ?: "No Action")
        Log.i("LocationService", intent.toString())

        when (intent.action) {
            null, START -> {
                Log.i("LocationService", intent.toString())
                serviceHandler.sendMessage(serviceHandler.obtainMessage())
            }
            STOP -> {
                val isStopped = stopSelfResult(startId)

                Log.i("LocationService", if (isStopped) "Stopped" else "Not stopped")
            }
            else -> {
                throw Exception("Unsupported action")
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()

        Log.i("LocationService", "Service destroying")
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createMonitoringNotification(): Notification {
        val openIntent = Intent(this, MainActivity::class.java)
        val stopIntent = Intent(this, LocationService::class.java).apply {
            action = STOP
        }

        val pendingOpenIntent = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_IMMUTABLE)
        val pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, monitoringChannelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("asgasgdfgas")
            .setContentText("asdasfs")
            .setOngoing(true)
            .addAction(R.drawable.tableoutlined, "Open app", pendingOpenIntent)
            .addAction(R.drawable.tableoutlined, "Stop", pendingStopIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
    }

    private fun showNotification() {
        val builder = NotificationCompat.Builder(this, proximityAlertChannelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("asgasgdfgas")
            .setContentText("asdasfs")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationId = (Math.random() * 100).toInt()

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@LocationService,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            notify(notificationId, builder.build())
        }
    }

//    ProximityAlertNotificationChannel
//    MonitoringNotificationChannel

    private fun createMonitoringNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                /* id = */ monitoringChannelId,
                /* name = */ getString(R.string.monitoring_channel_name),
                /* importance = */ NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.monitoring_channel_description)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createProximityAlertNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                /* id = */ proximityAlertChannelId,
                /* name = */ getString(R.string.proximity_alert_channel_name),
                /* importance = */ NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.proximity_alert_channel_description)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

//private enum class LocationServiceMessage {
//    START,
//    STOP,
//}

//private fun String?.toLocationServiceMessage(): LocationServiceMessage {
//    return when(this) {
//        LocationService.START -> {
//            LocationServiceMessage.START
//        }
//        LocationService.STOP -> {
//            LocationServiceMessage.STOP
//        }
//        else -> {
//            throw Exception("Unsupported or null action")
//        }
//    }
//}