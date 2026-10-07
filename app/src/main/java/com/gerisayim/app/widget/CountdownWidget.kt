package com.gerisayim.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.gerisayim.app.MainActivity
import com.gerisayim.app.data.Ayarlar
import com.gerisayim.app.data.SettingsRepository

class CountdownWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val ayarlar = SettingsRepository(context).oku()
        provideContent {
            WidgetIcerik(ayarlar)
        }
    }
}

@Composable
private fun WidgetIcerik(a: Ayarlar) {
    // Tema ayarına göre renkler ("sistem" için gündüz/gece otomatik)
    val acikArkaPlan = Color.White.copy(alpha = a.seffaflik)
    val koyuArkaPlan = Color(0xFF14121A).copy(alpha = a.seffaflik)
    val acikYazi = Color(0xFF1D1B20)
    val koyuYazi = Color(0xFFE6E0E9)

    val arkaPlan = when (a.tema) {
        "acik" -> ColorProvider(acikArkaPlan)
        "koyu" -> ColorProvider(koyuArkaPlan)
        else -> ColorProvider(day = acikArkaPlan, night = koyuArkaPlan)
    }
    val yazi = when (a.tema) {
        "acik" -> ColorProvider(acikYazi)
        "koyu" -> ColorProvider(koyuYazi)
        else -> ColorProvider(day = acikYazi, night = koyuYazi)
    }

    val kalan = a.hedefMillis - System.currentTimeMillis()

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(arkaPlan)
            .cornerRadius(a.kose.dp)
            .clickable(actionStartActivity<MainActivity>()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                a.hedefMillis <= 0L -> Text(
                    text = "Hedef seçilmedi\nAyarlamak için dokun",
                    style = TextStyle(
                        color = yazi,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                )

                kalan <= 0L -> Text(
                    text = "Süre doldu!",
                    style = TextStyle(
                        color = yazi,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )

                else -> {
                    val gun = kalan / 86_400_000
                    val saat = (kalan / 3_600_000) % 24
                    val dakika = (kalan / 60_000) % 60

                    Text(
                        text = "$gun",
                        style = TextStyle(
                            color = yazi,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = "gün",
                        style = TextStyle(color = yazi, fontSize = 13.sp)
                    )
                    Text(
                        text = "%02d saat %02d dakika".format(saat, dakika),
                        style = TextStyle(color = yazi, fontSize = 13.sp)
                    )
                }
            }
        }
    }
}
