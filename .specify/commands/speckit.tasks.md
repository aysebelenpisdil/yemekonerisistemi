# Yemek Öneri Sistemi - Görev Listesi (Kotlin/Android Native)

## Acil Görevler

### Proje Kurulumu
- [x] Android projesi oluştur (Kotlin)
- [x] Temel dizin yapısını kur
- [x] Git repository ayarları
- [ ] README.md güncelle
- [ ] .gitignore güncelle (geçici dosyaları ekle)

### Geliştirme Ortamı
- [x] Android Studio kurulumu
- [x] Kotlin plugin konfigürasyonu
- [ ] Android Emulator setup
- [ ] Gradle build optimizasyonları

### Temel Kütüphaneler
- [x] Material Design 3 components
- [x] Navigation Component kurulumu
- [x] Retrofit (API client) kurulumu
- [ ] Room Database kurulumu (offline cache)
- [ ] Coil/Glide (image loading) kurulumu
- [ ] ViewModel ve LiveData kurulumu

## Backend Hazırlıkları

### API Updates
- [ ] CORS configuration for mobile
- [ ] API versioning implementation
- [ ] Response format standardization
- [ ] Error response structure update

### Authentication
- [ ] JWT implementation review
- [ ] Refresh token endpoint
- [ ] Token expiry handling
- [ ] Secure token storage strategy

## UI Development Görevleri (Android)

### Navigation Setup
- [x] Bottom Navigation View implementation
- [x] Navigation Component kurulumu
- [x] Fragment navigation yapısı
- [ ] Deep linking configuration
- [ ] Safe Args plugin kurulumu

### Authentication Screens (Faz 2)
- [ ] Splash screen design ve implementation
- [ ] Login Activity/Fragment UI
- [ ] Registration flow (3 steps)
- [ ] Forgot password screen
- [ ] Email verification screen
- [ ] SharedPreferences ile token yönetimi

### Core Screens
- [x] Home/Dashboard Fragment (HomeFragment)
- [x] Inventory Fragment (InventoryFragment)
- [x] Recipe Detail Fragment (RecipeDetailFragment)
- [ ] Recipe List Fragment (RecipeListFragment)
- [ ] Meal planner Fragment
- [ ] Shopping list Fragment
- [ ] Profile Fragment
- [ ] Settings Fragment

### Reusable Components & Adapters
- [x] InventoryAdapter (RecyclerView)
- [x] IngredientDetailAdapter
- [x] InstructionAdapter
- [ ] RecipeCardAdapter
- [ ] Loading Progress Dialog
- [ ] Error SnackBar/Toast handler
- [ ] Empty State View
- [ ] Filter Bottom Sheet Dialog

## Feature Implementation (Android)

### Inventory Management
- [x] Inventory CRUD operations (UI)
- [x] Backend'den malzeme arama (AutoCompleteTextView)
- [x] Miktar artırma/azaltma mekanizması
- [ ] Barcode scanner integration (ML Kit)
- [ ] Camera integration (CameraX)
- [ ] Sort and filter (Bottom Sheet)
- [ ] Expiry date tracking
- [ ] Push notifications (Firebase)
- [ ] Room Database ile offline cache

### Recipe Features
- [x] Recipe detail görüntüleme (temel)
- [ ] Recipe listing with pagination
- [ ] Recipe search (backend entegrasyonu)
- [ ] Filter by ingredients
- [ ] Filter by cuisine type
- [ ] Recipe favoriting (Room Database)
- [ ] Recipe rating system
- [ ] Recipe comments
- [ ] Recipe image loading (Coil/Glide)

### Recommendation Engine (RAG - Faz 3)
- [x] Backend API integration yapısı (Retrofit)
- [ ] RAG açıklaması görüntüleme
- [ ] Filter preferences UI
- [ ] Recommendation cards design
- [ ] "Pişirmeye Başla" functionality
- [ ] Missing ingredients display
- [ ] Shopping list'e ekleme

