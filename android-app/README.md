
# Yemek Öneri Sistemi - Android Uygulaması

## Kurulum
1. Android Studio'yu açın
2. "Open an existing project" seçeneğini tıklayın
3. Bu klasörü (`android-app`) seçin
4. Gradle sync işleminin tamamlanmasını bekleyin

## Teknoloji Yığını
- **Dil:** Kotlin
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)
- **Kullanılan Kütüphaneler:**
  - Retrofit (API iletişimi)
  - Coroutines (Asenkron işlemler)
  - ViewModel & LiveData (MVVM mimarisi)
  - Room (Local cache)
  - Glide (Görsel yükleme)

## Ekranlar
1. **Splash Screen** - Uygulama açılışı
2. **Login/Register** - Kullanıcı girişi
3. **Home** - Ana ekran, hızlı öneriler
4. **Inventory** - Envanter yönetimi
5. **Recipe Detail** - Tarif detayları
6. **Profile** - Kullanıcı tercihleri

## API Bağlantısı
Backend URL: `http://localhost:8000/api/`
(Production'da güncellenmeli)