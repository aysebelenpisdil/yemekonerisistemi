# Modern Buzdolabı Ekranı - Implementation Planı

## 🎨 FAZ 1: Design System & Renk Teması (1 gün)

### 1.1 Renk Paleti Tanımlama
**Dosya:** `res/values/colors.xml`

```xml
<!-- Modern Beyaz-Krem-Mavi Paleti -->
<color name="primary">#4A90E2</color>           <!-- Modern Mavi -->
<color name="primary_dark">#357ABD</color>       <!-- Koyu Mavi -->
<color name="primary_light">#5DADE2</color>      <!-- Açık Mavi -->

<color name="secondary">#F5F5DC</color>          <!-- Krem/Bej -->
<color name="secondary_variant">#FAF8F3</color>  <!-- Açık Krem -->

<color name="background">#FFFFFF</color>         <!-- Beyaz -->
<color name="surface">#F8F9FA</color>           <!-- Açık Gri Yüzey -->

<color name="text_primary">#2C3E50</color>      <!-- Koyu Gri -->
<color name="text_secondary">#7F8C8D</color>    <!-- Orta Gri -->
<color name="text_hint">#BDC3C7</color>         <!-- Açık Gri -->

<color name="divider">#E0E0E0</color>           <!-- Ayırıcı Çizgi -->
<color name="card_border">#ECEFF1</color>       <!-- Card Kenar -->

<color name="success">#27AE60</color>           <!-- Yeşil -->
<color name="warning">#F39C12</color>           <!-- Turuncu -->
<color name="error">#E74C3C</color>             <!-- Kırmızı -->
```

### 1.2 Tipografi Stilleri
**Dosya:** `res/values/styles.xml`

```xml
<!-- Başlıklar -->
<style name="TextAppearance.App.Headline1">
    <item name="android:textSize">24sp</item>
    <item name="android:textStyle">bold</item>
    <item name="android:textColor">@color/text_primary</item>
    <item name="fontFamily">@font/roboto_bold</item>
</style>

<!-- Gövde Metinler -->
<style name="TextAppearance.App.Body1">
    <item name="android:textSize">16sp</item>
    <item name="android:textColor">@color/text_primary</item>
    <item name="fontFamily">@font/roboto_regular</item>
</style>

<!-- Açıklamalar -->
<style name="TextAppearance.App.Caption">
    <item name="android:textSize">12sp</item>
    <item name="android:textColor">@color/text_secondary</item>
    <item name="fontFamily">@font/roboto_light</item>
</style>
```

### 1.3 Card Stilleri
```xml
<style name="CardView.Modern">
    <item name="cardCornerRadius">12dp</item>
    <item name="cardElevation">2dp</item>
    <item name="cardBackgroundColor">@color/background</item>
    <item name="contentPadding">16dp</item>
</style>
```

---

## 🏷️ FAZ 2: NLP Kategorizasyon Sistemi (2 gün)

### 2.1 Backend: Kategori Algoritması
**Yeni Endpoint:**
```python
# backend/category_classifier.py
class IngredientCategorizer:
    """467 malzemeyi 12 kategoriye ayır"""

    CATEGORIES = {
        "vegetables": ["domates", "biber", "salatalık", "patlıcan", "kabak"],
        "fruits": ["elma", "armut", "muz", "portakal", "üzüm"],
        "meat_poultry": ["tavuk", "hindi", "kıyma", "dana", "kuzu"],
        "fish_seafood": ["balık", "somon", "levrek", "karides", "midye"],
        "dairy": ["süt", "yoğurt", "peynir", "tereyağı", "krema"],
        "grains_legumes": ["pirinç", "makarna", "nohut", "mercimek", "fasulye"],
        "nuts": ["fındık", "badem", "ceviz", "fıstık"],
        "spices_sauces": ["tuz", "biber", "kekik", "ketçap", "sos"],
        "beverages": ["su", "çay", "kahve", "meyve suyu"],
        "bakery": ["ekmek", "pasta", "börek", "tatlı"],
        "sweets": ["bal", "reçel", "çikolata", "şeker"],
        "other": []
    }
```

