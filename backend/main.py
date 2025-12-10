"""
Ana API Sunucusu
Yemek Öneri Sistemi Backend
"""

from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from fastapi.responses import JSONResponse
from pathlib import Path
import uvicorn
import logging

# Config import
from config import settings

# API router'ları import et
from api import ingredients, recipes

# Semantic search opsiyonel
try:
    from api import semantic
    SEMANTIC_AVAILABLE = True
except Exception as e:
    print(f"[Warning] Semantic search devre disi: {e}")
    SEMANTIC_AVAILABLE = False

# Logging setup
logging.basicConfig(
    level=getattr(logging, settings.log_level),
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)

app = FastAPI(
    title="Yemek Öneri Sistemi API",
    description="RAG Tabanlı Kişiselleştirilmiş Yemek Öneri Sistemi",
    version="0.1.0",
    docs_url="/docs" if settings.is_development else None,  # Production'da docs kapalı
    redoc_url="/redoc" if settings.is_development else None
)

# CORS ayarları - Config'den al
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Simple Rate Limiting Middleware
request_counts: dict = {}

@app.middleware("http")
async def rate_limit_middleware(request: Request, call_next):
    """Basit rate limiting (production için Redis kullanılmalı)"""
    if settings.is_production:
        client_ip = request.client.host
        import time
        current_minute = int(time.time() / 60)
        key = f"{client_ip}:{current_minute}"
        
        request_counts[key] = request_counts.get(key, 0) + 1
        
        # Eski kayıtları temizle
        old_keys = [k for k in request_counts if not k.endswith(str(current_minute))]
        for k in old_keys:
            del request_counts[k]
        
        if request_counts[key] > settings.rate_limit_per_minute:
            return JSONResponse(
                status_code=429,
                content={"detail": "Too many requests. Please try again later."}
            )
    
    response = await call_next(request)
    return response

# Static files - Yemek gorselleri
IMAGES_DIR = Path(__file__).parent.parent / "data" / "Food Images" / "Food Images"
if IMAGES_DIR.exists():
    app.mount("/static/images", StaticFiles(directory=str(IMAGES_DIR)), name="images")

@app.get("/")
async def root():
    return {
        "message": "Yemek Öneri Sistemi API",
        "version": "0.1.0",
        "status": "active",
        "environment": settings.environment,
        "endpoints": {
            "ingredients": "/api/ingredients",
            "recipes": "/api/recipes",
            "semantic": "/api/semantic" if SEMANTIC_AVAILABLE else "disabled",
            "images": "/static/images",
            "docs": "/docs" if settings.is_development else "disabled"
        }
    }

@app.get("/health")
async def health_check():
    return {
        "status": "healthy",
        "environment": settings.environment,
        "debug": settings.debug
    }

# Router'ları ekle
app.include_router(ingredients.router, prefix="/api/ingredients", tags=["ingredients"])
app.include_router(recipes.router, prefix="/api/recipes", tags=["recipes"])

if SEMANTIC_AVAILABLE:
    app.include_router(semantic.router, prefix="/api/semantic", tags=["semantic"])

if __name__ == "__main__":
    logger.info(f"Starting server in {settings.environment} mode")
    uvicorn.run(
        "main:app",
        host=settings.host,
        port=settings.port,
        reload=settings.is_development
    )