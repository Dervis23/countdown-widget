# Geri Sayım

Minimalist, Material 3 Expressive arayüzlü, ana ekran widget'lı geri sayım uygulaması.

## Özellikler

- Gelecekte bir **tarih + saat** seç, kalan süreyi **gün / saat / dakika / saniye** olarak gör
- **Ana ekran widget'ı**: kalan gün, saat ve dakikayı gösterir, yaklaşık dakikada bir güncellenir
- Widget özelleştirme: **tema** (Sistem / Açık / Koyu), **arka plan opaklığı**, **köşe yuvarlaklığı**
- Hafif: veritabanı yok, internet yok, izin yok; ayarlar DataStore'da tutulur
- Material 3 Expressive tasarım + Android 12+ cihazlarda Material You dinamik renkler

## GitHub Actions ile APK derleme (bilgisayar gerekmez)

1. Bu klasörün içeriğini bir GitHub reposuna yükle:
   - github.com → **New repository** → isim ver (örn. `geri-sayim`) → **Public** → oluştur
   - Repo sayfasında **uploading an existing file** bağlantısına tıkla
   - Bu klasördeki **tüm dosya ve klasörleri** (`.github` klasörü dahil!) sürükle-bırak ile yükle → **Commit changes**
2. Repo sayfasında **Actions** sekmesine gir, gerekiyorsa workflow'ları etkinleştir
3. Soldan **Android APK Derle** workflow'unu seç → **Run workflow** (veya yeni bir push yap)
4. Derleme bitince (2-5 dk) workflow çalışmasına tıkla → en alttaki **Artifacts** bölümünden `geri-sayim-apk` dosyasını indir
5. ZIP'in içindeki **app-debug.apk** dosyasını telefonuna at, kur (ilk kurulumda "bilinmeyen kaynaklara izin ver" isteyebilir)

## Widget ekleme

1. Ana ekranda boş alana uzun bas → **Widget'lar** → **Geri Sayım**
2. Widget'ı eklerken özelleştirme ekranı açılır: tema, opaklık, köşe yuvarlaklığı
3. Bu ayarları sonradan uygulama içinden de değiştirebilirsin

## Android Studio ile geliştirme (opsiyonel)

Projeyi Android Studio'da açıp doğrudan telefonuna kurabilirsin. Gerekli: JDK 17, Android SDK 36.

## Teknik

- Kotlin + Jetpack Compose (Material 3 Expressive `1.4.0`)
- Widget: Jetpack Glance `1.2.0` + AlarmManager ile dakikalık güncelleme
- Ayarlar: DataStore Preferences
- minSdk 26 (Android 8.0+), target/compile SDK 36