**API Endpoint:**
```
GET /api/ingredients/?category=vegetables
GET /api/ingredients/categories/  # Tüm kategorileri listele
```

### 2.2 Android: Kategori Model
```kotlin
// models/IngredientCategory.kt
enum class IngredientCategory(val displayName: String, val color: String, val icon: String) {
    VEGETABLES("Sebzeler", "#27AE60", "🥬"),
    FRUITS("Meyveler", "#E67E22", "🍎"),
    MEAT_POULTRY("Et & Tavuk", "#C0392B", "🍗"),
    FISH_SEAFOOD("Balık & Deniz Ürünleri", "#3498DB", "🐟"),
    DAIRY("Süt Ürünleri", "#5DADE2", "🥛"),
    GRAINS_LEGUMES("Tahıllar & Bakliyat", "#D4A373", "🌾"),
    NUTS("Kuruyemişler", "#8E44AD", "🥜"),
    SPICES_SAUCES("Baharatlar & Soslar", "#16A085", "🧂"),
    BEVERAGES("İçecekler", "#1ABC9C", "🍹"),
    BAKERY("Fırın Ürünleri", "#F39C12", "🍞"),
    SWEETS("Tatlı & Şekerlemeler", "#E91E63", "🍯"),
    OTHER("Diğer", "#95A5A6", "🌿")
}
```

### 2.3 Ingredient Data Class Güncelleme
```kotlin
data class Ingredient(
    val id: String,
    val name: String,
    val category: IngredientCategory,  // YENİ!
    val quantity: Double,
    val unit: String
)
```

---

## 🔍 FAZ 3: Gelişmiş Arama & Filtreleme (2 gün)

### 3.1 Akıllı Arama UI (Trendyol Benzeri)
**Layout Güncelleme:**
```xml
<!-- Arama Card'ı -->
<MaterialCardView>
    <LinearLayout horizontal>
        <SearchView hint="Malzeme ara veya ekle..."/>
        <IconButton icon="filter" />  <!-- YENİ! -->
        <IconButton icon="add" />
    </LinearLayout>
</MaterialCardView>
```

**Arama Özellikleri:**
- Material Design 3 SearchView
- Real-time autocomplete (467 malzeme)
- Fuzzy matching (typo tolerance)
- Search history chip'leri
- Clear button + close icon

### 3.2 Filtre Bottom Sheet (Trendyol Benzeri)
**UI Tasarımı:**
```
┌─────────────────────────────────┐
│ Filtrele                    [×] │
├─────────────────────────────────┤
│ 📦 Kategoriler                  │
│ ☐ 🥬 Sebzeler (12)              │
│ ☑ 🍎 Meyveler (8)               │
│ ☑ 🍗 Et & Tavuk (5)             │
│ ☐ 🥛 Süt Ürünleri (7)           │
│ [Tümünü Seç] [Temizle]         │
├─────────────────────────────────┤
│ 📊 Miktar                       │
│ [Slider: 0 - 10+]              │
├─────────────────────────────────┤
│      [Temizle] [Uygula (23)]   │
└─────────────────────────────────┘
```

**Filtre Özellikleri:**
- Checkbox ile çoklu seçim
- Her kategori yanında malzeme sayısı
- "Uygula" butonunda filtrelenmiş toplam
- Seçili filtreler chip olarak gösterilir
- Chip'lerde × butonu ile kaldırma

### 3.3 Filtre Chip'leri
```kotlin
// ActiveFiltersChipGroup.kt
- Seçili kategorileri chip olarak göster
- Her chip'te kategori ikonu + adı
- × butonu ile kaldırma
- Chip tıklamasıyla filter bottom sheet aç
```

**Görünüm:**
```
[🥬 Sebzeler ×] [🍎 Meyveler ×] [+ Daha fazla filtre]
```

