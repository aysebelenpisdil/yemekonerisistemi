# 🎨 Yemek Öneri Sistemi - UI/UX Tasarım Konsepti

## 📱 Uygulama Akışı

### 1️⃣ EKRAN: BUZDOLABIM (Envanter Yönetimi)

#### Tasarım Özellikleri:
```
┌─────────────────────────────────┐
│  ≡  Buzdolabım           🔍 👤  │
├─────────────────────────────────┤
│                                 │
│  [🔍 Malzeme ara veya ekle] [+] │
│                                 │
│  ▼ Sebzeler                    │
│  ┌──────┐ ┌──────┐ ┌──────┐   │
│  │Domates│ │Biber │ │Soğan │   │
│  └──────┘ └──────┘ └──────┘   │
│                                 │
│  ▼ Et & Tavuk                  │
│  ┌──────┐ ┌──────────┐         │
│  │Tavuk │ │Kıyma (500g)│        │
│  └──────┘ └──────────┘         │
│                                 │
│  ▼ Süt Ürünleri                │
│  ┌──────┐ ┌──────┐             │
│  │Yoğurt│ │Peynir│             │
│  └──────┘ └──────┘             │
│                                 │
│  ▼ Temel Gıda                  │
│  ┌──────┐ ┌──────┐ ┌──────┐   │
│  │Makarna│ │Pirinç│ │Un    │   │
│  └──────┘ └──────┘ └──────┘   │
│                                 │
├─────────────────────────────────┤
│  [     TARİFLERİ BUL 🍳     ]  │
└─────────────────────────────────┘
```

#### Renk Paleti:
- **Ana Renk (Primary):** #FF6B35 (Turuncu - İştah açıcı)
- **İkincil Renk:** #4ECDC4 (Turkuaz - Tazelik)
- **Arka Plan:** #FAFAFA (Açık gri)
- **Kategori Başlıkları:** #2D3436
- **Malzeme Etiketleri:** Beyaz arka plan, renkli kenar

#### Etkileşim Detayları:
- Malzeme ekleme: Otomatik tamamlama ile
- Kategori grupları: Genişletilip daraltılabilir
- Her malzemenin yanında miktar bilgisi eklenebilir
- Silme: Sağa kaydırma veya 'x' ikonu

---

### 2️⃣ EKRAN: SANA ÖZEL ÖNERİLER

#### Tasarım Özellikleri:
```
┌─────────────────────────────────┐
│  ←  Sana Özel Öneriler    🔍   │
├─────────────────────────────────┤
│                                 │
│  ✨ 12 tarif bulundu            │
│                                 │
│ ┌───────────────────────────┐   │
│ │  [Görsel: Tavuk Sote]     │   │
│ │                           │   │
│ │  TAVUK SOTE               │   │
│ │  ⏱️ 30 dk | 🔥 280 kal    │   │
│ │                           │   │
│ │  💡 Neden önerildi?       │   │
│ │  "Tavuk ve sebzelerinle   │   │
│ │   mükemmel uyum!"         │   │
│ │                           │   │
│ │  📦 Elindekiler: Tavuk,   │   │
│ │     Domates, Biber        │   │
│ └───────────────────────────┘   │
│                                 │
│ ┌───────────────────────────┐   │
│ │  [Görsel: Makarna]        │   │
│ │                           │   │
│ │  KREMANLI MAKARNA         │   │
│ │  ⏱️ 20 dk | 🔥 420 kal    │   │
│ │                           │   │
│ │  💡 "Hızlı yemek          │   │
│ │     tercihine uygun!"     │   │
│ └───────────────────────────┘   │
│                                 │
└─────────────────────────────────┘
```

#### Kart Tasarımı Özellikleri:
- **Kart Gölgesi:** elevation: 4dp
- **Border Radius:** 12dp
- **Görsel Yüksekliği:** 180dp
- **Kişiselleştirme Vurgusu:** Açık mavi arka plan
- **Font Hiyerarşisi:**
  - Tarif Adı: 18sp, Bold
  - Açıklama: 14sp, Regular
  - Meta bilgi: 12sp, Light

---

### 3️⃣ EKRAN: TARİF DETAYI

