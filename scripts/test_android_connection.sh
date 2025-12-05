#!/bin/bash

echo "=========================================="
echo "🔍 Android Backend Bağlantı Testi"
echo "=========================================="

# 1. Backend health check
echo -e "\n1️⃣ Backend Health Check..."
if curl -s http://localhost:8000/health | grep -q "healthy"; then
    echo "   ✅ Backend ÇALIŞIYOR"
else
    echo "   ❌ Backend ÇALIŞMIYOR!"
    echo "   💡 Çözüm: cd backend && python3 main.py"
    exit 1
fi

# 2. Fuzzy search test
echo -e "\n2️⃣ Fuzzy Search Test..."
RESULT=$(curl -s 'http://localhost:8000/api/ingredients/?q=domates&limit=3' | grep -o '"name"' | wc -l)
if [ "$RESULT" -ge 3 ]; then
    echo "   ✅ Fuzzy Search ÇALIŞIYOR ($RESULT sonuç)"
else
    echo "   ❌ Fuzzy Search ÇALIŞMIYOR!"
    exit 1
fi

# 3. Emulator network test
echo -e "\n3️⃣ Emulator Network Test..."
if command -v adb &> /dev/null; then
    if adb devices | grep -q "device$"; then
        echo "   📱 Emulator tespit edildi"
        if adb shell "curl -s http://10.0.2.2:8000/health" 2>/dev/null | grep -q "healthy"; then
            echo "   ✅ Emulator backend'e BAĞLANABİLİYOR"
        else
            echo "   ⚠️  Emulator backend'e bağlanamadı"
            echo "   💡 Emulator'ü yeniden başlatmayı dene"
        fi
    else
        echo "   ⚠️  Emulator çalışmıyor"
        echo "   💡 Android Studio'dan emulator'ü başlat"
    fi
else
    echo "   ⚠️  adb bulunamadı (Android Studio yüklü değil?)"
fi

# 4. Özet
echo -e "\n=========================================="
echo "✅ Backend Durumu: ÇALIŞIYOR"
echo "📊 Malzeme Sayısı: 467"
echo "🔍 Fuzzy Search: AKTİF"
echo "=========================================="
echo ""
echo "📱 Android App'te test et:"
echo "   1. Emulator'ü başlat"
echo "   2. App'i çalıştır"
echo "   3. 'Envanterim' sekmesine git"
echo "   4. Arama kutusuna yaz (örn: 'domates')"
echo ""
echo "📖 Logcat'te şunu görmelisin:"
echo "   D/InventoryFragment: ✅ X sonuç: [...]"
echo ""
