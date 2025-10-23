# Yemek Öneri Sistemi
## RAG Tabanlı Kişiselleştirilmiş Yemek Öneri Sistemi

**Proje Sahibi:** Ayşe Belen Pısdıl
**Üniversite:** Konya Teknik Üniversitesi - Bilgisayar Mühendisliği
**Proje Türü:** Bitirme Projesi

## 📋 Proje Özeti
Bu sistem, kullanıcıların evdeki malzemelerine (envanter) ve geçmiş tercihlerine göre kişiselleştirilmiş yemek tarifleri öneren, RAG (Retrieval-Augmented Generation) mimarisi kullanarak önerilerin nedenini açıklayabilen akıllı bir yemek asistanıdır.

## 🏗️ Sistem Mimarisi

```
yemekonerisistemi/
├── backend/                 # Python Backend (FastAPI)
│   ├── api/                # API endpoint'leri
│   ├── core/               # Öneri motoru, RAG sistemi
│   ├── data/               # Veri dosyaları
│   │   ├── raw/           # Ham veri
│   │   ├── processed/     # İşlenmiş veri
│   │   └── vectors/       # FAISS vektörleri
│   ├── models/            # Veri modelleri
│   ├── services/          # İş mantığı servisleri
│   ├── utils/             # Yardımcı fonksiyonlar
│   ├── tests/             # Unit testler
│   └── config/            # Yapılandırma dosyaları
│
├── android-app/            # Android Uygulaması (Kotlin)
│   ├── app/               # Ana uygulama kodu
│   ├── gradle/            # Gradle yapılandırması
│   └── docs/              # Android dokümantasyonu
│
└── docs/                   # Proje dokümantasyonu
```

## 🚀 Özellikler

### Temel Özellikler
- ✅ Envanter tabanlı tarif önerisi
- ✅ Kullanıcı alışkanlıklarını öğrenme
- ✅ RAG ile açıklanabilir öneriler
- ✅ Besin değeri hesaplama
- ✅ Alerjik madde uyarıları

### Gelişmiş Özellikler (Planlanan)
- 🔄 Eksik malzeme tespiti ve alternatif öneriler
- 🔄 Market listesi oluşturma
- 🔄 Sosyal özellikler (tarif paylaşımı)
- 🔄 Sesli asistan entegrasyonu

## 💻 Teknoloji Yığını

### Backend (Python)
- **Framework:** FastAPI
- **ML/NLP:** sentence-transformers, FAISS
- **Veri İşleme:** pandas, numpy
- **Veritabanı:** SQLite (development), PostgreSQL (production)

### Frontend (Android)
- **Dil:** Kotlin
- **Min SDK:** 24 (Android 7.0)
- **Mimari:** MVVM
- **Network:** Retrofit + OkHttp
- **Async:** Coroutines + Flow

## 📦 Kurulum

### Backend Kurulumu
```bash
cd backend
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate
pip install -r requirements.txt
python main.py
```

### Android Kurulumu
1. Android Studio'yu açın
2. `android-app` klasörünü açın
3. Gradle sync tamamlanmasını bekleyin
4. Emulator veya fiziksel cihazda çalıştırın

## 🧪 Test
```bash
# Backend testleri
cd backend
pytest tests/

# Android testleri
# Android Studio içinden çalıştırılır
```

## 📈 Geliştirme Fazları

### Faz 1: Temel Sistem (Aktif) ✅
- [x] Proje yapısı oluşturma
- [ ] Veri seti hazırlama ve temizleme
- [ ] Baseline öneri motoru
- [ ] FastAPI ile REST API
- [ ] Temel Android UI

### Faz 2: RAG Entegrasyonu 🔄
- [ ] FAISS vektör DB kurulumu
- [ ] Embedding model entegrasyonu
- [ ] RAG pipeline implementasyonu
- [ ] Açıklanabilir öneriler

### Faz 3: Kişiselleştirme 📅
- [ ] Kullanıcı profili sistemi
- [ ] Alışkanlık öğrenme modülü
- [ ] Feedback loop implementasyonu

## 📝 Lisans
Bu proje Konya Teknik Üniversitesi Bilgisayar Mühendisliği Bölümü bitirme projesi kapsamında geliştirilmektedir.

## 📧 İletişim
**Geliştirici:** Ayşe Belen Pısdıl
**E-posta:** [Üniversite e-postası]
**LinkedIn:** [Profil linki]

---
*Son güncelleme: 2025*