# 🔌 Android App - Backend Bağlantı Testi

## ✅ Backend Durumu
Backend **ÇALIŞIYOR** (http://localhost:8000)

---

## 📱 Android App Konfigürasyonu

### Mevcut Ayarlar
```kotlin
// RetrofitClient.kt
private const val BASE_URL = "http://10.0.2.2:8000"
```

Bu **DOĞRU** ayar! Android Emulator için `10.0.2.2` kullanılır.

---

## 🧪 Bağlantı Testi

### 1. Backend Test (Terminal)
```bash
curl http://localhost:8000/health
# Yanıt: {"status":"healthy"}

curl 'http://localhost:8000/api/ingredients/?q=domates&limit=3'
# Yanıt: JSON array with ingredients
```

### 2. Android Emulator Test

#### Emulator içinden backend'e erişim:
```bash
# Android Studio Terminal veya adb shell
adb shell "curl http://10.0.2.2:8000/health"
```

**Beklenen yanıt:**
```json
{"status":"healthy"}
```

---

## 🚀 Android App'i Test Etme

### Adım 1: Backend'in Çalıştığından Emin Ol
```bash
curl http://localhost:8000/health
```

### Adım 2: Android Studio'da Logcat'i Aç
1. Android Studio → Logcat
2. Filter: `InventoryFragment` veya `Retrofit`

### Adım 3: App'i Çalıştır
1. Run → Run 'app'
2. Emulator'ü bekle
3. "Envanterim" (Inventory) sekmesine git

### Adım 4: Arama Yap
Arama kutusuna yaz:
- `dom` → Domates önerileri görünmeli
- `yum` → Yumurta önerileri görünmeli
- `cil` → Çilek önerileri görünmeli

### Adım 5: Logcat'te Kontrol Et

**Başarılı durumda göreceğin loglar:**
```
D/InventoryFragment: 🔍 Fuzzy search: 'dom'
D/InventoryFragment: 📡 Response: 200
D/InventoryFragment: ✅ 5 sonuç: [Domates, Domates Salçası, ...]
D/InventoryFragment: 📋 Suggestions shown: 5 items
```

**Hata durumunda göreceğin loglar:**
```
E/InventoryFragment: ❌ API error: 404
E/InventoryFragment: 💥 Search error: Failed to connect to /10.0.2.2:8000
```

---

## 🛠️ Sorun Giderme

### Backend'e bağlanamıyor?

#### 1. Backend Çalışıyor mu?
```bash
curl http://localhost:8000/health
```

Eğer çalışmıyorsa:
```bash
cd /Users/belenikov/yemekonerisistemi/backend
python3 main.py
```

#### 2. Port Çakışması var mı?
```bash
# 8000 portunu kim kullanıyor kontrol et
lsof -i :8000

# Eğer başka bir process varsa öldür
lsof -ti:8000 | xargs kill -9
```

#### 3. Emulator Network Testi
```bash
# Emulator içinden localhost'a erişim
adb shell "ping -c 3 10.0.2.2"

# Emulator içinden backend'e curl
adb shell "curl http://10.0.2.2:8000/health"
```

#### 4. Fiziksel Cihaz Kullanıyorsan

RetrofitClient.kt'yi değiştir:
```kotlin
// Yerel IP adresini bul
// Terminal: ifconfig | grep "inet "
// Örnek: 192.168.1.100

private const val BASE_URL = "http://192.168.1.100:8000"  // Kendi IP'ni yaz
```

---

## 📊 Beklenen Davranış

### ✅ Başarılı Fuzzy Search
1. Kullanıcı "dom" yazar
2. 300ms debouncing (kullanıcı yazmayı bitirmesini bekle)
3. Backend'e istek: `GET /api/ingredients/?q=dom&limit=20`
4. Backend yanıt: 5 sonuç (Domates, Domates Salçası, ...)
5. RecyclerView'da öneriler görüntülenir
6. Kullanıcı bir öneriye tıklar
7. Malzeme envantere eklenir

### ❌ Hata Durumları

**1. Connection Refused**
```
java.net.ConnectException: Failed to connect to /10.0.2.2:8000
```
**Çözüm**: Backend'i başlat (`python3 main.py`)

**2. Timeout**
```
java.net.SocketTimeoutException: timeout
```
**Çözüm**: Backend yavaş yanıt veriyor veya çalışmıyor

**3. 404 Not Found**
```
HTTP 404 Not Found
```
**Çözüm**: API endpoint yanlış, backend routes'ları kontrol et

---

## 🎯 Hızlı Test Script

Terminal'de çalıştır:
```bash
#!/bin/bash
echo "🔍 Backend Test..."

# 1. Backend health check
echo "1️⃣ Health check..."
curl -s http://localhost:8000/health | jq || echo "❌ Backend çalışmıyor!"

# 2. Fuzzy search test
echo -e "\n2️⃣ Fuzzy search test..."
curl -s 'http://localhost:8000/api/ingredients/?q=domates&limit=3' | jq '.results[].name' || echo "❌ Search çalışmıyor!"

# 3. Emulator network test (eğer emulator çalışıyorsa)
echo -e "\n3️⃣ Emulator network test..."
adb shell "curl -s http://10.0.2.2:8000/health" 2>/dev/null || echo "⚠️  Emulator çalışmıyor veya bağlantı yok"

echo -e "\n✅ Testler tamamlandı!"
```

---

## 📱 Android Studio'da Debug

### 1. Breakpoint Koy
```kotlin
// InventoryFragment.kt, setupRealTimeSearch() içinde

if (response.isSuccessful && response.body() != null) {
    val results = response.body()!!.results  // ← Breakpoint buraya
    ...
}
```

### 2. Debug Modunda Çalıştır
- Run → Debug 'app'
- Arama kutusuna yaz
- Breakpoint'te dur
- `results` değişkenini incele

### 3. Network Inspector Kullan
- Android Studio → View → Tool Windows → App Inspection
- Network Inspector sekmesi
- Backend isteklerini görüntüle

---

## ✅ Başarı Kriterleri

Fuzzy search çalışıyorsa:
- ✅ Backend 8000 portunda çalışıyor
- ✅ Emulator 10.0.2.2:8000'e bağlanabiliyor
- ✅ Arama kutusuna yazınca öneriler görünüyor
- ✅ Logcat'te "✅ X sonuç" mesajı var
- ✅ Önerilere tıklayınca malzeme ekleniyor

---

## 💡 Pro Tips

1. **Backend her zaman açık olmalı**: Android app backend olmadan çalışmaz
2. **Logcat her şeyi gösterir**: InventoryFragment loglarını takip et
3. **Network Inspector kullan**: HTTP isteklerini görselleştir
4. **adb shell curl**: Emulator'den backend'e erişimi test et
5. **Port çakışması**: 8000 portunu başka bir uygulama kullanıyorsa değiştir

---

**Hala sorun mu var?** Logcat çıktısını paylaş, yardımcı olayım! 🚀
