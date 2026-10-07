package com.gerisayim.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "ayarlar")

/** Uygulama + widget ayarları */
data class Ayarlar(
    val hedefMillis: Long = 0L,
    val tema: String = "sistem",   // sistem | acik | koyu
    val seffaflik: Float = 1f,     // 0f = tamamen şeffaf, 1f = opak
    val kose: Int = 24             // widget köşe yuvarlaklığı (dp)
)

class SettingsRepository(private val context: Context) {

    companion object {
        private val HEDEF = longPreferencesKey("hedef_millis")
        private val TEMA = stringPreferencesKey("widget_tema")
        private val SEFFAFLIK = floatPreferencesKey("widget_seffaflik")
        private val KOSE = intPreferencesKey("widget_kose")
    }

    val ayarlarFlow: Flow<Ayarlar> = context.dataStore.data.map { p ->
        Ayarlar(
            hedefMillis = p[HEDEF] ?: 0L,
            tema = p[TEMA] ?: "sistem",
            seffaflik = p[SEFFAFLIK] ?: 1f,
            kose = p[KOSE] ?: 24
        )
    }

    suspend fun oku(): Ayarlar = ayarlarFlow.first()

    suspend fun hedefKaydet(millis: Long) {
        context.dataStore.edit { it[HEDEF] = millis }
    }

    suspend fun temaKaydet(tema: String) {
        context.dataStore.edit { it[TEMA] = tema }
    }

    suspend fun seffaflikKaydet(deger: Float) {
        context.dataStore.edit { it[SEFFAFLIK] = deger }
    }

    suspend fun koseKaydet(deger: Int) {
        context.dataStore.edit { it[KOSE] = deger }
    }
}
