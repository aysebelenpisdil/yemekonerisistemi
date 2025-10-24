# Yemek Öneri Sistemi — Spesifikasyon (Android/Kotlin + FastAPI)

## Özet
Envanter, tercih ve hedefleri dikkate alarak kişiselleştirilmiş yemek önerileri sunan Android uygulaması. Backend FastAPI, mobil istemci Kotlin.

## Mevcut Odak: UI Tema ve Deneyim
- Tasarım sistemi: YOS Calm Theme
  - Kaynaklar: `values/colors_yos.xml`, `values/styles_yos.xml`, `values/themes_yos.xml`
  - Tema: `@style/Theme.YOS` (Activity bazında, kademeli yayılım)
- Bileşenler: Button/Card/Chip/SearchBar için `Widget.YOS.*` stilleri
- Tipografi: `TextAppearance.YOS.*`

Başarı Kriterleri:
- Build PASS; AAPT hatası yok.
- Home, Inventory, Recipe List ekranlarında YOS stili uygulanmış.
- Erişilebilirlik kontrastı ve dokunma hedefleri yeterli.

## Ana Özellikler (UI/UX)

### Envanter (Buzdolabı) Ekranı
- Arama çubuğu: `Widget.YOS.SearchBar`, fuzzy arama backend entegrasyonu
- Filtre chip’leri: `Widget.YOS.Chip.*`
- Kartlar: `Widget.YOS.Card.Recipe` veya varyantı
- Sıralama ve filtre bottom sheet (Material Components)

### Tarif Liste/Detay
- Liste kartları YOS stilleri; boş/yükleme durumları
- Detay sayfasında içerik hiyerarşisi ve tipografi (`TextAppearance.YOS.H*`, `Body`, `Caption`)

## Teknik Gereksinimler

### Android (Kotlin)
- Retrofit + OkHttp + Gson; BASE_URL (emülatör): `http://10.0.2.2:8000`
- Material Components; tema: `Theme.YemekOneri` (genel) ve `Theme.YOS` (Activity bazlı)
- Kaynak dosyaları: `colors_yos.xml`, `styles_yos.xml`, `themes_yos.xml`

### Backend (FastAPI)
- Host: 0.0.0.0:8000, Health: `/health`
- CORS açık; fuzzy search ve tarif arama uçları

## Kapsam Dışı (bu fazda)
- React Native/TypeScript
- iOS kurulumları
- Gerçek zamanlı bildirimler, kapsamlı auth akışı

## Kabul Kriterleri (UI Fazı)
- [ ] Home/Inventory/RecipeList ekranlarında YOS tema ve stiller etkin
- [ ] Manifest’te MainActivity `Theme.YOS` ile açılıyor
- [ ] Arama/filtre/boş-durum UI’ları görsel doğrulandı
- [ ] assembleDebug PASS, temel smoke gezinme PASS