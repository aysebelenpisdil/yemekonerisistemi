# 🎯 Yemek Öneri Sistemi - Product Design Roadmap

## Executive Summary
**Vizyon**: Türkiye'nin en kullanıcı dostu yemek öneri uygulaması olmak
**Hedef Kullanıcı**: 25-45 yaş arası, evde yemek yapmayı seven, pratik çözümler arayan kullanıcılar
**Değer Önerisi**: Evdeki malzemelerle 5 dakikada tarif bul, israfı önle

## 📊 Mevcut Durum Analizi

### Kullanıcı Yolculuğu & Pain Points
1. **Onboarding** ❌ (Yok)
   - Kullanıcı uygulamayı açtığında ne yapacağını bilmiyor
   - İlk deneyim rehbersiz

2. **Ana Sayfa** ⚠️ (Temel)
   - Arama deneyimi monoton
   - Görsel çekicilik düşük
   - Call-to-action belirsiz

3. **Buzdolabım** ⚠️ (Fonksiyonel ama sıkıcı)
   - Malzeme ekleme deneyimi karmaşık
   - Görsel geri bildirim yok
   - Batch ekleme yok

4. **Tarif Önerileri** ⚠️ (Temel liste)
   - Kartlar görsel olarak çekici değil
   - Filtreleme yetersiz
   - Kişiselleştirme yok

## 🚀 MVP Design System Implementation

### Phase 1: Foundation (Hafta 1-2)
**Hedef**: Marka kimliği ve temel bileşenler

#### 1.1 Renk Paleti - Brand Identity
```
Primary: #5A80EB (Güven veren mavi)
Secondary: #5AC8C1 (Taze turkuaz)
Success: #5AD2A0 (Yeşil onay)
Warning: #F6C34A (Sarı uyarı)
Error: #FF6B6B (Kırmızı hata)
```

#### 1.2 Tipografi Hiyerarşisi
```
Display: 32px/700 - Hero başlıklar
H1: 24px/700 - Sayfa başlıkları
H2: 20px/600 - Bölüm başlıkları
Body: 16px/500 - Normal metin
Caption: 14px/400 - Yardımcı metin
```

### Phase 2: Core Components (Hafta 3-4)
**Hedef**: Kullanıcı etkileşimini artıracak bileşenler

#### 2.1 Primary CTA Button
- **Görünüm**: Gradient arka plan, pill şekil
- **Micro-interaction**: Tap'te scale(0.95)
- **Metrik**: CTR %15 artış hedefi

#### 2.2 Tarif Kartı 2.0
- **Hero Görsel**: 16:9 aspect ratio
- **Bilgi Hiyerarşisi**:
  - Başlık (H2)
  - Süre + Zorluk badge'leri
  - Malzeme eşleşme yüzdesi
- **Engagement Hook**: "Hızlı Tarif" badge'i

#### 2.3 Malzeme Chip'leri
- **Durum göstergesi**: Selected/Available/Missing
- **Batch işlemler**: Uzun bas > Çoklu seçim
- **Animasyon**: Smooth selection (220ms)

### Phase 3: Key Screens Redesign (Hafta 5-6)
**Hedef**: Conversion artışı

#### 3.1 Ana Sayfa - Hero Section
```
┌─────────────────────────┐
│  Bugün ne pişirsek? 🍳  │ <- Emotional hook
│ ┌─────────────────────┐ │
│ │ 🔍 Malzeme ara...  │ │ <- Large search
│ └─────────────────────┘ │
│ [Hızlı Öneri] [Popüler]│ <- Quick actions
└─────────────────────────┘
```

#### 3.2 Onboarding Flow (3 adım)
1. **Hoş geldin**: Değer önerisi
2. **Tercihler**: Diyet, alerji
3. **İlk malzemeler**: 5 popüler malzeme seç

#### 3.3 Empty States
- **İllüstrasyon**: Friendly chef karakteri
- **Mesaj**: "Henüz malzeme eklemedin 👨‍🍳"
- **CTA**: "İlk Malzemeni Ekle"