#### Tasarım Özellikleri:
```
┌─────────────────────────────────┐
│  ←  Tarif Detayı          ❤️ ⤴️ │
├─────────────────────────────────┤
│                                 │
│  [   BÜYÜK TARİF GÖRSELİ   ]   │
│  [        (Hero Image)      ]   │
│                                 │
│  TAVUK SOTE                    │
│  ⭐⭐⭐⭐⭐ (4.8)                 │
│                                 │
│  👥 4 Kişi | ⏱️ 30dk | 🔥280kal │
│                                 │
│ ┌───────────────────────────┐   │
│ │ 💡 NEDEN SANA ÖNERİLDİ?   │   │
│ │                           │   │
│ │ "Buzdolabındaki tavuk ve  │   │
│ │  sebzeleri kullanıyor.    │   │
│ │  Ayrıca geçen hafta       │   │
│ │  benzer tarifleri         │   │
│ │  beğenmiştin!"            │   │
│ └───────────────────────────┘   │
│                                 │
│  📋 MALZEMELER                  │
│  ─────────────                  │
│  ✅ 500g tavuk göğsü           │
│  ✅ 2 adet domates             │
│  ✅ 2 adet biber               │
│  ❌ 1 su bardağı krema        │
│     [Listeye Ekle]            │
│  ✅ Tuz, karabiber             │
│                                 │
│  👨‍🍳 HAZIRLANIŞI                │
│  ─────────────                  │
│  1️⃣ Tavukları kuşbaşı doğrayın │
│                                 │
│  2️⃣ Sebzeleri yıkayıp doğrayın │
│                                 │
│  3️⃣ Tavukları sıvı yağda      │
│     soteleyin...               │
│                                 │
└─────────────────────────────────┘
```

#### Özel Bileşenler:
- **Hero Image:** Parallax scroll efekti
- **Floating Action Button:** Favorilere ekleme
- **Chip Groups:** Malzeme durumu gösterimi
- **Stepper Layout:** Adım adım talimatlar

---

## 🎨 Material Design 3 Tema Yapısı

### Renk Şeması:
```kotlin
Primary: #FF6B35 (Warm Orange)
OnPrimary: #FFFFFF
PrimaryContainer: #FFE5DB
OnPrimaryContainer: #3A1607

Secondary: #4ECDC4 (Teal)
OnSecondary: #FFFFFF
SecondaryContainer: #D7F5F3
OnSecondaryContainer: #00201E

Background: #FAFAFA
OnBackground: #1C1B1F
Surface: #FFFFFF
OnSurface: #1C1B1F

Error: #BA1A1A
OnError: #FFFFFF
```

### Typography:
```kotlin
Display Large: Roboto 57sp
Display Medium: Roboto 45sp
Display Small: Roboto 36sp

Headline Large: Roboto 32sp
Headline Medium: Roboto 28sp
Headline Small: Roboto 24sp

Title Large: Roboto Medium 22sp
Title Medium: Roboto Medium 16sp
Title Small: Roboto Medium 14sp

Body Large: Roboto 16sp
Body Medium: Roboto 14sp
Body Small: Roboto 12sp
```

### Komponent Stilleri:
- **Button:** Rounded corners (8dp), elevation 2dp
- **Card:** Rounded corners (12dp), elevation 4dp
- **TextField:** Outlined variant, rounded corners (8dp)
- **Chip:** Rounded (16dp), outlined or filled
- **FAB:** Extended FAB with icon and text

---

## 📱 Responsive Design Notları

### Telefon (Compact - width < 600dp):
- Tek sütun layout
- Bottom navigation
- Fullscreen modals

### Tablet (Medium - 600dp < width < 840dp):
- 2 sütun grid for cards
- Navigation rail
- Dialog modals

### Büyük Tablet (Expanded - width > 840dp):
- 3 sütun grid
- Navigation drawer
- Side-by-side layouts

---

## 🚀 Animasyon ve Geçişler

### Ekran Geçişleri:
- **Shared Element Transition:** Tarif kartından detay sayfasına
- **Fade Through:** Tab değişimleri
- **Container Transform:** FAB'dan yeni ekrana

### Mikro Animasyonlar:
- **Ripple Effect:** Tüm tıklanabilir elementlerde
- **Elevation Change:** Hover ve focus durumlarında
- **Progress Indicators:** Yükleme durumlarında
- **Chip Selection:** Scale ve color animasyonu

---

## ♿ Erişilebilirlik

- Minimum dokunma alanı: 48dp x 48dp
- Renk kontrastı: WCAG AA standartları
- Screen reader desteği: ContentDescription
- Keyboard navigation support
- Font scaling support (sp kullanımı)