# 🔧 Emulator Backend Bağlantı Sorunu - Çözüm

## Sorun
Android emulator **10.0.2.2:8000**'e bağlanamıyor.

## Çözüm Seçenekleri

### ÇÖZÜM 1: Bilgisayarın Yerel IP'sini Kullan (En Kolay)

#### Adım 1: Yerel IP'ni Öğren
Terminal'de:
```bash
ifconfig | grep "inet " | grep -v 127.0.0.1
```

Çıktı:
```
inet 192.168.1.103 ...
```

Senin IP'n: **192.168.1.103** (veya benzeri)

#### Adım 2: RetrofitClient.kt'yi Değiştir

**Android Studio'da:**
1. Sol panelde: `app → java → com.yemekonerisistemi.app → api → RetrofitClient.kt`
2. Dosyayı aç
3. 15. satırı bul:
   ```kotlin
   private const val BASE_URL = "http://10.0.2.2:8000"
   ```
4. **DEĞIŞTIR:**
   ```kotlin
   private const val BASE_URL = "http://192.168.1.103:8000"  // Kendi IP'ni yaz!
   ```
5. **File → Save All** (Ctrl+S)
6. **Build → Clean Project**
7. **Build → Rebuild Project**
8. **Run** butonuna bas ▶

---

### ÇÖZÜM 2: Fiziksel Cihaz Kullan

Eğer fiziksel Android telefon varsa:

#### Adım 1: USB Debugging Aç
Telefonda:
1. Settings → About Phone
2. "Build Number"a 7 kez tıkla (Developer Mode açılır)
3. Settings → Developer Options → USB Debugging ✅

#### Adım 2: Telefonu Bilgisayara Bağla
1. USB kabloyla bağla
2. "USB Debugging'e izin ver" → Tamam

#### Adım 3: RetrofitClient.kt'yi Değiştir
```kotlin
private const val BASE_URL = "http://192.168.1.103:8000"  // Kendi IP'ni yaz
```

#### Adım 4: Android Studio'da Cihazı Seç
Run → Select Device → [Telefonun adı]

---

### ÇÖZÜM 3: Emulator Ayarlarını Kontrol Et

#### Adım 1: Emulator'ü Kapat
Android Studio'da emulator'ü kapat

#### Adım 2: AVD Manager'ı Aç
Tools → Device Manager (veya AVD Manager)

#### Adım 3: Emulator Ayarlarını Düzenle
1. Emulator'ün yanındaki ⋮ → Edit
2. Show Advanced Settings
3. **Network Mode**: Bridged veya NAT olmalı
4. OK

#### Adım 4: Cold Boot
Device Manager → ⋮ → Cold Boot Now

---

### ÇÖZÜM 4: Android Emulator'den Manuel Test

#### Adım 1: Emulator'ü Başlat
Android Studio → Run → Emulator

#### Adım 2: Emulator'de Chrome Aç
Emulator'deki Chrome app'i aç

#### Adım 3: Backend'e Erişmeyi Dene

Adres çubuğuna yaz:
```
http://10.0.2.2:8000/health
```

**NE GÖRÜYORSUN?**

**A) Sayfa açıldı, {"status":"healthy"} göründü:**
✅ Backend'e erişiliyor! RetrofitClient kodunda sorun var.

**B) "Site can't be reached" hatası:**
❌ Network sorunu var. ÇÖZÜM 1'i dene (yerel IP kullan).

---

## 🎯 Hangi Çözümü Deniyorsun?

### EN KOLAY: Çözüm 1 (Yerel IP)

1. Terminal'de IP'ni bul:
   ```bash
   ifconfig | grep "inet " | grep -v 127.0.0.1
   ```

2. RetrofitClient.kt'de değiştir:
   ```kotlin
   private const val BASE_URL = "http://[SENIN_IP]:8000"
   ```

3. Clean → Rebuild → Run

---

## 📸 Ekran Görüntüleri İle Yardım

Eğer kafan karıştıysa, şunları göster:

1. **RetrofitClient.kt** dosyasının ekran görüntüsü (15. satır görünsün)
2. **Emulator'de Chrome'da** `http://10.0.2.2:8000/health` açtığında ne görünüyor?
3. **Terminal'de** `ifconfig | grep "inet "` çıktısı

Bu bilgilerle tam olarak çözeceğim! 🔧
