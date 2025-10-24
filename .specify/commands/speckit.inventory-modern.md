# Inventory (Buzdolabı) Ekranı — UI Planı (YOS Calm Theme)

## 🎨 FAZ 1: Design System & Renk Teması (0.5 gün)

### 1.1 Renk Paleti Tanımlama
**Dosya:** `app/src/main/res/values/colors_yos.xml`

```xml
<!-- YOS Calm Theme’den örnekler (tam palet dosyada) -->
<color name="yos_brand_500">#5A80EB</color>
<color name="yos_brand_700">#384FB2</color>
<color name="yos_accent_500">#5AC8C1</color>
<color name="yos_background">#FAFBFE</color>
<color name="yos_surface_elevated">#F8F9FC</color>
<color name="yos_text_primary">#1A1E2E</color>
<color name="yos_text_secondary">#4A5568</color>
```

### 1.2 Tipografi Stilleri
**Dosya:** `app/src/main/res/values/styles_yos.xml`

```xml
<!-- Başlıklar (YOS) -->
<style name="TextAppearance.YOS.H1">
    <item name="android:textSize">24sp</item>
    <item name="android:textStyle">bold</item>
    <item name="android:textColor">@color/text_primary</item>
    <item name="fontFamily">@font/roboto_bold</item>
</style>

<!-- Gövde Metinler (YOS) -->
<style name="TextAppearance.YOS.Body">
    <item name="android:textSize">16sp</item>
    <item name="android:textColor">@color/text_primary</item>
    <item name="fontFamily">@font/roboto_regular</item>
</style>

<!-- Açıklamalar (YOS) -->
<style name="TextAppearance.YOS.Caption">
    <item name="android:textSize">12sp</item>
    <item name="android:textColor">@color/text_secondary</item>
    <item name="fontFamily">@font/roboto_light</item>
</style>
```

### 1.3 Card Stilleri
```xml
<style name="Widget.YOS.Card.Recipe" parent="Widget.MaterialComponents.CardView">
    <item name="cardCornerRadius">16dp</item>
    <item name="cardElevation">4dp</item>
    <item name="cardBackgroundColor">@color/yos_surface_elevated</item>
    <item name="contentPadding">0dp</item>
    <item name="android:clickable">true</item>
    <item name="android:focusable">true</item>
</style>
```

---

## 🏷️ FAZ 2: Arama & Filtre UI (1 gün)

UI odaklı; backend değişikliği yapmadan mevcut arama/fuzzy altyapısını kullan.

Arayüz:
- Toolbar: başlık + arama ikonu + filtre ikonu + sıralama ikonu
- Arama kartı: `Widget.YOS.SearchBar`
- Aktif filtreler: `Widget.YOS.Chip.*`

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

## 🔍 FAZ 3: Listeleme ve Kartlar (1 gün)

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

Kart Düzeni:
- `Widget.YOS.Card.Recipe` ile modern kart görünümü
- Sol: görsel/ikon; Orta: ad + kategori chip; Sağ: miktar kontrolleri

### 3.2 Filtre Bottom Sheet
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

## 🎛️ FAZ 4: Sıralama Sistemi (0.5 gün)

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

## 📏 FAZ 5: Akıllı Birim Sistemi (opsiyonel)

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

## 🎴 FAZ 6: Modern Malzeme Kartları (entegrasyon)

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

## 📊 FAZ 7: Kategori Bazlı Gruplama (opsiyonel)

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

## ✨ FAZ 8: Animasyonlar & Mikro-İnteraksiyonlar (opsiyonel)

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

## 🧪 FAZ 9: Testing & Polish (0.5 gün)

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

## 📅 Toplam Tahmini Süre: 3–4 İş Günü (UI odaklı faz)

### Sprint Planı:
- **Sprint 1 (Gün 1-3):** Design System + Arama
- **Sprint 2 (Gün 4-6):** Filtreleme + Birim Sistemi
- **Sprint 3 (Gün 7-9):** Modern Cards + Animasyonlar
- **Sprint 4 (Gün 10):** Testing + Polish

---

## 🎯 Başarı Kriterleri

✅ Beyaz-krem-mavi renk teması uygulanmış
✅ Online alışveriş deneyimi sağlanmış
✅ Arama ve filtreleme UI’si aktif (mevcut backend ile)
✅ A-Z sıralama ve diğer seçenekler çalışıyor
✅ Akıllı birim sistemi çalışıyor
✅ Modern card tasarımları uygulanmış
✅ Smooth animasyonlar eklenmiş
✅ Loading ve empty state'ler var
✅ Swipe-to-delete çalışıyor
