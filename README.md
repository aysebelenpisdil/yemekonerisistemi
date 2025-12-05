# Yemek Oneri Sistemi

**Proje Sahibi:** Ayse Belen Pisdil
**Universite:** Konya Teknik Universitesi - Bilgisayar Muhendisligi
**Proje Turu:** Bitirme Projesi

---

## Proje Aciklamasi

Kullanicilarin evdeki malzemelerine gore kisisellestirilmis yemek tarifleri oneren akilli bir mobil uygulama ve backend sistemi.

---

## Tamamlanan Calismalarin Detayli Aciklamasi

### 1. Backend Gelistirme (Python/FastAPI)

#### API Endpoints
- **GET /api/ingredients/** - Malzeme arama (fuzzy search destekli)
- **GET /api/recipes/** - Tarif listeleme ve filtreleme
- **POST /api/recipes/recommendations** - Malzemelere gore tarif onerisi
- **GET /api/recipes/{id}** - Tarif detayi
- **GET /health** - Sistem sagligi kontrolu

#### Veritabani Yapisi
- SQLAlchemy ORM ile model tanimlari
- Alembic ile veritabani migration yonetimi
- Recipe ve Ingredient tablolari
- JSON tabanli demo veri destegi

#### Servis Katmani
- **RecipeService**: Tarif arama, filtreleme ve oneri mantigi
- **IngredientService**: Malzeme arama ve fuzzy matching
- **SemanticService**: Semantik arama altyapisi (embedding destegi)

#### Ozellikler
- Fuzzy search ile yazim hatalarini tolere eden arama
- Turkce karakter destegi (cilek -> Cilek)
- Pisirme suresi ve kalori bazli filtreleme
- Latency loglama ve performans izleme

### 2. Android Uygulama Gelistirme (Kotlin)

#### Mimari Yapi
- MVVM (Model-View-ViewModel) tasarim deseni
- Fragment tabanli navigasyon
- LiveData ve ViewModel ile reaktif UI

#### Ekranlar ve Ozellikler
- **Ana Sayfa**: Tarif onerileri ve arama
- **Envanter Yonetimi**: Kullanicinin malzemelerini ekleme/cikarma
- **Tarif Detay**: Adim adim pisirme talimatlari
- **Profil**: Kullanici tercihleri ve ayarlar

#### Network Katmani
- Retrofit ile REST API entegrasyonu
- OkHttp interceptor ile loglama
- 30 saniye timeout yapilandirmasi
- BuildConfig ile ortam bazli URL yonetimi (debug/release)

#### UI Bilesenleri
- Material Design 3 uyumu
- RecyclerView ile liste goruntuleme
- CardView ile tarif kartlari
- ConstraintLayout ile responsive tasarim
- Glide ile gorsel yukleme

---

## Kullanilan Teknolojiler ve Araclar

### Backend

| Teknoloji | Versiyon | Kullanim Amaci |
|-----------|----------|----------------|
| Python | 3.x | Ana programlama dili |
| FastAPI | - | REST API framework |
| Uvicorn | - | ASGI server |
| SQLAlchemy | - | ORM (Object-Relational Mapping) |
| Alembic | - | Veritabani migration |
| Pydantic | - | Veri validasyonu |

### Android

| Teknoloji | Versiyon | Kullanim Amaci |
|-----------|----------|----------------|
| Kotlin | - | Ana programlama dili |
| Android SDK | 34 | Hedef platform |
| Min SDK | 24 | Minimum desteklenen Android 7.0 |
| Retrofit | 2.9.0 | HTTP client |
| OkHttp | 4.12.0 | Network interceptor |
| Gson | - | JSON parsing |
| Glide | 4.16.0 | Gorsel yukleme |
| Coroutines | 1.7.3 | Asenkron islemler |
| Navigation Component | 2.7.6 | Ekran gecisleri |
| Material Components | 1.11.0 | UI tasarim |
| DataStore | 1.0.0 | Lokal veri saklama |

### Gelistirme Araclari

| Arac | Kullanim Amaci |
|------|----------------|
| Android Studio | Android IDE |
| Git/GitHub | Versiyon kontrolu |
| Gradle (Kotlin DSL) | Build sistemi |

---

## Proje Yapisi

```
yemekonerisistemi/
├── backend/
│   ├── api/                 # API endpoint tanimlari
│   ├── db/                  # Veritabani modelleri
│   ├── models/              # Pydantic modelleri
│   ├── services/            # Is mantigi servisleri
│   ├── data/                # Demo veri dosyalari
│   ├── alembic/             # Migration dosyalari
│   ├── scripts/             # Yardimci scriptler
│   ├── main.py              # Uygulama giris noktasi
│   └── requirements.txt     # Python bagimliliklari
│
├── android-app/
│   └── app/src/main/
│       ├── java/.../
│       │   ├── api/         # Retrofit client ve servisler
│       │   ├── models/      # Veri modelleri
│       │   ├── ui/          # Fragment ve Activity
│       │   └── adapters/    # RecyclerView adapterleri
│       └── res/             # Layout, drawable, values
│
├── scripts/                 # Calistirma scriptleri
├── docs/                    # Proje dokumanlari
└── README.md
```

---

## Calistirma

### Backend
```bash
./scripts/run_backend.sh
```
Backend http://localhost:8000 adresinde calisir.
API dokumantasyonu: http://localhost:8000/docs

### Android
1. Android Studio ile `android-app` klasorunu ac
2. Gradle sync tamamlanmasini bekle
3. Emulator veya fiziksel cihazda calistir

---

*Son guncelleme: Aralik 2025*