### 3.4 Toolbar İkonları Güncelleme
```xml
<Toolbar>
    <TextView>Buzdolabım</TextView>
    <ImageButton>🔍 Ara</ImageButton>       <!-- Search icon -->
    <ImageButton>⚙️ Filtrele</ImageButton>  <!-- YENİ! Filter icon -->
    <ImageButton>⬇️ Sırala</ImageButton>    <!-- YENİ! Sort icon -->
</Toolbar>
```

---

## 🎛️ FAZ 4: Sıralama Sistemi (1 gün)

### 3.1 Filtre Bottom Sheet
**Tasarım:**
```
┌─────────────────────────────────┐
│ Filtrele                    [×] │
├─────────────────────────────────┤
│ 📦 Kategori                     │
│ □ Sebzeler                      │
│ □ Meyveler                      │
│ □ Et & Tavuk                    │
│ □ Süt Ürünleri                  │
│ □ Tahıllar                      │
├─────────────────────────────────┤
│ 📊 Miktar                       │
│ [Slider: 0 - 10+]              │
├─────────────────────────────────┤
│        [Temizle] [Uygula]      │
└─────────────────────────────────┘
```

**Kategoriler:**
- Sebzeler & Meyveler
- Et & Tavuk & Balık
- Süt Ürünleri & Peynir
- Tahıllar & Baklagiller
- Baharatlar & Soslar
- İçecekler
- Diğer

### 3.2 Sıralama Menüsü
**Popup Menu:**
```kotlin
- A → Z (Alfabetik)
- Z → A (Ters Alfabetik)
- Miktar (Az → Çok)
- Miktar (Çok → Az)
- Son Eklenenler
- Kategoriye Göre Grupla
```

### 3.3 Toolbar İkonları
```xml
<!-- Toolbar Layout -->
<androidx.appcompat.widget.Toolbar>
    <TextView>Buzdolabım</TextView>
    <ImageButton>🔍 Ara</ImageButton>
    <ImageButton>⚙️ Filtrele</ImageButton>
    <ImageButton>⬇️ Sırala</ImageButton>
</androidx.appcompat.widget.Toolbar>
```

---

## 📏 FAZ 4: Akıllı Birim Sistemi (1 gün)

### 4.1 Birim Enum Sınıfı
```kotlin
enum class IngredientUnit(val displayName: String, val category: UnitCategory) {
    // Ağırlık
    GRAM("gram", UnitCategory.WEIGHT),
    KILOGRAM("kg", UnitCategory.WEIGHT),

    // Hacim
    LITER("litre", UnitCategory.VOLUME),
    MILLILITER("ml", UnitCategory.VOLUME),

    // Adet
    PIECE("adet", UnitCategory.COUNT),
    PACKAGE("paket", UnitCategory.COUNT),

    // Ölçü
    TABLESPOON("yemek kaşığı", UnitCategory.MEASURE),
    TEASPOON("çay kaşığı", UnitCategory.MEASURE),
    CUP("su bardağı", UnitCategory.MEASURE)
}
```

### 4.2 Otomatik Birim Önerisi
```kotlin
fun suggestUnit(ingredientName: String): IngredientUnit {
    return when {
        ingredientName.contains("su", ignoreCase = true) -> LITER
        ingredientName.contains("yağ", ignoreCase = true) -> MILLILITER
        ingredientName in listOf("yumurta", "domates") -> PIECE
        else -> GRAM
    }
}
```

### 4.3 Birim Dönüştürücü
```kotlin
// 1000g → 1kg otomatik dönüşüm
// 1000ml → 1L otomatik dönüşüm
```

---

## 🎴 FAZ 5: Modern Malzeme Kartları (2 gün)