## 📈 Success Metrics

### User Engagement
- **DAU/MAU Ratio**: %25 → %40
- **Session Duration**: 3 dk → 5 dk
- **Screens per Session**: 4 → 7

### Conversion Metrics
- **Onboarding Completion**: Hedef %80
- **First Recipe View**: D1 %60
- **First Recipe Save**: D7 %30

### Retention
- **D1 Retention**: %40 → %55
- **D7 Retention**: %20 → %30
- **D30 Retention**: %10 → %15

## 🧪 A/B Test Planı

### Test 1: Search Bar Prominence
- **Control**: Mevcut küçük arama
- **Variant**: Hero search box
- **Metrik**: Search engagement rate

### Test 2: Tarif Kartı Tasarımı
- **Control**: Mevcut liste
- **Variant**: Görsel ağırlıklı kart
- **Metrik**: CTR, Save rate

### Test 3: Onboarding
- **Control**: Direkt ana sayfa
- **Variant**: 3 adımlı onboarding
- **Metrik**: D7 retention

## 🔄 Iterasyon Planı

### Sprint 1 (Hafta 1-2)
- [ ] Renk paleti implementation
- [ ] Tipografi sistemi
- [ ] Primary button component
- [ ] Ana sayfa hero section

### Sprint 2 (Hafta 3-4)
- [ ] Tarif kartı redesign
- [ ] Malzeme chip'leri
- [ ] Empty states
- [ ] Micro-animations

### Sprint 3 (Hafta 5-6)
- [ ] Onboarding flow
- [ ] Search experience
- [ ] Filter/Sort UI
- [ ] Loading states

## 🎨 Design Principles

1. **Sadelik**: Her ekranda tek bir primary action
2. **Rehberlik**: Kullanıcı ne yapacağını bilmeli
3. **Geri Bildirim**: Her aksiyona anında yanıt
4. **Kişiselleştirme**: Kullanıcı tercihlerini hatırla
5. **Delight**: Küçük sürprizler (emoji, animasyon)

## 📱 Component Priority Matrix

| Component | Impact | Effort | Priority |
|-----------|--------|--------|----------|
| Hero Search | High | Low | P0 |
| Tarif Kartı | High | Medium | P0 |
| Onboarding | High | Medium | P1 |
| Chip'ler | Medium | Low | P1 |
| Empty States | Medium | Low | P1 |
| Animations | Low | Medium | P2 |

## 🚦 Risk Mitigation

### Technical Risks
- **Performance**: Lazy loading for images
- **Compatibility**: Min SDK 21 support
- **Network**: Offline state handling

### UX Risks
- **Cognitive Load**: Progressive disclosure
- **Learning Curve**: Tooltips & hints
- **Accessibility**: Contrast ratio ≥ 4.5:1

## 📅 Timeline

```
Hafta 1-2: Foundation
Hafta 3-4: Components
Hafta 5-6: Screens
Hafta 7: Testing
Hafta 8: Rollout
```

## 🎯 OKRs

### Objective: Kullanıcı deneyimini iyileştir

**KR1**: Onboarding completion %80
**KR2**: Recipe view rate %60 (D1)
**KR3**: User satisfaction 4.5/5

### Objective: Engagement artır

**KR1**: DAU/MAU %40
**KR2**: Session duration 5 dk
**KR3**: Feature adoption %50

## 🏁 Next Steps

1. **Immediate** (Bugün)
   - [ ] colors_yos.xml ekle
   - [ ] Ana sayfa search tasarımı

2. **This Week**
   - [ ] Component library oluştur
   - [ ] İlk A/B test setup

3. **This Sprint**
   - [ ] MVP design system
   - [ ] 3 key screen redesign

---

**Product Owner**: Design System Lead
**Stakeholders**: Dev Team, Marketing
**Review Cycle**: Weekly sprint review
**Success Criteria**: %20 retention improvement