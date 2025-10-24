#!/bin/bash

# Yemek Öneri Sistemi - Backend Başlatma Script
# Bu script backend server'ı başlatır ve sağlık kontrolü yapar

echo "=========================================="
echo "🚀 Backend Server Başlatılıyor..."
echo "=========================================="

# Backend dizinine git
cd "$(dirname "$0")/backend" || exit 1

# Virtual environment varsa aktif et
if [ -d "venv" ]; then
    echo "📦 Virtual environment aktif ediliyor..."
    source venv/bin/activate
fi

# Gerekli paketlerin kurulu olup olmadığını kontrol et
echo "📋 Paket kontrolü..."
python3 -c "import fastapi, uvicorn" 2>/dev/null || {
    echo "❌ Gerekli paketler bulunamadı!"
    echo "💡 Şu komutu çalıştırın: pip install fastapi uvicorn"
    exit 1
}

# Backend'i başlat
echo ""
echo "✅ Backend başlatılıyor: http://0.0.0.0:8000"
echo "📱 Android Emulator için: http://10.0.2.2:8000"
echo "📱 Fiziksel cihaz için yerel IP adresinizi kullanın"
echo ""
echo "📖 API Dokümantasyonu: http://localhost:8000/docs"
echo ""
echo "⏹️  Durdurmak için: Ctrl+C"
echo ""

# Server'ı başlat
python3 main.py
