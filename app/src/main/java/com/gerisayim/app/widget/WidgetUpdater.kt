package com.gerisayim.app.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Widget'ı yaklaşık dakikada bir güncellemek için hafif bir AlarmManager zinciri.
 * setAndAllowWhileIdle kullanılır: kesin (exact) alarm izni gerekmez, pil dostudur.
 */
object WidgetUpdater {
    private const val ISTEK_KODU = 4207

    fun planla(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.setAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 60_000,
            pendingIntent(context)
        )
    }

    fun iptal(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pendingIntent(context))
    }

    private fun pendingIntent(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            ISTEK_KODU,
            Intent(context, WidgetTickReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
}

class WidgetTickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bekleyen = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                CountdownWidget().updateAll(context)
            } finally {
                bekleyen.finish()
            }
        }
        WidgetUpdater.planla(context) // zinciri devam ettir
    }
}

class CountdownWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = CountdownWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        WidgetUpdater.planla(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        WidgetUpdater.planla(context)
    }

    override fun onDisabled(context: Context) {
        WidgetUpdater.iptal(context)
        super.onDisabled(context)
    }
}
