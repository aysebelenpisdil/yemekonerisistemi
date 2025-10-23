# Yemek Öneri Sistemi - Proje Anayasası

## Proje Prensipleri

### 1. Temel Amaç
- Kullanıcılara kişiselleştirilmiş yemek önerileri sunmak
- Envanter yönetimi ile entegre çalışan bir sistem oluşturmak
- Kullanıcı dostu ve erişilebilir bir arayüz sağlamak

### 2. Teknoloji Prensipleri
- **Frontend:** React Native (Cross-platform mobil uygulama)
- **Backend:** Node.js + Express + TypeScript
- **Veritabanı:** SQLite (geliştirme) / PostgreSQL (production)
- **API:** RESTful API standartları
- **State Management:** Redux Toolkit veya Context API

### 3. Kod Standartları
- TypeScript kullanımı zorunlu
- ESLint ve Prettier ile kod formatı standardizasyonu
- Functional components ve hooks kullanımı (React Native)
- Modüler ve yeniden kullanılabilir komponent mimarisi
- Comprehensive error handling ve logging

### 4. Güvenlik Prensipleri
- JWT tabanlı authentication
- Input validation ve sanitization
- Secure API endpoints
- Environment variables ile hassas bilgilerin korunması

### 5. Kullanıcı Deneyimi & Design System
**Renk Paleti (Beyaz-Krem-Mavi):**
- Primary: #4A90E2 (Modern Mavi)
- Secondary: #F5F5DC (Krem/Bej)
- Background: #FFFFFF (Beyaz)
- Surface: #F8F9FA (Açık Gri)
- Accent: #5DADE2 (Açık Mavi)
- Text Primary: #2C3E50 (Koyu Gri)
- Text Secondary: #7F8C8D (Orta Gri)

**Tipografi:**
- Heading: Roboto Bold (24sp, 20sp, 18sp)
- Body: Roboto Regular (16sp, 14sp)
- Caption: Roboto Light (12sp)

**Spacing & Grid:**
- Base unit: 8dp
- Card padding: 16dp
- Section margin: 24dp
- Element spacing: 8dp, 16dp, 24dp

**Card Design:**
- Corner radius: 12dp
- Elevation: 2dp (normal), 4dp (pressed)
- Border: 1dp #E0E0E0 (opsiyonel)

**UI/UX Prensipleri:**
- Responsive ve performanslı UI
- Online alışveriş platformu benzeri deneyim
- Smooth animasyonlar (200-300ms)
- Micro-interactions
- Loading states & skeletons
- Empty states ile user guidance
- Intuitive navigation
- Accessibility standartlarına uyum (WCAG 2.1)

### 6. Veri Yönetimi
- Normalized database structure
- Efficient query optimization
- Data caching stratejileri
- Real-time sync capabilities

### 7. Test ve Kalite
- Unit testing coverage (minimum %70)
- Integration testing
- E2E testing kritik user flow'lar için
- Continuous Integration/Deployment pipeline

### 8. Dokümantasyon
- Comprehensive API documentation
- Code comments for complex logic
- README files for each module
- User guide and technical documentation