# 🐛 Android App Nasıl Debug Edilir?

## Adım Adım Kılavuz

### 1. RetrofitClient'e Public BASE_URL Ekle

`RetrofitClient.kt` dosyasını aç ve değiştir:

```kotlin
object RetrofitClient {
    // PRIVATE yerine PUBLIC yap!
    const val BASE_URL = "http://10.0.2.2:8000"  // private kaldır

    // ... geri kalanı aynı
}
```

### 2. InventoryFragment'e Manuel Test Ekle

`InventoryFragment.kt` → `onViewCreated()` metoduna EN BAŞA ekle:

```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // ========== MANUEL TEST BAŞLANGIÇ ==========
    android.util.Log.d("MANUEL_TEST", "========================================")
    android.util.Log.d("MANUEL_TEST", "Fragment başladı, backend testi yapılıyor...")
    android.util.Log.d("MANUEL_TEST", "========================================")

    lifecycleScope.launch {
        delay(2000) // 2 saniye bekle (UI yüklensin)

        try {
            android.util.Log.d("MANUEL_TEST", "Backend'e test isteği gönderiliyor...")
            android.util.Log.d("MANUEL_TEST", "URL: ${RetrofitClient.BASE_URL}/api/ingredients/?q=test&limit=5")

            val response = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.searchIngredients("test", 5)
            }

            android.util.Log.d("MANUEL_TEST", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            android.util.Log.d("MANUEL_TEST", "✅ BACKEND YANIT VERDİ!")
            android.util.Log.d("MANUEL_TEST", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            android.util.Log.d("MANUEL_TEST", "HTTP Code: ${response.code()}")
            android.util.Log.d("MANUEL_TEST", "Success: ${response.isSuccessful}")
            android.util.Log.d("MANUEL_TEST", "Body null?: ${response.body() == null}")

            if (response.isSuccessful && response.body() != null) {
                val total = response.body()!!.total
                val names = response.body()!!.results.map { it.name }

                android.util.Log.d("MANUEL_TEST", "Toplam sonuç: $total")
                android.util.Log.d("MANUEL_TEST", "İlk 5: $names")

                Toast.makeText(
                    context,
                    "✅ Backend çalışıyor! $total sonuç bulundu",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                android.util.Log.e("MANUEL_TEST", "❌ Yanıt başarısız: ${response.code()}")
                Toast.makeText(
                    context,
                    "❌ Backend yanıt verdi ama hatalı: ${response.code()}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            android.util.Log.e("MANUEL_TEST", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            android.util.Log.e("MANUEL_TEST", "💥 BACKEND'E ULAŞILAMIYOR!")
            android.util.Log.e("MANUEL_TEST", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            android.util.Log.e("MANUEL_TEST", "Hata mesajı: ${e.message}")
            android.util.Log.e("MANUEL_TEST", "Hata tipi: ${e.javaClass.simpleName}")
            e.printStackTrace()

            Toast.makeText(
                context,
                "💥 HATA: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    // ========== MANUEL TEST BİTİŞ ==========

    // View'ları bağla (eski kod devam ediyor)
    searchEditText = view.findViewById(R.id.searchEditText)
    // ... geri kalanı aynı
}
```

### 3. Build ve Run

```
1. Build → Clean Project
2. Build → Rebuild Project
3. Run → Run 'app'
```

### 4. Logcat'i İzle

Android Studio → Logcat → Filter kısmına yaz:
```
MANUEL_TEST
```

### 5. Sonuçları Analiz Et

#### ✅ BAŞARILI DURUM:
```
D/MANUEL_TEST: ✅ BACKEND YANIT VERDİ!
D/MANUEL_TEST: HTTP Code: 200
D/MANUEL_TEST: Success: true
D/MANUEL_TEST: Toplam sonuç: 5
```

**Ekranda Toast:** "✅ Backend çalışıyor! 5 sonuç bulundu"

➡️ **Backend çalışıyor, fuzzy search kodu sorunlu**

#### ❌ CONNECTION REFUSED:
```
E/MANUEL_TEST: 💥 BACKEND'E ULAŞILAMIYOR!
E/MANUEL_TEST: Hata mesajı: Failed to connect to /10.0.2.2:8000
E/MANUEL_TEST: Hata tipi: ConnectException
```

**Ekranda Toast:** "💥 HATA: Failed to connect..."

➡️ **Backend çalışmıyor veya emulator erişemiyor**

**Çözümler:**
1. Backend'in çalıştığından emin ol: `curl http://localhost:8000/health`
2. Emulator'ü yeniden başlat: Cold Boot
3. BASE_URL'i kontrol et: `http://10.0.2.2:8000`

#### ❌ TIMEOUT:
```
E/MANUEL_TEST: Hata tipi: SocketTimeoutException
```

➡️ **Network yavaş veya backend dondu**

**Çözümler:**
1. Backend loglarını kontrol et
2. Timeout süresini artır (RetrofitClient.kt)
3. Emulator'ü yeniden başlat

#### ❌ 404 NOT FOUND:
```
D/MANUEL_TEST: HTTP Code: 404
```

➡️ **API endpoint yanlış**

**Çözümler:**
1. Backend URL'i kontrol et: http://localhost:8000/docs
2. ApiService.kt'de endpoint'i kontrol et: `@GET("/api/ingredients/")`

---

## 📊 Logcat Filtreleri

### Sadece test logları:
```
MANUEL_TEST
```

### Tüm debug logları:
```
DEBUG|MANUEL_TEST
```

### Network logları:
```
OkHttp|Retrofit
```

### Hata logları:
```
level:ERROR
```

### Kombine:
```
MANUEL_TEST|DEBUG|OkHttp|Retrofit|InventoryFragment
```

---

## 🎯 Hangi Senaryodasın?

### Senaryo 1: Toast "✅ Backend çalışıyor!" göründü
**Durum**: Backend erişilebilir ✅
**Sorun**: Fuzzy search kodu çalışmıyor

**Sonraki adım**: TEST_FRAGMENT.kt'deki kodu kullan (detaylı debug)

### Senaryo 2: Toast "💥 HATA: Failed to connect" göründü
**Durum**: Backend'e erişilemiyor ❌
**Sorun**: Network bağlantısı yok

**Sonraki adım**:
```bash
# Terminal'de:
curl http://localhost:8000/health

# Eğer çalışıyorsa, emulator problemi
# Eğer çalışmıyorsa, backend'i başlat:
cd backend && python3 main.py
```

### Senaryo 3: Hiçbir Toast görünmedi
**Durum**: Fragment yüklenmedi ❌
**Sorun**: App crash oldu veya fragment açılmadı

**Sonraki adım**: Logcat'te "ActivityThread" veya "AndroidRuntime" filtrele

---

## 💡 Hızlı Kontroller

### Backend Kontrolü:
```bash
curl http://localhost:8000/health
# Yanıt: {"status":"healthy"}
```

### Emulator Network Kontrolü:
```bash
adb shell "ping -c 3 10.0.2.2"
adb shell "curl http://10.0.2.2:8000/health"
```

### Gradle Dependencies:
```gradle
// app/build.gradle
dependencies {
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.9.0'
}
```

---

**BU ADIMDki SONUÇLARI PAYLAŞ:**
1. Logcat'teki MANUEL_TEST logları
2. Toast mesajı ne diyor?
3. Backend terminal'de ne görünüyor?

Sonuçlarına göre tam çözümü vereceğim! 🔍
