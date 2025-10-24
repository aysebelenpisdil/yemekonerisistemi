# 🐛 Android App Debug Checklist

## Ne Oldu?
Backend çalışıyor ama Android app'te fuzzy search çalışmıyor.

## 📋 Kontrol Edilecekler

### 1. Logcat'te Ne Var?
Android Studio → Logcat → Filter: `InventoryFragment`

**Aranacak mesajlar:**
```
❌ HATA VAR:
E/InventoryFragment: 💥 Search error: ...
E/InventoryFragment: ❌ API error: ...

✅ ÇALIŞIYOR:
D/InventoryFragment: 🔍 Fuzzy search: 'dom'
D/InventoryFragment: 📡 Response: 200
D/InventoryFragment: ✅ 5 sonuç: [...]
```

### 2. Ne Tür Hata?

#### A. Connection Refused
```
java.net.ConnectException: Failed to connect to /10.0.2.2:8000
```
**Çözüm**: Backend çalışmıyor veya emulator backend'e erişemiyor

#### B. Timeout
```
java.net.SocketTimeoutException: timeout
```
**Çözüm**: Backend yavaş veya network problemi

#### C. 404 Not Found
```
HTTP 404
```
**Çözüm**: API endpoint yanlış

#### D. Hiç log yok
**Çözüm**: SearchEditText çalışmıyor, listener kurulmamış

---

## 🔍 Adım Adım Debug

### Adım 1: Backend Test
```bash
curl http://localhost:8000/health
curl 'http://localhost:8000/api/ingredients/?q=test&limit=5'
```

### Adım 2: Emulator IP Test
Emulator çalışırken:
```bash
# adb'yi bul
export PATH=$PATH:/Users/belenikov/Library/Android/sdk/platform-tools

# Emulator'den backend'e curl
adb shell "curl http://10.0.2.2:8000/health"
```

### Adım 3: RetrofitClient Log Level
RetrofitClient.kt'de log level BODY olmalı:
```kotlin
level = HttpLoggingInterceptor.Level.BODY
```

### Adım 4: Manifest İzinleri
AndroidManifest.xml'de internet izni var mı?
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

## 🛠️ Olası Sorunlar ve Çözümler

### Sorun 1: Backend'e Erişemiyor
**Belirti**: Connection refused hatası

**Çözüm 1**: Backend port kontrolü
```bash
lsof -i :8000
```

**Çözüm 2**: Emulator network sıfırla
- Emulator → Settings → ... → Wipe Data

**Çözüm 3**: BASE_URL'i değiştir
```kotlin
// RetrofitClient.kt
private const val BASE_URL = "http://10.0.2.2:8000"  // Emulator için
// private const val BASE_URL = "http://localhost:8000"  // YANLIŞ!
```

### Sorun 2: Fuzzy Search İsteği Gönderilmiyor
**Belirti**: Logcat'te hiç log yok

**Kontrol 1**: SearchEditText listener kurulmuş mu?
```kotlin
// InventoryFragment.kt - setupRealTimeSearch() çağrılıyor mu?
android.util.Log.d("InventoryFragment", "🚀 Real-time fuzzy search aktif!")
```

**Kontrol 2**: Query minimum 2 karakter mi?
```kotlin
if (query.length >= 2) {  // Bu satırı kontrol et
```

### Sorun 3: API Response Gelmiyor
**Belirti**: Request gidiyor ama response yok

**Kontrol**: API endpoint doğru mu?
```kotlin
// ApiService.kt
@GET("/api/ingredients/")  // "/" ile başlamalı!
```

### Sorun 4: JSON Parse Hatası
**Belirti**: Response geliyor ama parse edilemiyor

**Kontrol**: IngredientDTO model doğru mu?
```kotlin
data class IngredientDTO(
    val name: String,  // Backend'dekiyle aynı olmalı
    ...
)
```

---

## 📱 Manuel Test Adımları

### Test 1: TextView'a Direkt Yaz
InventoryFragment.kt'ye ekle:
```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // TEST: Backend'e istek at
    lifecycleScope.launch {
        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.searchIngredients("test", 5)
            }

            android.util.Log.d("TEST", "Response code: ${response.code()}")
            android.util.Log.d("TEST", "Body: ${response.body()}")

            Toast.makeText(
                context,
                "Backend yanıt verdi! ${response.body()?.total} sonuç",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            android.util.Log.e("TEST", "Hata: ${e.message}", e)
            Toast.makeText(
                context,
                "HATA: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
```

### Test 2: SearchEditText Listener
```kotlin
searchEditText.addTextChangedListener { editable ->
    android.util.Log.d("TEST", "Text changed: ${editable.toString()}")
}
```

---

## 🎯 Beklenen Davranış

1. Kullanıcı "dom" yazar
2. Logcat: `D/InventoryFragment: 🔍 Fuzzy search: 'dom'`
3. 300ms bekler (debouncing)
4. Logcat: `D/InventoryFragment: 📡 Response: 200`
5. Logcat: `D/InventoryFragment: ✅ 5 sonuç: [Domates, ...]`
6. UI'da suggestions card görünür
7. RecyclerView'da 5 öneri listelenir

---

## 💡 Hızlı Çözümler

### Çözüm 1: Clean ve Rebuild
```
Build → Clean Project
Build → Rebuild Project
```

### Çözüm 2: Invalidate Caches
```
File → Invalidate Caches / Restart → Invalidate and Restart
```

### Çözüm 3: Emulator Yeniden Başlat
```
Emulator → ... → Cold Boot Now
```

### Çözüm 4: Gradle Sync
```
File → Sync Project with Gradle Files
```

---

## 📊 Logcat Filtre Önerileri

```
# Tüm InventoryFragment logları
InventoryFragment

# Tüm network logları
OkHttp|Retrofit

# Hata logları
level:ERROR

# Kombine filtre
InventoryFragment|OkHttp|Retrofit
```

---

**Lütfen şunları paylaş:**
1. ✅ Logcat'teki hata mesajları (kırmızı loglar)
2. ✅ "dom" yazdığında ne oluyor?
3. ✅ Toast mesajı görünüyor mu?
4. ✅ Suggestions card görünüyor mu?

Bu bilgilerle tam olarak sorunu buluruz! 🔍
