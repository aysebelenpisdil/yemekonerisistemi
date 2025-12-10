"""
Ollama LLM Service
Lokal LLM ile tarif açıklamaları üretir
"""

import time
from typing import Optional, Dict, Any, List
import logging

logger = logging.getLogger(__name__)

# Ollama import (opsiyonel)
try:
    import ollama
    OLLAMA_AVAILABLE = True
except ImportError:
    OLLAMA_AVAILABLE = False
    logger.warning("ollama paketi bulunamadı. LLM özellikleri devre dışı.")


class OllamaLLMService:
    """Ollama ile lokal LLM servisi"""
    
    def __init__(self, model: str = "llama3:8b"):
        self.model = model
        self._available = None
    
    def is_available(self) -> bool:
        """Ollama servisi çalışıyor mu kontrol et"""
        if self._available is not None:
            return self._available
        
        if not OLLAMA_AVAILABLE:
            self._available = False
            return False
        
        try:
            # Ollama'ya basit bir istek at
            ollama.list()
            self._available = True
            logger.info(f"✅ Ollama kullanılabilir, model: {self.model}")
        except Exception as e:
            self._available = False
            logger.warning(f"⚠️ Ollama kullanılamıyor: {e}")
        
        return self._available
    
    def generate(
        self,
        prompt: str,
        system_prompt: Optional[str] = None,
        max_tokens: int = 500,
        temperature: float = 0.7
    ) -> Dict[str, Any]:
        """
        LLM ile metin üret
        
        Args:
            prompt: Kullanıcı sorusu/promptu
            system_prompt: Sistem talimatları
            max_tokens: Maksimum token sayısı
            temperature: Yaratıcılık seviyesi (0-1)
        
        Returns:
            {"text": str, "latency_ms": float, "success": bool}
        """
        start_time = time.time()
        
        if not self.is_available():
            return {
                "text": self._fallback_response(prompt),
                "latency_ms": 0,
                "success": False,
                "error": "Ollama not available"
            }
        
        try:
            messages = []
            
            if system_prompt:
                messages.append({"role": "system", "content": system_prompt})
            
            messages.append({"role": "user", "content": prompt})
            
            response = ollama.chat(
                model=self.model,
                messages=messages,
                options={
                    "num_predict": max_tokens,
                    "temperature": temperature
                }
            )
            
            latency_ms = (time.time() - start_time) * 1000
            
            return {
                "text": response["message"]["content"],
                "latency_ms": latency_ms,
                "success": True
            }
            
        except Exception as e:
            logger.error(f"Ollama hatası: {e}")
            return {
                "text": self._fallback_response(prompt),
                "latency_ms": (time.time() - start_time) * 1000,
                "success": False,
                "error": str(e)
            }
    
    def _fallback_response(self, prompt: str) -> str:
        """Ollama çalışmıyorsa basit fallback yanıt"""
        return "Bu tarifler envanterinizdeki malzemelerle uyumlu görünüyor."
    
    def explain_recipes(
        self,
        query: str,
        recipes: List[Dict[str, Any]],
        user_context: Optional[Dict[str, Any]] = None
    ) -> str:
        """
        Tarif önerileri için açıklama üret
        
        Args:
            query: Kullanıcı sorgusu
            recipes: Önerilen tarifler
            user_context: Kullanıcı tercihleri
        
        Returns:
            Türkçe açıklama metni
        """
        if not recipes:
            return "Maalesef uygun tarif bulunamadı."
        
        # Prompt oluştur
        recipe_list = "\n".join([
            f"- {r.get('title', 'Bilinmeyen tarif')}"
            for r in recipes[:5]
        ])
        
        system_prompt = """Sen bir Türk mutfak asistanısın. 
Kullanıcının sorduğu soruya göre önerilen tarifleri açıkla.
Kısa, samimi ve yardımcı ol. Türkçe yaz."""
        
        user_prompt = f"""Kullanıcı: "{query}"

Önerilen tarifler:
{recipe_list}

Bu tarifleri neden önerdiğini 2-3 cümle ile açıkla."""
        
        result = self.generate(
            prompt=user_prompt,
            system_prompt=system_prompt,
            max_tokens=200,
            temperature=0.7
        )
        
        return result["text"]


# Singleton instance
_llm_service = None

def get_llm_service() -> OllamaLLMService:
    """Singleton LLM service instance"""
    global _llm_service
    if _llm_service is None:
        _llm_service = OllamaLLMService()
    return _llm_service
