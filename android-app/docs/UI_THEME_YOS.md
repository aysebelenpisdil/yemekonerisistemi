# YOS Calm Theme – Android entegrasyon rehberi

Bu tema Yemek Öneri Sistemi için sakin/odaklı, özgün bir görsel dil sunar. Hiçbir uygulamanın birebir kopyası değildir; sadece “dinginlik, geniş boşluk, yumuşak köşeler, hafif gölgeler” gibi niteliklerden ilham alır.

## 1) Design tokens

Kaynak: `android-app/design/tokens/yos_tokens.json`

İçerik: renk (light/dark), tipografi, spacing, radius, elevation, motion ve temel bileşen kuralları.

## 2) Android’e bağlama stratejisi

Derlemeyi riske atmamak için tema çıktısını önce ayrı dosya olarak deneyin. Aşağıdaki XML’leri projeye ekleyip adım adım aktive edebilirsiniz.

> Not: Aşağıdaki dosyaları eklemek uygulamayı hemen etkilemez; sadece referans için. Etkinleştirmek için `AndroidManifest.xml` içinde `application` tema adını `Theme.YOS` yapın veya Activity bazında uygulayın.

### 2.1 colors.xml (örnek)

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Light -->
    <color name="yos_brand_500">#5F83EB</color>
    <color name="yos_brand_700">#394FB1</color>
    <color name="yos_accent_500">#56C7BD</color>

    <color name="yos_surface_base_light">#FFFFFF</color>
    <color name="yos_surface_elev1_light">#F6F7FB</color>
    <color name="yos_surface_elev2_light">#EFF1F8</color>
    <color name="yos_text_primary_light">#111322</color>
    <color name="yos_text_secondary_light">#3E4460</color>

    <!-- Dark -->
    <color name="yos_surface_base_dark">#0F1220</color>
    <color name="yos_surface_elev1_dark">#161A2B</color>
    <color name="yos_surface_elev2_dark">#1B2033</color>
    <color name="yos_text_primary_dark">#FFFFFF</color>
    <color name="yos_text_secondary_dark">#C7CBE2</color>

    <!-- State -->
    <color name="yos_success">#4FCB8D</color>
    <color name="yos_warning">#F4C34C</color>
    <color name="yos_error">#FF6B6B</color>
</resources>
```

### 2.2 themes.xml (örnek – Material 3 kullanıyorsanız)

> Material 3 (com.google.android.material:material) varsa şu iskeleti kullanın; yoksa ilk aşamada sadece colors.xml ile başlayın.

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.YOS" parent="Theme.Material3.DayNight.NoActionBar">
        <!-- Ana renkler -->
        <item name="colorPrimary">@color/yos_brand_700</item>
        <item name="colorPrimaryContainer">@color/yos_brand_500</item>
        <item name="colorSecondary">@color/yos_accent_500</item>

        <!-- Yüzey & metin -->
        <item name="android:textColorPrimary">@color/yos_text_primary_dark</item>
        <item name="android:textColorSecondary">@color/yos_text_secondary_dark</item>
        <item name="colorSurface">@color/yos_surface_elev1_dark</item>

        <!-- Şekil ve radius -->
        <item name="shapeAppearanceSmallComponent">@style/YOS.Shape.Small</item>
        <item name="shapeAppearanceMediumComponent">@style/YOS.Shape.Medium</item>
        <item name="shapeAppearanceLargeComponent">@style/YOS.Shape.Large</item>
    </style>

    <style name="YOS.Shape.Small" parent="ShapeAppearance.Material3.SmallComponent">
        <item name="cornerFamily">rounded</item>
        <item name="cornerSize">8dp</item>
    </style>
    <style name="YOS.Shape.Medium" parent="ShapeAppearance.Material3.MediumComponent">
        <item name="cornerFamily">rounded</item>
        <item name="cornerSize">16dp</item>
    </style>
    <style name="YOS.Shape.Large" parent="ShapeAppearance.Material3.LargeComponent">
        <item name="cornerFamily">rounded</item>
        <item name="cornerSize">24dp</item>
    </style>
</resources>
```

### 2.3 Bileşen kuralları (stil isimleri)

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Birincil buton -->
    <style name="Widget.YOS.Button.Primary" parent="Widget.Material3.Button">
        <item name="backgroundTint">@color/yos_brand_700</item>
        <item name="cornerRadius">999dp</item>
        <item name="android:paddingLeft">16dp</item>
        <item name="android:paddingRight">16dp</item>
        <item name="android:textStyle">bold</item>
    </style>

    <!-- Kart -->
    <style name="Widget.YOS.Card" parent="Widget.Material3.CardView.Elevated">
        <item name="cardBackgroundColor">@color/yos_surface_elev1_dark</item>
        <item name="cardUseCompatPadding">true</item>
        <item name="shapeAppearance">@style/YOS.Shape.Large</item>
    </style>

    <!-- Chip -->
    <style name="Widget.YOS.Chip" parent="Widget.Material3.Chip.Assist">
        <item name="chipBackgroundColor">@color/yos_surface_elev2_dark</item>
        <item name="shapeAppearance">@style/YOS.Shape.Medium</item>
    </style>
</resources>
```

## 3) Entegrasyon adımları

1. `colors.xml` içeriğini mevcut `values/colors.xml` dosyanıza ekleyin veya `values/colors_yos.xml` olarak yeni bir dosya oluşturun.
2. Material 3 kullanıyorsanız `values/themes_yos.xml` ve `values/styles_yos.xml` dosyalarını ekleyin ve `AndroidManifest.xml` → `application` → `android:theme="@style/Theme.YOS"` yapın (veya Activity bazında).
3. Bileşenleri kullanırken buton için `style="@style/Widget.YOS.Button.Primary"`, kart için `style="@style/Widget.YOS.Card"` atayın.

> Risk azaltma: İlk aşamada sadece renkleri ekleyip tek bir ekranda tema/komponent stilini dener, sorun yoksa tüm uygulamaya yayarsınız.

## 4) Erişilebilirlik ve performans ölçütleri

- Kontrast: Normal metin için ≥ 4.5:1 (AA).
- Dokunma hedefi: 44dp ve üzeri.
- Animasyon: 160–220ms, `cubic-bezier(0.4,0,0.2,1)`.
- Karanlık mod: Dark renkler dosyada mevcut.

## 5) Telif güvenliği

Telifli görsel/ikon, logo veya özgün layout ölçülerini KOPYALAMA. Bu tema özgündür; yalnızca genel niteliklerden ilham alır.

---

Sorunsuz kurulum için istersen değişiklikleri otomatik uygulayacak bir PR şablonu da hazırlayabilirim; haber ver yeter.
