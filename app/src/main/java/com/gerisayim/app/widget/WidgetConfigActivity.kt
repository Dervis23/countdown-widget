package com.gerisayim.app.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.lifecycleScope
import com.gerisayim.app.data.SettingsRepository
import com.gerisayim.app.ui.WidgetAyarEkrani
import com.gerisayim.app.ui.theme.GeriSayimTheme
import kotlinx.coroutines.launch

/**
 * Widget ana ekrana eklenirken açılan özelleştirme ekranı.
 * Tema, şeffaflık ve köşe yuvarlaklığı burada seçilir.
 */
class WidgetConfigActivity : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Kullanıcı geri çıkarsa widget eklenmesin
        setResult(RESULT_CANCELED)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val repo = SettingsRepository(this)

        setContent {
            GeriSayimTheme {
                ConfigEkrani(repo = repo, onKaydet = { widgetiKur() })
            }
        }
    }

    private fun widgetiKur() {
        lifecycleScope.launch {
            val glanceId = GlanceAppWidgetManager(this@WidgetConfigActivity)
                .getGlanceIdBy(appWidgetId)
            CountdownWidget().update(this@WidgetConfigActivity, glanceId)
            WidgetUpdater.planla(this@WidgetConfigActivity)

            setResult(
                RESULT_OK,
                Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            )
            finish()
        }
    }
}

@Composable
private fun ConfigEkrani(repo: SettingsRepository, onKaydet: () -> Unit) {
    Scaffold { padding ->
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Widget'ı Özelleştir",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 24.dp, top = 24.dp)
            )
            Text(
                "Bu ayarları daha sonra uygulama içinden de değiştirebilirsin.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 24.dp, top = 4.dp)
            )
            WidgetAyarEkrani(repo = repo, onKaydet = onKaydet)
        }
    }
}
