package com.fernandez.catalogo.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.fernandez.catalogo.MainActivity

class NotificadorAndroid(context: Context) : Notificador {
    private val appContext = context.applicationContext
    private val manager = NotificationManagerCompat.from(appContext)
    private var ultimoEnvio = 0L

    @Synchronized
    override fun mostrar(titulo: String, texto: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return
        if (!manager.areNotificationsEnabled()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val sistema = appContext.getSystemService(NotificationManager::class.java)
            if (sistema.getNotificationChannel(CANAL_ID) == null) {
                sistema.createNotificationChannel(
                    NotificationChannel(CANAL_ID, "Notificaciones del catálogo", NotificationManager.IMPORTANCE_DEFAULT)
                )
            }
            if (sistema.getNotificationChannel(CANAL_ID)?.importance == NotificationManager.IMPORTANCE_NONE) return
        }

        val ahora = SystemClock.elapsedRealtime()
        if (ultimoEnvio > 0 && ahora - ultimoEnvio < 1_000) return

        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val abrirCatalogo = PendingIntent.getActivity(
            appContext,
            NOTIFICACION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificacion = NotificationCompat.Builder(appContext, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(abrirCatalogo)
            .build()

        try {
            // Un ID estable actualiza el mismo aviso, en lugar de acumular duplicados.
            manager.notify(NOTIFICACION_ID, notificacion)
            ultimoEnvio = ahora
        } catch (_: SecurityException) {
            // El permiso puede revocarse entre la comprobación y el envío.
        }
    }

    private companion object {
        const val CANAL_ID = "catalogo_notificaciones"
        const val NOTIFICACION_ID = 15
    }
}
