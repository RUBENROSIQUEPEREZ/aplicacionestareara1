package com.example.myapplication1.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myapplication1.MainActivity
import com.example.myapplication1.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Se llama cuando llega un mensaje y la app está en primer plano
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Si el mensaje tiene contenido de notificación, lo mostramos
        remoteMessage.notification?.let {
            showNotification(it.title, it.body)
        }
    }

    // Se llama si el token de registro cambia (por seguridad o reinstalación)
    override fun onNewToken(token: String) {
        Log.d("FCM", "Nuevo token: $token")
        // Aquí podrías enviar el token a tu servidor si tuvieras uno propio
    }

    private fun showNotification(title: String?, body: String?) {
        val channelId = "canal_notificaciones_app"
        val channelName = "Notificaciones Generales"

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // PendingIntent para abrir la app al tocar la notificación
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Asegúrate de tener un icono válido
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Para Android Oreo (8.0) y superior, necesitamos crear un canal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        manager.notify(0, builder.build())
    }
}