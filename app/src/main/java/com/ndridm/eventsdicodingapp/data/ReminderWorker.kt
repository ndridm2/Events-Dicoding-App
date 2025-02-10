package com.ndridm.eventsdicodingapp.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.loopj.android.http.BuildConfig
import com.ndridm.eventsdicodingapp.R
import com.ndridm.eventsdicodingapp.data.remote.network.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class ReminderWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("DailyReminderWorker", "Worker dimulai...")

                showNotification("Ini adalah notifikasi test!")

                val response = ApiConfig.getApiService().getEventNotification(1, 1).awaitResponse()

                if (response.isSuccessful) {
                    val events = response.body()?.listEvents
                    if (!events.isNullOrEmpty()) {
                        val eventTitle = events.first().name ?: "Event Hari Ini"
                        if (BuildConfig.DEBUG) {
                            Log.d("DailyReminderWorker", "Event ditemukan: $eventTitle")
                        }
                        showNotification(eventTitle)
                    } else {
                        if (BuildConfig.DEBUG) {
                            Log.d("DailyReminderWorker", "Tidak ada event")
                        }
                    }
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.e("DailyReminderWorker", "Gagal mengambil event dari API")
                    }
                }

                return@withContext Result.success()

            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    Log.e("DailyReminderWorker", "Error: ${e.message}", e)
                }
                return@withContext Result.retry()
            }
        }
    }

    private fun showNotification(eventTitle: String) {
        if (BuildConfig.DEBUG) {
            Log.d("DailyReminderWorker", "Menampilkan notifikasi: $eventTitle")
        }

        val channelId = "daily_reminder_channel"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Buat Notification Channel hanya untuk Android 8+ (Oreo ke atas)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Reminder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel untuk notifikasi pengingat event harian"
            }

            notificationManager.createNotificationChannel(channel)
        }

        // Cek izin notifikasi hanya untuk Android 13+ (API 33 - Tiramisu ke atas)
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    "android.permission.POST_NOTIFICATIONS"
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (BuildConfig.DEBUG) {
                    Log.d("DailyReminderWorker", "Izin notifikasi tidak diberikan!")
                }
                return
            }
        }

        // Buat notifikasi
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.baseline_notifications)
            .setContentTitle("Jangan Lewatkan Event Hari Ini!")
            .setContentText(eventTitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(1, notification)
    }
}