"""
Ana API Sunucusu
Yemek Öneri Sistemi Backend
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import uvicorn

# API router'ları import et
from api import ingredients

app = FastAPI(
    title="Yemek Öneri Sistemi API",
    description="RAG Tabanlı Kişiselleştirilmiş Yemek Öneri Sistemi",
    version="0.1.0"
)

# CORS ayarları (Android uygulaması için)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Production'da güncellenmeli
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
async def root():
    return {
        "message": "Yemek Öneri Sistemi API",
        "version": "0.1.0",
        "status": "active"
    }

@app.get("/health")
async def health_check():
    return {"status": "healthy"}

# Router'ları ekle
app.include_router(ingredients.router, prefix="/api/ingredients", tags=["ingredients"])

if __name__ == "__main__":
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8000,
        reload=True  # Development mode
    )