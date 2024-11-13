package com.example.rmas

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.rmas.models.MapItem
import com.example.rmas.repositories.ServiceLocator
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

    companion object ACTIONS {
        const val START = "Start"
        const val STOP = "Stop"
    }

    private val monitoringChannelId = "MonitoringChannelId"
    private val proximityAlertChannelId = "ProximityAlertChannelId"

    private val notifiedMapItems = mutableSetOf<String>()

    private val job = SupervisorJob()

    override fun onCreate() {
        val handlerThread = HandlerThread("Location service thread", Process.THREAD_PRIORITY_BACKGROUND).apply { start() }

        serviceHandler = ServiceHandler(
            locationClient = LocationServices.getFusedLocationProviderClient(this),
            scope = CoroutineScope(Dispatchers.IO + job),
            looper = handlerThread.looper,
        )

        createMonitoringNotificationChannel()
        createProximityAlertNotificationChannel()

        startForeground("MonitoringNotification".hashCode(), createMonitoringNotification())
    }

    override fun onStartCommand(receivedIntent: Intent, flags: Int, startId: Int): Int {
        Log.i("LocationService", "$receivedIntent")

        when (receivedIntent.action) {
            null, START -> {
                serviceHandler.sendMessage(serviceHandler.obtainMessage())
            }
            STOP -> {
                val isStopped = stopSelfResult(startId)
            }
            else -> {
                throw Exception("Unsupported action")
            }
        }

        return START_STICKY
    }

    // Lazy je jer ako pokusam inline dobijem gresku da je this null
    // Mogao sam u init ali onda promenljiva treba da bude nullable, ovako izbegavam sve to

    private val pendingOpenAppIntent by lazy {
        val intent = Intent(this, MainActivity::class.java)
        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private val pendingStopServiceIntent by lazy {
        val intent = Intent(this, LocationService::class.java).apply { action = STOP }
        PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun createMonitoringNotification(): Notification {
        return NotificationCompat.Builder(this, monitoringChannelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Looking for interesting places nearby")
            .setOngoing(true)
            .addAction(R.drawable.tableoutlined, "Open app", pendingOpenAppIntent)
            .addAction(R.drawable.tableoutlined, "Stop", pendingStopServiceIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
    }

    private fun sendProximityNotification(mapItem: MapItem) {
        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
        intent.putExtras(Bundle().apply {
            putString("mapItemId", mapItem.id)
        })

        val showMapItemPendingIntent = PendingIntent.getActivity(this, mapItem.id.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationId = mapItem.id.hashCode()
        val notification = NotificationCompat.Builder(this, proximityAlertChannelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Map Item nearby")
            .setContentText(mapItem.title)
            .setContentIntent(showMapItemPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(this@LocationService, Manifest.permission.POST_NOTIFICATIONS ) == PackageManager.PERMISSION_GRANTED) {
                notify(notificationId, notification)
            }
        }
    }

    private inner class ServiceHandler(
        locationClient: FusedLocationProviderClient,
        scope: CoroutineScope,
        looper: Looper
    ) : Handler(looper) {
        private val timer = Timer()
        private val work = Work(locationClient, scope)

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
        private  val coroutineScope: CoroutineScope
    ): TimerTask() {

        private suspend fun actualWork(location: Location) {
            val mapItemsInRange = ServiceLocator.mapItemRepository.getAllMapItemsInRange(location, 1f)

            mapItemsInRange.forEach {
                if (!notifiedMapItems.contains(it.id)) {
                    notifiedMapItems.add(it.id)
                    sendProximityNotification(it)
                }
            }
        }

        override fun run() {
            if (ContextCompat.checkSelfPermission(
                    this@LocationService,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this@LocationService,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {
                coroutineScope.launch {
                    try {
                        actualWork(locationClient.lastLocation.await())
                    } catch (e: Exception) {
                        Log.e("LocationService", e.toString())
                    }
                }
            } else {
                Log.e("LocationService", "No location permission")
            }
        }
    }

    private fun createMonitoringNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(monitoringChannelId, getString(R.string.monitoring_channel_name), NotificationManager.IMPORTANCE_DEFAULT).apply { description = getString(R.string.monitoring_channel_description) }

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
                createNotificationChannel(channel)
            }
        }
    }

    private fun createProximityAlertNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(proximityAlertChannelId, getString(R.string.proximity_alert_channel_name), NotificationManager.IMPORTANCE_DEFAULT).apply { description = getString(R.string.proximity_alert_channel_description) }

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
                createNotificationChannel(channel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onBind(intent: Intent): IBinder? { return null }
}