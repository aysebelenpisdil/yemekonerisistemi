# Yemek Öneri Sistemi - Implementation Guide

## Başlamadan Önce

Bu döküman, Spec Kit tasks'larında belirtilen görevlerin step-by-step implementation rehberidir.

## Step 1: React Native Projesi Kurulumu

```bash
# React Native projesi oluştur
npx react-native init YemekOneriSistemi --template react-native-template-typescript

# Proje dizinine geç
cd YemekOneriSistemi

# iOS dependencies kurulumu (Mac only)
cd ios && pod install && cd ..

# Projeyi test et
npx react-native run-ios # veya
npx react-native run-android
```

## Step 2: Dizin Yapısı

```
src/
├── api/           # API service layers
├── assets/        # Images, fonts, etc.
├── components/    # Reusable components
│   ├── common/
│   ├── forms/
│   └── ui/
├── config/        # App configuration
├── constants/     # App constants
├── hooks/         # Custom hooks
├── navigation/    # Navigation configuration
├── screens/       # Screen components
│   ├── auth/
│   ├── inventory/
│   ├── recipes/
│   ├── meal-planning/
│   └── profile/
├── services/      # Business logic
├── store/         # Redux store
│   ├── slices/
│   └── middleware/
├── styles/        # Global styles
├── types/         # TypeScript types
└── utils/         # Utility functions
```

## Step 3: Temel Paket Kurulumları

```bash
# Navigation
npm install @react-navigation/native @react-navigation/stack @react-navigation/bottom-tabs
npm install react-native-screens react-native-safe-area-context react-native-gesture-handler

# State Management
npm install @reduxjs/toolkit react-redux

# UI Components
npm install react-native-elements react-native-vector-icons react-native-safe-area-context

# Forms
npm install react-hook-form

# API & Networking
npm install axios

# Utilities
npm install date-fns react-native-keychain @react-native-async-storage/async-storage

# Dev Dependencies
npm install --save-dev @types/react-native-vector-icons
```

## Step 4: Navigation Setup

```typescript
// src/navigation/RootNavigator.tsx
import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';

// Navigation yapısı implementation
```

## Step 5: Redux Store Configuration

```typescript
// src/store/index.ts
import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';
import inventoryReducer from './slices/inventorySlice';
import recipeReducer from './slices/recipeSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    inventory: inventoryReducer,
    recipes: recipeReducer,
  },
});
```

## Step 6: API Service Layer

```typescript
// src/api/client.ts
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API_BASE_URL = 'http://localhost:3001/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
});

// Request interceptor for auth
apiClient.interceptors.request.use(async (config) => {
  const token = await AsyncStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

## Step 7: Authentication Implementation

```typescript
// src/screens/auth/LoginScreen.tsx
// Login screen implementation with form validation

// src/store/slices/authSlice.ts
// Authentication state management
```

## Step 8: Core Features

### Inventory Management
```typescript
// src/screens/inventory/InventoryListScreen.tsx
// Inventory listing with CRUD operations

// src/screens/inventory/AddInventoryItem.tsx
// Add new inventory item with barcode scanner
```

### Recipe Features
```typescript
// src/screens/recipes/RecipeListScreen.tsx
// Recipe listing with search and filters

// src/screens/recipes/RecipeDetailScreen.tsx
// Detailed recipe view with ingredients and instructions
```

### Recommendation Engine
```typescript
// src/screens/home/HomeScreen.tsx
// Dashboard with recommendations

// src/services/recommendationService.ts
// Business logic for recommendations
```

## Step 9: Native Features Integration

### Barcode Scanner
```bash
npm install react-native-camera react-native-permissions
# veya
npm install react-native-vision-camera
```

### Push Notifications
```bash
npm install @react-native-firebase/app @react-native-firebase/messaging
```

## Step 10: Testing Setup

```bash
# Jest configuration
npm install --save-dev @testing-library/react-native @testing-library/jest-native

# Detox for E2E
npm install --save-dev detox
```

## Step 11: Build Configuration

### iOS
```bash
# iOS build
cd ios
xcodebuild -workspace YemekOneriSistemi.xcworkspace -scheme YemekOneriSistemi -configuration Release
```

### Android
```bash
# Android build
cd android
./gradlew assembleRelease
```

## Step 12: CI/CD Pipeline

```yaml
# .github/workflows/ci.yml
name: CI
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Setup Node.js
        uses: actions/setup-node@v2
      - run: npm ci
      - run: npm test
```

## Deployment Checklist

- [ ] App icons generated for all sizes
- [ ] Splash screens configured
- [ ] Environment variables secured
- [ ] ProGuard rules configured (Android)
- [ ] Code signing completed (iOS)
- [ ] Beta testing completed
- [ ] Store listings prepared
- [ ] Privacy policy published
- [ ] Terms of service published

## Başarılı Implementation İçin İpuçları

1. **Incremental Development:** Küçük, test edilebilir parçalar halinde geliştirin
2. **Component Reusability:** Ortak componentleri baştan doğru tasarlayın
3. **State Management:** Global state'i minimum tutun
4. **Performance:** React Native performans best practice'lerini takip edin
5. **Testing:** Her feature için test yazın
6. **Documentation:** Kod ve API dökümantasyonunu güncel tutun

## Sonraki Adımlar

1. Bu implementation guide'ı takip ederek projeyi kurun
2. Backend API'yi React Native için optimize edin
3. Core features'ları implement edin
4. Test coverage'ı artırın
5. Beta testing başlatın
6. Store'lara deploy edin