### Meal Planning (Faz 3)
- [ ] Calendar view implementation
- [ ] Meal assignment UI
- [ ] Weekly meal plan generation
- [ ] Shopping list generation from meal plan
- [ ] Meal plan persistence

### Nutritional Tracking (Faz 3)
- [x] Nutrition facts display (basic)
- [ ] Daily nutrition summary
- [ ] Progress charts (MPAndroidChart)
- [ ] Goal setting interface
- [ ] Historical data with Room DB
- [ ] Weekly/Monthly reports

## Testing Tasks (Android)

### Unit Tests (JUnit & Mockito)
- [ ] ViewModel unit tests
- [ ] Repository unit tests
- [ ] API service tests (MockWebServer)
- [ ] Utility function tests
- [ ] Data model tests

### Instrumentation Tests (Espresso)
- [ ] Fragment UI tests
- [ ] Navigation tests
- [ ] RecyclerView adapter tests
- [ ] Database (Room) tests

### Integration Tests
- [ ] Authentication flow tests
- [ ] Recipe browsing tests
- [ ] Inventory management tests
- [ ] API integration tests

### E2E Tests
- [ ] UI Automator setup
- [ ] User onboarding flow
- [ ] Complete recipe recommendation flow
- [ ] Inventory to recipe flow

## Deployment Preparation (Android)

### Assets
- [ ] App icon design (multiple densities)
- [ ] Splash screen design
- [ ] Adaptive icon configuration
- [ ] Play Store screenshots (Türkçe)
- [ ] Feature graphic (1024x500)
- [ ] Promo video (opsiyonel)

### Store Setup
- [ ] Google Play Console hesap kurulumu
- [ ] App listing (Türkçe & English)
- [ ] Store description optimization
- [ ] Privacy policy (web hosting)
- [ ] Terms of service

### Build & Release
- [ ] ProGuard/R8 konfigürasyonu
- [ ] Release build configuration
- [ ] App signing setup (keystore)
- [ ] Version management (build.gradle)
- [ ] AAB (Android App Bundle) generation
- [ ] Internal testing track (Play Console)
- [ ] Beta testing track
- [ ] CI/CD pipeline (GitHub Actions)

## Öncelik Sıralaması (Android - Kotlin)

### ✅ Tamamlanan (FAZ 1)
1. ✅ Android proje kurulumu (Kotlin)
2. ✅ Navigation Component structure (Bottom Nav + Fragments)
3. ✅ Home/Dashboard Fragment
4. ✅ Inventory Fragment (backend entegrasyonu)
5. ✅ Recipe Detail Fragment
6. ✅ Retrofit API client
7. ✅ Material Design 3 tema

### 🔥 Yüksek Öncelik (Bu Hafta - FAZ 2)
1. [ ] Recipe List Fragment implementation
2. [ ] Recipe search backend entegrasyonu
3. [ ] Recipe filtering (malzeme, kalori, süre)
4. [ ] Image loading (Coil) kurulumu
5. [ ] Room Database kurulumu (offline cache & favorites)
6. [ ] Error handling ve loading states
7. [ ] .gitignore güncelleme (geçici dosyalar)

### ⚙️ Orta Öncelik (Sonraki 2 Hafta - FAZ 2-3)
1. [ ] Authentication (Login/Register)
2. [ ] Filter Bottom Sheet Dialog
3. [ ] Shopping list functionality
4. [ ] RAG recommendation engine entegrasyonu
5. [ ] Recipe favoriting (Room)
6. [ ] Basic unit tests (ViewModel, Repository)
7. [ ] ProGuard/R8 konfigürasyonu

### 📅 Düşük Öncelik (Sonraki Ay - FAZ 3)
1. [ ] Meal planning feature
2. [ ] Barcode scanner (ML Kit)
3. [ ] Camera integration (CameraX)
4. [ ] Push notifications (Firebase)
5. [ ] Advanced nutrition tracking
6. [ ] Social features (recipe sharing)
7. [ ] Offline-first architecture
8. [ ] Performance optimizations
9. [ ] Play Store deployment