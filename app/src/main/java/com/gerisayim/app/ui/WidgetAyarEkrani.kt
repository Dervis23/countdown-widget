package com.gerisayim.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gerisayim.app.data.Ayarlar
import com.gerisayim.app.data.SettingsRepository
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Widget özelleştirme paneli.
 * Hem uygulama içinde hem widget ekleme ekranında (WidgetConfigActivity) kullanılır.
 * [onKaydet] null değilse "Kaydet" butonu gösterilir (widget ekleme akışı için).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetAyarEkrani(
    repo: SettingsRepository,
    onKaydet: (() -> Unit)? = null
) {
    val ayarlar by repo.ayarlarFlow.collectAsState(initial = Ayarlar())
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Widget Görünümü", style = MaterialTheme.typography.titleMedium)

        // Tema seçimi
        Text("Tema", style = MaterialTheme.typography.labelLarge)
        val temalar = listOf("sistem" to "Sistem", "acik" to "Açık", "koyu" to "Koyu")
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            temalar.forEachIndexed { i, (deger, etiket) ->
                SegmentedButton(
                    selected = ayarlar.tema == deger,
                    onClick = { scope.launch { repo.temaKaydet(deger) } },
                    shape = SegmentedButtonDefaults.itemShape(index = i, count = temalar.size)
                ) { Text(etiket) }
            }
        }

        // Şeffaflık
        Text(
            "Arka plan opaklığı: %${(ayarlar.seffaflik * 100).roundToInt()}",
            style = MaterialTheme.typography.labelLarge
        )
        Slider(
            value = ayarlar.seffaflik,
            onValueChange = { scope.launch { repo.seffaflikKaydet(it) } },
            valueRange = 0f..1f
        )

        // Köşe yuvarlaklığı
        Text("Köşe yuvarlaklığı", style = MaterialTheme.typography.labelLarge)
        val koseler = listOf(12 to "Az", 24 to "Orta", 36 to "Çok")
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            koseler.forEachIndexed { i, (deger, etiket) ->
                SegmentedButton(
                    selected = ayarlar.kose == deger,
                    onClick = { scope.launch { repo.koseKaydet(deger) } },
                    shape = SegmentedButtonDefaults.itemShape(index = i, count = koseler.size)
                ) { Text(etiket) }
            }
        }

        if (onKaydet != null) {
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = onKaydet) { Text("Widget'ı Ekle") }
            }
        }
    }
}
