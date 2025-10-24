# 🔧 Android - Backend Bağlantı Kurulum ve Debug Kılavuzu

## ✅ Backend Durumu
- **Teknoloji**: FastAPI (Python)
- **Port**: 8000
- **Durum**: ✅ ÇALIŞIYOR
- **API Base URL**: http://localhost:8000
- **Dokümantasyon**: http://localhost:8000/docs

## 📱 Android Studio'da Yapmanız Gerekenler

### 1. Backend'i Başlatın
```bash
# Terminal'de projenin ana dizinine gidin
cd /Users/belenikov/yemekonerisistemi/backend

# Backend'i başlatın
python3 main.py

# Veya hazır script ile
cd /Users/belenikov/yemekonerisistemi
./BACKEND_BASLAT.sh
```

### 2. Android Studio'da Kontrol Edilecekler

#### A. RetrofitClient.kt Dosyası
Dosya yolu: `android-app/app/src/main/java/com/yemekonerisistemi/app/api/RetrofitClient.kt`

```kotlin
// EMULATOR için doğru URL (✅ DOĞRU)
private const val BASE_URL = "http://10.0.2.2:8000"

// Fiziksel cihaz için (gerekirse değiştirin)
// private const val BASE_URL = "http://192.168.1.X:8000"
```

#### B. AndroidManifest.xml İzinleri
Dosya yolu: `android-app/app/src/main/AndroidManifest.xml`

```xml
<!-- İnternet izni OLMALI -->
<uses-permission android:name="android.permission.INTERNET" />

<!-- Android 9+ için (API 28+) -->
<application
    ...
    android:usesCleartextTraffic="true"
    ...>
```

### 3. 🧪 Bağlantı Testi

#### Terminal'den Test
```bash
# Backend çalışıyor mu?
curl http://localhost:8000/

# Ingredients API çalışıyor mu?
curl "http://localhost:8000/api/ingredients/?q=domates&limit=3"
```

#### Android Emulator'den Test
1. Emulator'de Chrome tarayıcıyı açın
2. Adrese gidin: `http://10.0.2.2:8000`
3. JSON response görmelisiniz

### 4. 🐛 Android Studio Debug İpuçları

#### Logcat'te HTTP Logları
Android Studio'da:
1. Logcat penceresini açın (Alt + 6)
2. Filtre ekleyin: `OkHttp` veya `Retrofit`
3. Uygulamayı çalıştırın ve logları izleyin

#### Network Inspector
1. View → Tool Windows → App Inspection
2. Network Inspector sekmesini açın
3. API çağrılarını gerçek zamanlı izleyin

### 5. 🔴 Sık Karşılaşılan Hatalar ve Çözümleri

#### Hata: "Failed to connect to /10.0.2.2:8000"
**Çözüm**:
- Backend'in çalıştığından emin olun
- Terminal'de `lsof -i:8000` ile port durumunu kontrol edin

#### Hata: "CLEARTEXT communication not permitted"
**Çözüm**:
- AndroidManifest.xml'e `android:usesCleartextTraffic="true"` ekleyin

#### Hata: "UnknownHostException"
**Çözüm**:
- Emulator'de internet bağlantısını kontrol edin
- DNS ayarlarını kontrol edin

#### Hata: "SocketTimeoutException"
**Çözüm**:
- RetrofitClient.kt'de timeout sürelerini artırın:
```kotlin
.connectTimeout(60, TimeUnit.SECONDS)
.readTimeout(60, TimeUnit.SECONDS)
```

### 6. 📝 Test Senaryosu

1. **Backend'i başlatın** ve çalıştığını doğrulayın
2. **Android Studio'da** uygulamayı çalıştırın
3. **İkinci sayfaya** (malzemeler) gidin
4. **Logcat'i** kontrol edin:
   - HTTP 200 response görmelisiniz
   - JSON data logları görmelisiniz

### 7. 🚀 Başarılı Bağlantı Göstergeleri

✅ Backend konsolu "INFO: 127.0.0.1:XXXX - 'GET /api/ingredients/ HTTP/1.1' 200 OK" gösteriyor
✅ Android Logcat'te "OkHttp" loglarında response body görünüyor
✅ Uygulama malzeme listesini gösteriyor
✅ Arama yapıldığında sonuçlar geliyor

## 📌 Önemli Notlar

1. **Her zaman önce backend'i başlatın**, sonra Android uygulamasını çalıştırın
2. **Emulator kullanıyorsanız** 10.0.2.2 adresini kullanın
3. **Fiziksel cihaz kullanıyorsanız** bilgisayarınızın IP adresini kullanın
4. **Backend loglarını izleyin** - hangi endpoint'lere istek geldiğini görebilirsiniz

## 🆘 Hala Sorun mu Var?

1. Backend loglarını paylaşın (terminal çıktısı)
2. Android Studio Logcat çıktısını paylaşın (OkHttp filtrelenmiş)
3. AndroidManifest.xml dosyanızı kontrol edin
4. RetrofitClient.kt dosyanızdaki BASE_URL'i kontrol edin

---

🎯 **Backend Şu Anda Çalışıyor**: http://localhost:8000
📱 **Emulator İçin URL**: http://10.0.2.2:8000
📚 **API Dokümantasyonu**: http://localhost:8000/docs