# Bitirme Projesi Ara Raporu

Proje Başlığı: RAG Tabanlı Kişiselleştirilmiş Yemek Öneri Sistemi  
Öğrenci: Ayşe Belen Pısdıl · Konya Teknik Üniversitesi, Bilgisayar Mühendisliği  
Tarih: 11.11.2025

---

## 1. Yürütücü Özet
Kullanıcının evdeki malzemeleri (envanter) ve beslenme/tercih kısıtlarını kullanarak hızlı, kişiselleştirilmiş ve açıklanabilir yemek tarifleri öneren bir sistem geliştirilmektedir. Mevcut sürümde FastAPI tabanlı backend, Türkçe uyarlanmış fuzzy arama (467 malzeme) ve Android istemci çalışır; temel öneri akışı canlıdır. Bir sonraki fazda Retrieval‑Augmented Generation (RAG) ile “neden bu öneri” açıklamaları ve kişiselleştirme motoru (profil + geri bildirim) devreye alınacaktır.

## 2. Hedefler ve Kapsam
| Boyut | Açıklama |
|-------|----------|
| Ana Hedef | Envanter + alışkanlık + kısıtlar ile açıklanabilir tarif önerileri |
| İşlevsel Kapsam | Malzeme arama, tarif önerisi, açıklama üretimi (hedef), kişiselleştirme |
| Teknik Kapsam | FastAPI backend, Android (Kotlin), embedding & vektör indeks (plan), RAG hattı |
| Performans Hedefleri | Arama p95 < 200 ms; Açıklama p95 < 500 ms (RAG sonrası); Top‑5 isabet ≥ %60 (pilot) |
| Kalite Hedefleri | Latency izleme, hata oranı ≤ %2, güvenli API (JWT + CORS daraltma) |

Detaylandırılacak: Kullanıcı profili veri modeli; açıklama prompt şablonları.

## 3. Kaynak Araştırması (Yöntem ve Bulgular)

### 3.1 Amaç ve Araştırma Soruları
- Q1: Türkçe içeriklerde arama doğruluğunu ve kullanıcı deneyimini hangi yöntemler iyileştirir?
- Q2: Açıklanabilir öneri için RAG bileşenlerinin en pratik kombinasyonu nedir?
- Q3: Kişiselleştirme için minimum veri ile güvenli ve etkili re‑ranking nasıl yapılır?

### 3.2 Arama Stratejisi
- Kaynak türleri: Akademik makaleler, resmi kütüphane dokümantasyonları (sentence‑transformers, FAISS), üretim vaka yazıları.
- Anahtar kelimeler: “Turkish normalization”, “fuzzy matching”, “RAG pipeline”, “vector search FAISS/Qdrant”, “personalization implicit feedback”.
- Zaman aralığı: Son 5 yıl odaklı; gerekli yerlerde klasik referanslar.

### 3.3 Dahil Etme / Dışlama Ölçütleri
- Dahil: Türkçe/metin arama, RAG mimarisi, açıklanabilirlik, düşük gecikme odaklı çözümler, mobil/edge pratikleri.
- Dışlama: Sadece büyük-ölçek kurumsal kapalı çözümler; lisans ve veri kısıtı olan içerikler.

### 3.4 Tematik Bulgular (Özet)
- Fuzzy + Normalizasyon: Türkçe karakter dönüşümü ve typo toleransı, top‑N isabeti anlamlı artırır; kural + benzerlik hibriti stabildir.
- Vector Retrieval: SBERT/bge gibi çok dilli embedding + FAISS başlangıçta yeterli; ölçek gerekirse Qdrant opsiyoneldir.
- Re‑ranking: Kısıt filtreleri (diyet/süre/kalori/alerji) + benzerlik birleştirmesi; ilerleyen aşamada cross‑encoder iyileştirmesi.
- Generation (Açıklama): Kaynak referanslı kısa açıklamalar kullanıcı güvenini artırır; prompt şablonları kritik.
- Personalization: Implicit sinyaller (tıklama, pinleme, tekrar) ile re‑rank erken aşamada anlamlı kazanım sağlar.
- Observability: p95 latency ve hata oranı izlenmezse iyileştirme zorlaşır; basit metrik endpoint faydalı.

### 3.5 Projeye Etkileri ve Uygulamalar
| Öğrenim → Uygulama | Projede Karşılığı | Durum |
|---|---|---|
| TR normalizasyon + fuzzy hibrit | `SearchEngine.normalize_turkish`, Levenshtein + similarity + kural puanı | Canlı |
| Çoklu sinyal relevance | `calculate_relevance` çok‑kriterli skor | Canlı |
| Kurallı filtreleme ile güvenli başlangıç | `recipe_service` süre/kalori/ana malzeme filtreleri | Kısmi |
| RAG (embedding + FAISS + açıklama) | sentence‑transformers + FAISS POC, açıklama prompt taslakları | Plan/POC |
| Implicit feedback ile kişiselleştirme | Profil şeması + event toplama | Plan |
| Tip güvenliği ve açık şema | Pydantic v2 modelleri | Canlı |
| Observability | Prometheus/Sentry + benchmark script | Plan |

