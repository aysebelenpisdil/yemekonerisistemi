# Yemek Öneri Sistemi — Implementation Guide (Android/Kotlin)

Bu rehber, UI tema entegrasyonu ve ekranların YOS Calm Theme’a geçirilmesi için adım adım uygulanacak işleri içerir.

## 0) Önkoşullar
- Gradle Wrapper: 8.7
- Android Gradle Plugin: 8.5.2
- Emülatör → Backend: `http://10.0.2.2:8000`

## 1) Tema Kaynakları
- Şu dosyaların mevcut olduğunu doğrula (veya oluştur):
  - `app/src/main/res/values/colors_yos.xml`
  - `app/src/main/res/values/styles_yos.xml`
  - `app/src/main/res/values/themes_yos.xml`
- Derleme link hatalarını önlemek için stil ebeveynlerinin geçerli olduğundan emin ol (MaterialComponents).

## 2) Tema Uygulama
- `AndroidManifest.xml` içinde sadece `MainActivity` için `android:theme="@style/Theme.YOS"` tanımlı kalsın.
- Uygulama geneli için `Theme.YemekOneri` korunur; kademeli geçiş için Activity bazında ilerlenir.

## 3) Bileşenleri Stil ile Bağlama
- Home/Inventory/RecipeList layoutlarında:
  - Kartlar: `style="@style/Widget.YOS.Card.Recipe"`
  - Birincil buton: `style="@style/Widget.YOS.Button.Primary"`
  - İkincil buton: `style="@style/Widget.YOS.Button.Secondary"`
  - Chip’ler: `style="@style/Widget.YOS.Chip.*"`
  - Arama kutusu: `style="@style/Widget.YOS.SearchBar"`

## 4) Build ve Mini Smoke
- Derleme: `./gradlew assembleDebug`
- Smoke: Uygulama açılışı, Home → Inventory → RecipeList gezinme; görsel tutarlılığı kontrol.

## 5) Erişilebilirlik ve Durumlar
- Dokunma hedefleri ≥ 48dp, kontrast AA niyeti.
- Yükleme, boş ve hata durumları için ortak desenler (EmptyState, ProgressIndicator).

## 6) Sonraki Adımlar
- Daha fazla fragment’ta `Theme.YOS` yayılımı.
- Alt bileşen varyantları (örn. BottomSheet dialog overlay teması).

Başarı Kriterleri:
- Build PASS; YOS stilleri 3 ana ekranda etkin.