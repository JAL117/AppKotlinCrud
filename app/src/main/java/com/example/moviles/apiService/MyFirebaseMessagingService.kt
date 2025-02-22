package com.example.moviles.apiService

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.moviles.MainActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val _receivedMessage = MutableSharedFlow<Pair<String?, String?>>()
    val receivedMessage: SharedFlow<Pair<String?, String?>> = _receivedMessage.asSharedFlow()

    override fun onNewToken(token: String) {
        Log.d("FCM Token", "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM Message", "From: ${remoteMessage.from}")

        remoteMessage.data.isNotEmpty().let {
            Log.d("FCM Message", "Message data payload: " + remoteMessage.data)
            handleDataMessage(remoteMessage.data)
        }

        remoteMessage.notification?.let { notification ->
            Log.d("FCM Message", "Message Notification Body: ${notification.body}")
            showNotification(notification.title, notification.body)
            runBlocking {
                _receivedMessage.emit(Pair(notification.title, notification.body))
            }
        }
    }

    private fun showNotification(title: String?, body: String?) {
        val channelId = "my_channel_id"
        val notificationId = 1

        // Crear el Intent para abrir MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Construir la notificación
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Cambiar por un icono adecuado
            .setContentTitle(title ?: "Nueva Notificación")
            .setContentText(body ?: "Tienes un nuevo mensaje")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Crear el canal de notificación (para Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "My Channel"
            val channelDescription = "Canal de notificaciones de mi aplicación"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Mostrar la notificación
        with(NotificationManagerCompat.from(this)) {
            // Verificar si se tienen los permisos (Android 13+)
            if (ActivityCompat.checkSelfPermission(
                    this@MyFirebaseMessagingService,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("FCM", "No se tienen los permisos para mostrar la notificación")
                // No mostrar la notificación si no se tienen los permisos
                return
            }
            notify(notificationId, builder.build())
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val title = data["title"]
        val message = data["message"]
        runBlocking {
            _receivedMessage.emit(Pair(title, message))
        }
        Log.d("FCM Data Message", "Title: $title, Message: $message")
    }

    private fun sendRegistrationToServer(token: String?) {
        Log.d("FCM Token", "Sending token to server: $token")
        // TODO: Implementar la lógica para enviar el token a tu servidor NodeJS usando Retrofit.
    }

    fun getToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                Log.d("FCM Token (Manual)", "Manual FCM Token: $token")
                sendRegistrationToServer(token)
            }
    }
}