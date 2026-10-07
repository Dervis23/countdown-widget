package com.gerisayim.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gerisayim.app.data.Ayarlar
import com.gerisayim.app.data.SettingsRepository
import com.gerisayim.app.ui.WidgetAyarEkrani
import com.gerisayim.app.ui.theme.GeriSayimTheme
import com.gerisayim.app.widget.CountdownWidget
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeriSayimTheme {
                AnaEkran()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnaEkran() {
    val context = LocalContext.current
    val repo = remember { SettingsRepository(context) }
    val ayarlar by repo.ayarlarFlow.collectAsState(initial = Ayarlar())
    val scope = rememberCoroutineScope()

    // Her saniye güncellenen "şimdi"
    var simdi by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            simdi = System.currentTimeMillis()
            delay(1000)
        }
    }

    var tarihSeciciAcik by remember { mutableStateOf(false) }
    var saatSeciciAcik by remember { mutableStateOf(false) }
    var secilenTarih by remember { mutableStateOf<LocalDate?>(null) }

    val hedef = ayarlar.hedefMillis
    val kalan = hedef - simdi
    val hedefMetin = remember(hedef) {
        if (hedef <= 0) "Hedef tarih seçilmedi"
        else Instant.ofEpochMilli(hedef).atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm"))
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))
            Text("Geri Sayım", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                hedefMetin,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))

            if (hedef <= 0L) {
                Text(
                    "Başlamak için bir tarih ve saat seç.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            } else if (kalan <= 0L) {
                Text("Süre doldu!", style = MaterialTheme.typography.displaySmall)
            } else {
                val gun = kalan / 86_400_000
                val saat = (kalan / 3_600_000) % 24
                val dakika = (kalan / 60_000) % 60
                val saniye = (kalan / 1_000) % 60

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SayacKarti(deger = gun.toString(), etiket = "gün", modifier = Modifier.weight(1f))
                    SayacKarti(deger = "%02d".format(saat), etiket = "saat", modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SayacKarti(deger = "%02d".format(dakika), etiket = "dakika", modifier = Modifier.weight(1f))
                    SayacKarti(deger = "%02d".format(saniye), etiket = "saniye", modifier = Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(32.dp))
            Button(onClick = { tarihSeciciAcik = true }) {
                Text(if (hedef <= 0L) "Tarih ve Saat Seç" else "Tarihi Değiştir")
            }
            Spacer(Modifier.height(24.dp))
            HorizontalDivider()

            // Widget özelleştirme
            WidgetAyarEkrani(repo)
        }
    }

    if (tarihSeciciAcik) {
        val tarihState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { tarihSeciciAcik = false },
            confirmButton = {
                TextButton(onClick = {
                    tarihState.selectedDateMillis?.let { millis ->
                        // DatePicker UTC gece yarısı döner; yerel güne çevir
                        secilenTarih = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC).toLocalDate()
                        tarihSeciciAcik = false
                        saatSeciciAcik = true
                    }
                }) { Text("İleri") }
            },
            dismissButton = {
                TextButton(onClick = { tarihSeciciAcik = false }) { Text("Vazgeç") }
            }
        ) {
            DatePicker(state = tarihState)
        }
    }

    if (saatSeciciAcik) {
        val saatState = rememberTimePickerState()
        TimePickerDialog(
            onDismissRequest = { saatSeciciAcik = false },
            confirmButton = {
                TextButton(onClick = {
                    val tarih = secilenTarih
                    if (tarih != null) {
                        val millis = tarih
                            .atTime(saatState.hour, saatState.minute)
                            .atZone(ZoneId.systemDefault())
                            .toInstant().toEpochMilli()
                        scope.launch {
                            repo.hedefKaydet(millis)
                            CountdownWidget().updateAll(context)
                        }
                    }
                    saatSeciciAcik = false
                }) { Text("Tamam") }
            },
            dismissButton = {
                TextButton(onClick = { saatSeciciAcik = false }) { Text("Vazgeç") }
            }
        ) {
            TimePicker(state = saatState)
        }
    }
}

@Composable
fun SayacKarti(deger: String, etiket: String, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(deger, style = MaterialTheme.typography.displaySmall)
            Text(
                etiket,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
