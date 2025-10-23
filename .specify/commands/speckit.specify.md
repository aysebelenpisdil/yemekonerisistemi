# Yemek Öneri Sistemi - Spesifikasyon

## Proje Özeti
Kullanıcıların beslenme alışkanlıklarını, besin değerlerini ve envanter durumunu dikkate alarak kişiselleştirilmiş yemek önerileri sunan mobil uygulama.

## Temel Özellikler

### 1. Kullanıcı Yönetimi
- Kullanıcı kaydı ve girişi
- Profil yönetimi
- Beslenme tercihleri (vegan, vejetaryen, glutensiz vb.)
- Alerji ve kısıtlama bilgileri
- Hedef kalori ve makro besin takibi

### 2. Envanter Yönetimi (Buzdolabı Ekranı)
**Modern UI/UX Gereksinimleri:**
- ✅ Beyaz-krem-mavi renk paleti (#4A90E2, #F5F5DC, #FFFFFF)
- ✅ Online alışveriş platformu (Trendyol) benzeri kullanıcı deneyimi
- ✅ Profesyonel card yerleşimi ve 8dp spacing sistemi
- ✅ 12dp yuvarlatılmış köşeler, 2-4dp elevation
- 🔧 Renk tutarlılığı: Tüm butonlar mavi tema ile uyumlu olmalı

**NLP/ML Destekli Kategorizasyon Sistemi:**
- 🎯 467 malzeme otomatik olarak kategorilere ayrılmalı
- 📦 **Ana Kategoriler:**
  1. 🥬 Sebzeler (Domates, Biber, Salatalık, Patlıcan, vb.)
  2. 🍎 Meyveler (Elma, Armut, Muz, Portakal, vb.)
  3. 🍗 Et & Tavuk (Tavuk Göğsü, Kıyma, Dana Eti, vb.)
  4. 🐟 Balık & Deniz Ürünleri (Somon, Levrek, Karides, vb.)
  5. 🥛 Süt Ürünleri (Süt, Yoğurt, Peynir, Tereyağı, vb.)
  6. 🌾 Tahıllar & Bakliyat (Pirinç, Makarna, Nohut, Mercimek, vb.)
  7. 🥜 Kuruyemişler (Fındık, Badem, Ceviz, vb.)
  8. 🧂 Baharatlar & Soslar (Tuz, Biber, Ketçap, vb.)
  9. 🍹 İçecekler (Su, Meyve Suyu, Çay, Kahve, vb.)
  10. 🍞 Fırın & Hamur İşleri (Ekmek, Pasta, Börek, vb.)
  11. 🍯 Tatlı & Şekerlemeler (Bal, Reçel, Çikolata, vb.)
  12. 🌿 Diğer (Kategorize edilemeyen malzemeler)

- 🤖 **NLP Algoritması:**
  - Kelime bazlı pattern matching
  - Keyword detection (örn: "süt", "yağlı", "peynir" → Süt Ürünleri)
  - Fuzzy matching ile benzer malzemeleri gruplama
  - Backend'de kategori bilgisi cache'lenmeli

**Gelişmiş Arama & Filtreleme (Trendyol Benzeri):**
- 🔍 **Arama Çubuğu:**
  - Real-time autocomplete (467 malzeme)
  - Typo tolerance (fuzzy search)
  - Son aramalar geçmişi
- ⚙️ **Filtre İkonu:**
  - Arama çubuğunun SAĞ tarafında, + ikonunun YANINDA
  - Bottom sheet ile kategori seçimi
  - Çoklu kategori seçimi (checkbox'larla)
  - "Tümünü Seç" / "Temizle" butonları
  - Seçili filtre sayısı badge'i (örn: ⚙️ 3)
- 🎯 **Filtreleme Deneyimi:**
  - Trendyol gibi: "Kategori > Alt Kategori" hiyerarşisi
  - Filtrelenmiş sonuç sayısı gösterimi
  - Aktif filtreleri gösteren chip'ler
  - Her chip'in × butonu ile kaldırma

**Sıralama Seçenekleri:**
- 📊 Sıralama Menüsü (Dropdown/Popup Menu):
  1. A → Z (Alfabetik)
  2. Z → A (Ters Alfabetik)
  3. Miktar (Az → Çok)
  4. Miktar (Çok → Az)
  5. Son Eklenenler
  6. 📂 Kategoriye Göre Grupla (Expandable list)
- 🎨 Varsayılan: Alfabetik sıralama
- 💾 Kullanıcının seçimi kaydedilmeli (SharedPreferences)

**Toolbar Layout (GÜNCELLENMİŞ):**
```
┌────────────────────────────────────────┐
│  Buzdolabım                  [🔍] [⚙️] [⬇️] │ <- Toolbar (Mavi)
└────────────────────────────────────────┘
│ [🔎 Malzeme ara veya ekle...] [+]     │ <- Arama Card
│ [Filtre Chip: Sebzeler ×] [Meyveler ×]│ <- Aktif Filtreler
└────────────────────────────────────────┘
```

**Miktar & Birim Yönetimi:**
- 🧮 Akıllı birim sistemi (adet, gram, kg, litre, ml, yemek kaşığı, çay kaşığı, su bardağı)
- 🎯 Ürün cinsine göre otomatik birim önerisi:
  - Yumurta → adet
  - Süt → litre/ml
  - Domates → adet/kg
  - Tuz → gram/çay kaşığı
- ➕➖ Miktar artırma/azaltma (+/- butonları)
- 📊 Görsel miktar göstergeleri

**Malzeme Kartları (Modern Tasarım):**
- 🎴 Modern card layout:
  ```
  ┌─────────────────────────────────────┐
  │ [📷 48dp] Malzeme Adı     [- 5 +] 🗑️│
  │           [🏷️ Kategori]   [adet]    │
  └─────────────────────────────────────┘
  ```
- 🖼️ Sol: Malzeme görseli/ikonu (48dp)
- 📝 Orta: Adı + Kategori badge
- 🎛️ Sağ: Miktar kontrolleri + Sil butonu
- 🎨 12dp corner radius, 2dp elevation
- 👆 Swipe-to-delete desteği
- 🎨 Kategori badge renkleri (colors.xml'de tanımlı)

### 3. Yemek Tarifi Veritabanı
- Geniş tarif koleksiyonu
- Kategorilere göre sınıflandırma (kahvaltı, öğle, akşam, atıştırmalık)
- Mutfak türlerine göre filtreleme
- Zorluk seviyesi göstergesi
- Hazırlama ve pişirme süreleri

### 4. Öneri Motoru
- Mevcut envantere göre yapılabilecek yemekler
- Besin değeri hedeflerine uygun öneriler
- Kullanıcı tercihlerine göre filtreleme
- Eksik malzeme sayısına göre sıralama
- Günlük/haftalık yemek planı önerisi

### 5. Besin Değeri Takibi
- Kalori hesaplama
- Makro besinler (protein, karbonhidrat, yağ)
- Mikro besinler (vitaminler, mineraller)
- Günlük/haftalık/aylık raporlama
- Hedef takibi ve ilerleme göstergesi

### 6. Sosyal Özellikler
- Tarif paylaşma
- Yorum ve değerlendirme
- Favorilere ekleme
- Tarif koleksiyonları oluşturma

### 7. Bildirimler
- Yemek zamanı hatırlatıcıları
- Son kullanma tarihi uyarıları
- Alışveriş listesi hatırlatmaları
- Yeni tarif önerileri

## Teknik Gereksinimler

### Frontend (React Native)
- iOS ve Android desteği
- Offline mode capability
- Push notifications
- Camera integration (malzeme fotoğrafı için)
- Barcode scanner (ürün ekleme için)

### Backend
- RESTful API
- Real-time sync
- Secure authentication
- Image storage ve optimization
- Search ve filtering engine

### Veritabanı
- User profiles
- Recipe database
- Inventory tracking
- Nutritional information
- Activity logs