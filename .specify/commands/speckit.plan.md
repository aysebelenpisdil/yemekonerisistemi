# Yemek Öneri Sistemi — UI Tema Odaklı Plan (Android/Kotlin)

Bu plan, mevcut Android uygulamasında YOS Calm Theme'ı (colors_yos.xml, styles_yos.xml, themes_yos.xml) uygulayıp ekranları yeni stille uyumlu hale getirmeye odaklanır.

## Faz 1: Tema Kaynaklarının Sağlamlaştırılması (0.5 gün)
- Kaynak dosyaları mevcut mu ve derleniyor mu kontrol: `values/colors_yos.xml`, `values/styles_yos.xml`, `values/themes_yos.xml`.
- Hatalı parent veya eksik base stil var mı? Gider.
- Manifest’te Activity bazında `Theme.YOS` uygulanmış mı? `MainActivity` ile sınırlı tut.

Başarı Kriterleri:
- assembleDebug PASS, kaynak link hatası yok.
- `Theme.YOS` altında temel bileşenler (Button/Card/Chip) doğru görünür.

## Faz 2: Ana Ekranların Stil Uyarlaması (1–1.5 gün)
- HomeFragment: Kartlar → `@style/Widget.YOS.Card.Recipe`, aksiyon butonları → `@style/Widget.YOS.Button.Primary`.
- InventoryFragment: Arama → `@style/Widget.YOS.SearchBar`, filtre/etiketler → `Widget.YOS.Chip.*`.
- RecipeListFragment: Liste kartları → `Widget.YOS.Card.Recipe`, yükleme/boş durumları → `Widget.YOS.EmptyState` ve ProgressIndicator.

Başarı Kriterleri:
- 3 ekranda komponentler yeni stilleri kullanıyor, renk/typografi tutarlı.

## Faz 3: Erişilebilirlik ve Durumlar (0.5 gün)
- Kontrast kontrol (AA niyeti), dokunma hedefleri ≥ 48dp.
- Yükleme, boş ve hata durumları için tutarlı görünümler.

Başarı Kriterleri:
- Her ekranda en az bir boş ve bir yükleme durumu görsel olarak doğrulandı.

## Faz 4: İnce Ayar ve Yayılım (0.5 gün)
- Toolbar/BottomNav renkleri ve seçili-dışı durum renkleri.
- Gölge/elevation, köşe yarıçapları için tutarlılık.
- Diğer fragment’lara kademeli tema yayılımı.

Başarı Kriterleri:
- Uygulama genelinde YOS stili tutarlı ve regresyon yok.

## Kalite Kapıları
- Build: assembleDebug PASS.
- Kaynaklar: AAPT hatası yok, tema/stil referansları çözülüyor.
- Mini smoke: Uygulama açılış, Home/Inventory/RecipeList arasında gezinme.

## Notlar
- Emülatör için backend: `http://10.0.2.2:8000`.
- Varsayılan app teması `Theme.YemekOneri`, `MainActivity` özelinde `Theme.YOS` (kademeli geçiş için).