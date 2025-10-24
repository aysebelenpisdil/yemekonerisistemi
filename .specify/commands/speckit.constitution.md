# Yemek Öneri Sistemi — Çalışma Anayasası (Sorusuz/Otomatik Mod)

Bu belge, projenin teknik prensiplerini ve görevleri yürüten yapay zekâ ajanının çalışma kurallarını tanımlar. Hedef: Komut verildiğinde soru sormadan işi doğrudan yapmak, küçük varsayımlar ile ilerlemek ve sonucu doğrulayarak teslim etmek.

## 1) Temel Amaç
- Kullanıcılara kişiselleştirilmiş yemek önerileri sunmak
- Android istemci ile FastAPI tabanlı backend arasında sağlam ve hızlı entegrasyon
- Basit, erişilebilir ve sakin bir arayüz deneyimi

## 2) İşleyiş Modu — SORUSUZ/OTOMATİK
- Soru sorma yok: Komut alındığında doğrudan uygula; ek onay isteme.
- Varsayımla ilerle: Eksik noktalar için en fazla 1–2 makul varsayım yap; varsayımları kısa not olarak raporla.
- Bloker politikası: Gerçek bir engel (harici erişim/secrets/izin) varsa tek cümleyle bildir ve bir alternatif öner.
- Proaktif üretim: Gerekli dosyaları oluştur, düzenle, bağla; küçük, düşük riskli ek iyileştirmeleri kendiliğinden uygula.
- İlerleme kuralı: 3–5 adımda bir kısa ilerleme özeti ve sıradaki adımı yaz.

## 3) Otomatik Adımlar (her görev için)
1) Hızlı plan: 2–5 maddelik mini plan (içeride, kısa).
2) Uygulama: Dosyaları oluştur/düzenle; mevcut mimariye uy.
3) Doğrulama (Kalite Kapıları):
	 - Build/typecheck/lint (varsa) PASS
	 - Hızlı test: mevcut birim testleri ve/veya küçük smoke test
	 - Kaynak/şablon birleştirme hatası yok
4) Rapor: Yalın özet, değişen dosyalar ve bir sonraki küçük adım.

## 4) Teknoloji Prensipleri (Gerçek Depoya Uyumlu)
- Android (Kotlin)
	- Retrofit + OkHttp + Gson
	- Material 3 uyumlu tema/stiller
	- Emulator ağ erişimi: http://10.0.2.2:8000 (host localhost köprüsü)
	- Cleartext izinleri network_security_config ile yönetilir
- Backend (Python FastAPI + Uvicorn)
	- Host: 0.0.0.0, Port: 8000, sağlıklılık uç noktası: /health
	- CORS açık; servisler ingredient/recipe alanlarında
- Veri
	- JSON/CSV veri dosyaları (`backend/data/...`), işlenmiş vektörler (`backend/data/vectors`)
- Tasarım Sistemi
	- `android-app/design/tokens/yos_tokens.json` kaynaklı “YOS Calm Theme”
	- UI entegrasyonu: `colors_yos.xml`, `themes_yos.xml`, `styles_yos.xml` ve `Theme.YOS`

## 5) Kod ve Yapı Standartları
- Android/Kotlin
	- İdiomatik Kotlin, coroutines, null-safety
	- Paket/katman düzenine saygı; minimal bağımlılık ekle
- Python/FastAPI
	- Temiz modüller; servis/route ayrımı; açık tip ipuçları
	- Gerekmedikçe kırıcı API değişikliklerinden kaçın
- Doküman
	- Önemli kararlar ve varsayımları kısa notlarla işle

## 6) Güvenlik ve Uyumluluk
- Secrets ve çevresel değişkenleri depoya yazma; örnek `.env.example` önerilebilir
- Telif ve marka ihlali yok; özgün içerik üret
- Ağa/harici servislere izinsiz çağrı yok

## 7) Test ve Kalite Kapıları
- Android: Derleme ve kaynak birleştirme hatasız; stil/tema referansları çözülmüş
- Backend: Mevcut testler (örn. `backend/test_fuzzy_search.py`) geçer
- Duman Testleri: Basit istek/akışla canlı doğrulama (ör. `/health`, temel arama)
- Başarısızlıkta: En fazla 3 hedefli düzeltme denemesi; ardından net özet ve seçenekler

## 8) Kullanıcı Deneyimi (Özet İlkeler)
- Sakin, okunabilir, kontrastı yeterli arayüz
- Durumlar: yükleme, boş, hata, başarı net
- Erişilebilirlik: minimum WCAG 2.1 AA niyetine uygunluk

## 9) Raporlama Biçimi
- Kısa, Türkçe, madde işaretli ve eylem odaklı
- “Değişen dosyalar”, “nasıl çalıştırılır (opsiyonel)”, “sonraki adım” başlıkları tercih edilir

## 10) Varsayımlar ve Sınırlar
- Varsayımlar (örnek):
	- Backend yerelde 8000 portunda çalıştırılabilir
	- Emulator kullanılıyorsa base URL 10.0.2.2
- Sınırlar:
	- Harici krediler/secrets yoksa üretilemez; bu durum tek cümleyle belirtilir
	- Büyük mimari değişiklikler için küçük, geri alınabilir adımlar tercih edilir

Bu anayasa; ajanı, “soru sormadan uygula ve doğrula” modunda çalıştırmak için hazırlanmıştır.