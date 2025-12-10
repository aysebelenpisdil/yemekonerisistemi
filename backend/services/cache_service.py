"""
Cache Service
DiskCache ile kalıcı önbellekleme
"""

import hashlib
import json
from typing import Any, Optional, Dict
from pathlib import Path
import logging

logger = logging.getLogger(__name__)

# DiskCache import
try:
    import diskcache
    CACHE_AVAILABLE = True
except ImportError:
    CACHE_AVAILABLE = False
    logger.warning("diskcache paketi bulunamadı. Cache devre dışı.")


class CacheService:
    """Disk tabanlı cache servisi"""
    
    def __init__(self, cache_dir: str = None):
        self.cache_dir = cache_dir or str(
            Path(__file__).parent.parent / "data" / "cache"
        )
        self._cache = None
        self._init_cache()
    
    def _init_cache(self):
        """Cache'i başlat"""
        if not CACHE_AVAILABLE:
            logger.warning("Cache devre dışı - diskcache kurulu değil")
            return
        
        try:
            Path(self.cache_dir).mkdir(parents=True, exist_ok=True)
            self._cache = diskcache.Cache(self.cache_dir)
            logger.info(f"✅ Cache başlatıldı: {self.cache_dir}")
        except Exception as e:
            logger.error(f"Cache başlatılamadı: {e}")
    
    def _make_key(self, prefix: str, data: Any) -> str:
        """Unique cache key oluştur"""
        content = json.dumps(data, sort_keys=True, default=str)
        hash_val = hashlib.md5(content.encode()).hexdigest()[:12]
        return f"{prefix}:{hash_val}"
    
    def get(self, key: str) -> Optional[Any]:
        """Cache'den değer al"""
        if not self._cache:
            return None
        
        try:
            return self._cache.get(key)
        except Exception as e:
            logger.error(f"Cache get hatası: {e}")
            return None
    
    def set(self, key: str, value: Any, expire: int = 3600) -> bool:
        """
        Cache'e değer kaydet
        
        Args:
            key: Cache anahtarı
            value: Kaydedilecek değer
            expire: TTL saniye (varsayılan 1 saat)
        """
        if not self._cache:
            return False
        
        try:
            self._cache.set(key, value, expire=expire)
            return True
        except Exception as e:
            logger.error(f"Cache set hatası: {e}")
            return False
    
    def get_or_set(
        self,
        key: str,
        factory_func,
        expire: int = 3600
    ) -> Any:
        """
        Cache'de varsa döndür, yoksa üret ve kaydet
        
        Args:
            key: Cache anahtarı
            factory_func: Değer yoksa çağrılacak fonksiyon
            expire: TTL saniye
        """
        cached = self.get(key)
        if cached is not None:
            logger.debug(f"Cache HIT: {key}")
            return cached
        
        logger.debug(f"Cache MISS: {key}")
        value = factory_func()
        self.set(key, value, expire)
        return value
    
    def delete(self, key: str) -> bool:
        """Cache'den sil"""
        if not self._cache:
            return False
        
        try:
            del self._cache[key]
            return True
        except KeyError:
            return False
        except Exception as e:
            logger.error(f"Cache delete hatası: {e}")
            return False
    
    def clear(self) -> bool:
        """Tüm cache'i temizle"""
        if not self._cache:
            return False
        
        try:
            self._cache.clear()
            logger.info("Cache temizlendi")
            return True
        except Exception as e:
            logger.error(f"Cache clear hatası: {e}")
            return False
    
    def stats(self) -> Dict[str, Any]:
        """Cache istatistikleri"""
        if not self._cache:
            return {"available": False}
        
        try:
            return {
                "available": True,
                "size": len(self._cache),
                "volume": self._cache.volume(),
                "directory": self.cache_dir
            }
        except Exception as e:
            return {"available": False, "error": str(e)}


# Singleton instance
_cache_service = None

def get_cache() -> CacheService:
    """Singleton cache instance"""
    global _cache_service
    if _cache_service is None:
        _cache_service = CacheService()
    return _cache_service
