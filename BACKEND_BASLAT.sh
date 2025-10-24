#!/bin/bash

echo "=========================================="
echo "🚀 Yemek Öneri Sistemi - Backend"
echo "=========================================="
echo ""

# Backend dizinine git
cd "$(dirname "$0")/backend" || exit 1

# Eski process'leri temizle
echo "🧹 Port temizleniyor..."
lsof -ti:8000 | xargs kill -9 2>/dev/null
sleep 1

echo ""
echo "📦 Backend başlatılıyor..."
echo ""
echo "✅ Backend adresleri:"
echo "   💻 Bilgisayardan: http://localhost:8000"
echo "   📱 Emulator'den:  http://10.0.2.2:8000"
echo ""
echo "📖 API Dokümantasyonu: http://localhost:8000/docs"
echo ""
echo "⏹️  Durdurmak için: Ctrl+C"
echo ""
echo "=========================================="
echo ""

# Backend'i 0.0.0.0'da başlat (emulator erişebilsin)
python3 -c "
import uvicorn
from main import app

uvicorn.run(
    app,
    host='0.0.0.0',  # Emulator için önemli!
    port=8000,
    log_level='info'
)
"
