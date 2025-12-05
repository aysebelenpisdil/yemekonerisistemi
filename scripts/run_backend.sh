#!/bin/bash
#
# Backend Starter Script
# Usage: ./scripts/run_backend.sh
#

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
BACKEND_DIR="$PROJECT_ROOT/backend"

echo "=========================================="
echo "Yemek Oneri Sistemi - Backend"
echo "=========================================="

cd "$BACKEND_DIR" || { echo "Backend dizini bulunamadi: $BACKEND_DIR"; exit 1; }

echo "Port temizleniyor..."
lsof -ti:8000 | xargs kill -9 2>/dev/null
sleep 1

if [ -d "venv" ]; then
    echo "Virtual environment aktif ediliyor..."
    source venv/bin/activate
fi

echo "Paket kontrolu..."
python3 -c "import fastapi, uvicorn" 2>/dev/null || {
    echo "Gerekli paketler bulunamadi!"
    echo "Cozum: pip install fastapi uvicorn"
    exit 1
}

echo ""
echo "Backend adresleri:"
echo "  Bilgisayardan: http://localhost:8000"
echo "  Emulator'den:  http://10.0.2.2:8000"
echo ""
echo "API Dokumantasyonu: http://localhost:8000/docs"
echo ""
echo "Durdurmak icin: Ctrl+C"
echo ""

python3 -c "
import uvicorn
from main import app

uvicorn.run(
    app,
    host='0.0.0.0',
    port=8000,
    log_level='info'
)
"