### 3.6 Sınırlılıklar ve Gelecek Çalışma
- Model karşılaştırmaları (TR kalite vs latency) tablo olarak tamamlanacak.
- Kullanıcı çalışmaları (Top‑5 isabet, memnuniyet) pilot veri ile ölçülecek.
- Lisanslı tarif kaynaklarının doğrulama ve alıntı politikası netleştirilecek.

Detaylandırılacak: Embedding model karşılaştırma tablosu; re‑ranking ağırlık optimizasyonu; feedback sinyali şeması ve ölçüm planı.

## 4. Mimari ve Yöntem
Mevcut Katmanlar:
- API Katmanı: FastAPI; endpoint’ler `/api/ingredients`, `/api/recipes`.
- Servis Katmanı: IngredientService (yükleme + arama), RecipeService (demo öneriler).
- Arama Motoru: Türkçe normalizasyon + Levenshtein + SequenceMatcher + kural skoru.
- Mobil İstemci: Kotlin + Retrofit + Coroutines; gerçek zamanlı arama.

Hedef Katmanlar (RAG + Kişiselleştirme):
1. Retrieval: Vektör indeks (FAISS ilk POC, ölçek için Qdrant opsiyonu).
2. Re‑ranking: Benzerlik skoru + kısıt filtreleri (alerji/diyet/süre/kalori).
3. Generation: LLM (Türkçe açıklama, referanslı malzeme listesi).
4. Personalization: Profil + implicit sinyaller (tıklama, seçim sıklığı).

Detaylandırılacak: Re‑ranking algoritması ağırlıklandırma formülü; kişiselleştirme feature set.

## 5. Veri ve İşleme
| Aşama | Durum | Açıklama |
|-------|-------|----------|
| Kaynak Toplama | Tamamlandı | CSV temel malzeme besin verisi |
| Dönüşüm | Tamamlandı | `csv_to_json.py` ile JSON üretimi |
| Normalizasyon | Devam | Karakter dönüşümü, ad standardizasyonu |
| Eşanlamlı Sözlük | Plan | Malzeme adı varyantlarının eşleştirilmesi |
| Tarif Veri Seti | Plan | Açık lisanslı kaynakların seçimi |

Detaylandırılacak: Tarif veri şeması (ingredient mapping + nutrition + cuisine tag’leri).

## 6. Mevcut İlerleme Özeti
| Bileşen | Durum | Not |
|---------|-------|-----|
| Backend çekirdek | Çalışıyor | Temel endpoint’ler aktif |
| Fuzzy arama | Optimize | Türkçe karakter + typo toleransı |
| Android entegrasyonu | Çalışıyor | Canlı arama deneyimi |
| Dokümantasyon | İyi | QUICK_START & kullanım rehberleri |
| Altyapı script’leri | Mevcut | Port temizleme & başlatma |
| RAG hattı | Plan | POC tasarım aşamasında |
| Kişiselleştirme | Plan | Profil & implicit sinyaller henüz yok |

## 7. Zaman Çizelgesi (Plan vs Gerçek)
| Dönem | Hedef | Durum |
|-------|-------|-------|
| Eyl–Eki | Gereksinim + Literatür | Tamamlandı |
| Eki–Kas | Veri edinimi + Şema | Tamamlandı |
| Kas–Ara | Normalizasyon + Hazırlık | Devam |
| Ara–Oca | Envanter Modülü (v1) + Vektör POC Hazırlık | Plan |
| Oca–Şub | Baseline önerici + İlk ölçümler | Plan |
| Şub–Mar | RAG POC (Retrieval + açıklama) | Plan |
| Mar–May | Kişiselleştirme + Kullanıcı testleri + Raporlama | Plan |

Detaylandırılacak: Her faz için nicel kabul kriteri (örn. nDCG@5 ≥ hedef).

## 8. Sorunlar ve Çözüm Adımları
| Sorun | Etki | Çözüm | Durum |
|-------|------|-------|-------|
| Emulator erişememesi | Android test blokajı | Host `0.0.0.0` + script | Çözüldü |
| Türkçe arama varyans | Sonuç kalitesi | Skor kuralı ince ayar | İyileştirildi |
| Port çakışmaları | Başlatma hatası | Otomatik kill komutu | Çözüldü |
| Veri eşanlamlı eksikliği | Eşleşme kaybı | Sözlük tasarımı | Plan |