### 5.1 Yeni Card Layout
```xml
<!-- item_ingredient_modern.xml -->
<com.google.android.material.card.MaterialCardView>
    <ConstraintLayout>
        <!-- Sol: Malzeme İkonu/Görseli -->
        <ImageView
            android:id="@+id/ingredientIcon"
            android:layout_width="48dp"
            android:layout_height="48dp"/>

        <!-- Orta: Malzeme Bilgileri -->
        <LinearLayout orientation="vertical">
            <TextView><!-- Malzeme Adı --></TextView>
            <Chip><!-- Kategori Badge --></Chip>
        </LinearLayout>

        <!-- Sağ: Miktar Kontrolleri -->
        <LinearLayout orientation="horizontal">
            <IconButton>-</IconButton>
            <TextView>5 adet</TextView>
            <IconButton>+</IconButton>
        </LinearLayout>
    </ConstraintLayout>
</com.google.android.material.card.MaterialCardView>
```

### 5.2 Kategori Badge'leri
```kotlin
// Renkli kategori chip'leri
val categoryColors = mapOf(
    "Sebze" to "#27AE60",
    "Meyve" to "#E67E22",
    "Et" to "#C0392B",
    "Süt Ürünü" to "#3498DB"
)
```

### 5.3 Swipe-to-Delete
```kotlin
// ItemTouchHelper ile swipe gesture
- Sola kaydır → Sil
- Sağa kaydır → Düzenle (opsiyonel)
```

---

## 📊 FAZ 6: Kategori Bazlı Gruplama (1 gün)

### 6.1 Grouped RecyclerView
```kotlin
// ExpandableRecyclerView kullanımı
Section 1: 🥬 Sebzeler (5 ürün)
  - Domates
  - Biber
  ...

Section 2: 🍗 Et & Tavuk (3 ürün)
  - Tavuk Göğsü
  ...
```

### 6.2 Collapse/Expand Animasyonları
- Smooth rotation animation
- Fade in/out content

---

## ✨ FAZ 7: Animasyonlar & Mikro-İnteraksiyonlar (1 gün)

### 7.1 Enter/Exit Animasyonlar
```kotlin
// Card ekleme animasyonu
- Fade in + Scale up
- Staggered animation (sıralı görünüm)
```

### 7.2 Loading States
```xml
<!-- Shimmer effect skeleton -->
<com.facebook.shimmer.ShimmerFrameLayout>
    <include layout="@layout/skeleton_ingredient_card"/>
</com.facebook.shimmer.ShimmerFrameLayout>
```

### 7.3 Empty State
```xml
<!-- Boş buzdolabı görünümü -->
<LinearLayout gravity="center">
    <ImageView src="@drawable/empty_fridge_illustration"/>
    <TextView>Buzdolabın boş görünüyor!</TextView>
    <Button>İlk Malzemeni Ekle</Button>
</LinearLayout>
```

---

## 🧪 FAZ 8: Testing & Polish (1 gün)

### 8.1 Unit Tests
```kotlin
- FilterManager testi
- SortManager testi
- UnitConverter testi
```

### 8.2 UI Tests
```kotlin
- Arama flow testi
- Filtreleme testi
- Sıralama testi
- CRUD operasyon testleri
```

### 8.3 Performance Optimization
- RecyclerView view holder pattern
- DiffUtil for efficient updates
- Image loading optimization
- Memory leak kontrolü

---

## 📅 Toplam Tahmini Süre: 8-10 İş Günü

### Sprint Planı:
- **Sprint 1 (Gün 1-3):** Design System + Arama
- **Sprint 2 (Gün 4-6):** Filtreleme + Birim Sistemi
- **Sprint 3 (Gün 7-9):** Modern Cards + Animasyonlar
- **Sprint 4 (Gün 10):** Testing + Polish

---

## 🎯 Başarı Kriterleri

✅ Beyaz-krem-mavi renk teması uygulanmış
✅ Online alışveriş deneyimi sağlanmış
✅ Gelişmiş arama çalışıyor (NLP/ML)
✅ Filtreleme sistemi aktif
✅ A-Z sıralama ve diğer seçenekler çalışıyor
✅ Akıllı birim sistemi çalışıyor
✅ Modern card tasarımları uygulanmış
✅ Smooth animasyonlar eklenmiş
✅ Loading ve empty state'ler var
✅ Swipe-to-delete çalışıyor