## 9. Risk Matrisi
| Risk | Olasılık | Etki | Mitigasyon | İzleme Metriği |
|------|----------|------|-----------|----------------|
| Veri kalitesi | Orta | Yüksek | Temizleme + sözlük | Hatalı eşleşme oranı |
| Model TR performansı | Orta | Orta | Çok dilli model + fine‑tune | Embedding benzerlik dağılımı |
| Latency artışı | Orta | Orta | Caching + hafif model | p95 latency |
| Güvenlik açıkları | Düşük | Yüksek | JWT, rate limit, CORS | 401/403 log oranı |
| Kullanıcı adoption düşük | Orta | Yüksek | Persona test + feedback | CTR, retention |

Detaylandırılacak: Fine‑tune veri seçimi stratejisi; caching katmanı tasarımı.

## 10. Performans ve Metrikler (Baz + Hedef)
| Metrik | Mevcut Baz | Hedef (Faz) | Not |
|--------|------------|-------------|-----|
| Arama p95 latency | Ölçülecek | <200 ms (Faz 1 sonu) | Benchmark script gerekli |
| Açıklama p95 latency | Yok | <500 ms (RAG POC) | LLM seçimi kritik |
| Top‑5 isabet | Ölçülecek | ≥%60 (Pilot) | Mini kullanıcı testi |
| Hata oranı (HTTP 5xx) | Ölçülecek | ≤%1 | Health + metrics endpoint |
| Android ağ hata oranı | Ölçülecek | ≤%2 | Logcat + instrumentation |

Detaylandırılacak: Ölçüm metodolojisi dokümanı; test veri seti seçimi.

## 11. Sonraki Faz İş Paketleri (Detaylı Adım Listesi)
1. RAG POC: Küçük tarif korpusu (1k–5k), embedding üretimi, FAISS indeksleme.  
2. Re‑ranking Denemeleri: Skor bileşenleri (benzerlik + kural) ağırlık validasyonu.  
3. Açıklama Promptları: Malzeme + kullanıcı kısıtı + gerekçe şablonları.  
4. Kişiselleştirme Başlangıcı: Profil şeması + implicit event toplama (tıklama, görüntüleme).  
5. Metrik Pipeline: Benchmark script, metrik endpoint (Prometheus format).  

Detaylandırılacak: Prompt sürümleme stratejisi; feedback etiketleme şeması.

## 12. Kaynak ve Gereksinimler
| Kaynak | İhtiyaç | Gerekçe |
|--------|--------|---------|
| Zaman | Haftalık 6–10 saat | Faz 2–3 geliştirme yükü |
| Donanım | CPU + opsiyonel GPU | Embed üretimi hızlandırma |
| Danışmanlık | Model & mimari review | RAG entegrasyon doğrulama |
| Veri | Açık lisanslı tarif kaynakları | Korpus yeterliliği |

Detaylandırılacak: Tarif veri kaynak listesi + lisans doğrulama matrisi.

## 13. Sonuç ve Değer Özetlemesi
Temel mimari istikrarlı çalışıyor; Türkçe arama kalitesi tatmin edici başlangıç seviyesi sunuyor. RAG ve kişiselleştirme fazları devreye alındığında açıklanabilirlik, isabet ve kullanıcı memnuniyetinde anlamlı artış beklenmektedir. Riskler tanımlandı, mitigasyon akışları ve ölçümler planlandı.

## 14. Açık Kalemler (Detaylandırılacak Liste)
- Kullanıcı profil veri modeli (attribute listesi, veri gizliliği seviyesi)
- Embedding model seçimi karşılaştırma tablosu (latency, boyut, TR kalite)
- Re‑ranking ağırlık optimizasyon prosedürü
- Açıklama prompt sürümleri ve kalite metrikleri (BLEU / ROUGE opsiyonel)
- Eşanlamlı malzeme sözlüğü ve güncelleme governance süreci
- Metrik toplama otomasyonu (CI entegrasyonu)

## 15. Referanslar (Ön Liste)
- Öneri Sistemleri: (Detaylandırılacak tam kaynakça)
- Sentence-Transformers & FAISS resmi dokümantasyonu
- FastAPI Resmi Dokümantasyonu
- Çok dilli embedding benchmark raporları

## 16. Ekler (Hazırlanacak)
- Mimari diyagram (hedef RAG pipeline)  
- API JSON örnekleri (ingredients search, recipe recommend)  
- Demo ekran görüntüleri (Android arama)  
- Benchmark script taslağı  

---

Not: “Detaylandırılacak” etiketli her madde, final raporda genişletilecek öncelikli geliştirme veya dokümantasyon kalemidir.
