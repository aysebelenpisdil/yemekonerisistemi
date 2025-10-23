"""
Advanced Search Utilities
Trendyol-style fuzzy search with Turkish character support
"""
import re
from typing import List, Tuple
from difflib import SequenceMatcher

class SearchEngine:
    """
    Profesyonel arama motoru
    - Fuzzy search (typo tolerance)
    - Turkish character normalization
    - Partial matching
    - Relevance scoring
    """
    
    # Turkish character mapping
    TURKISH_CHAR_MAP = {
        'ı': 'i', 'İ': 'I',
        'ğ': 'g', 'Ğ': 'G',
        'ü': 'u', 'Ü': 'U',
        'ş': 's', 'Ş': 'S',
        'ö': 'o', 'Ö': 'O',
        'ç': 'c', 'Ç': 'C'
    }
    
    @staticmethod
    def normalize_turkish(text: str) -> str:
        """
        Turkish karakterleri normalize et
        Hem Turkish hem de normalized versiyonları karşılaştırmak için
        """
        if not text:
            return ""
        
        # Önce lowercase yap
        text = text.lower()
        
        # Turkish karakterleri değiştir
        normalized = text
        for turkish_char, latin_char in SearchEngine.TURKISH_CHAR_MAP.items():
            normalized = normalized.replace(turkish_char.lower(), latin_char.lower())
        
        return normalized
    
    @staticmethod
    def similarity_score(query: str, target: str) -> float:
        """
        İki string arasındaki benzerlik skoru (0.0 - 1.0)
        SequenceMatcher kullanarak
        """
        return SequenceMatcher(None, query.lower(), target.lower()).ratio()
    
    @staticmethod
    def levenshtein_distance(s1: str, s2: str) -> int:
        """
        Levenshtein distance (edit distance) hesapla
        Typo tolerance için
        """
        if len(s1) < len(s2):
            return SearchEngine.levenshtein_distance(s2, s1)
        
        if len(s2) == 0:
            return len(s1)
        
        previous_row = range(len(s2) + 1)
        for i, c1 in enumerate(s1):
            current_row = [i + 1]
            for j, c2 in enumerate(s2):
                # j+1 yerine j kullan çünkü previous_row ve current_row 0'dan başlıyor
                insertions = previous_row[j + 1] + 1
                deletions = current_row[j] + 1
                substitutions = previous_row[j] + (c1 != c2)
                current_row.append(min(insertions, deletions, substitutions))
            previous_row = current_row
        
        return previous_row[-1]
    
    @staticmethod
    def calculate_relevance(query: str, item_name: str) -> float:
        """
        Relevance score hesapla (0.0 - 100.0)
        
        Kriterler:
        1. Exact match (100)
        2. Starts with (90-95)
        3. Contains (70-85)
        4. Fuzzy match (50-70)
        5. No match (0)
        """
        if not query or not item_name:
            return 0.0
        
        query_lower = query.lower()
        item_lower = item_name.lower()
        
        # Turkish normalized versions
        query_norm = SearchEngine.normalize_turkish(query)
        item_norm = SearchEngine.normalize_turkish(item_name)
        
        # 1. EXACT MATCH (100 puan)
        if query_lower == item_lower or query_norm == item_norm:
            return 100.0
        
        # 2. STARTS WITH (90-95 puan)
        if item_lower.startswith(query_lower) or item_norm.startswith(query_norm):
            # Tam kelime başlangıcı daha yüksek puan
            return 95.0
        
        # 3. CONTAINS - Partial matching (70-85 puan)
        if query_lower in item_lower or query_norm in item_norm:
            # Kelimenin başında olması daha değerli
            pos = item_lower.find(query_lower)
            if pos == -1:
                pos = item_norm.find(query_norm)
            
            # Başa yakın olması bonus verir
            position_bonus = max(0, 15 - (pos * 0.5))
            return 70.0 + position_bonus
        
        # 4. FUZZY MATCH - Typo tolerance (50-70 puan)
        # Similarity score kullan
        similarity = SearchEngine.similarity_score(query_norm, item_norm)
        
        # Levenshtein distance kontrolü
        max_distance = max(2, len(query) // 3)  # Query uzunluğuna göre tolerance
        distance = SearchEngine.levenshtein_distance(query_norm, item_norm)
        
        if distance <= max_distance and similarity > 0.5:
            return 50.0 + (similarity * 20.0)
        
        # 5. WORD MATCHING - Kelime kelime kontrol
        query_words = set(query_norm.split())
        item_words = set(item_norm.split())
        
        if query_words & item_words:  # Kesişim varsa
            match_ratio = len(query_words & item_words) / len(query_words)
            return 40.0 + (match_ratio * 30.0)
        
        # 6. NO MATCH
        return 0.0
    
    @staticmethod
    def search(query: str, items: List[str], threshold: float = 30.0, limit: int = 50) -> List[Tuple[str, float]]:
        """
        Gelişmiş arama fonksiyonu
        
        Args:
            query: Arama sorgusu
            items: Aranacak item listesi
            threshold: Minimum relevance skoru (0-100)
            limit: Maksimum sonuç sayısı
        
        Returns:
            [(item_name, score), ...] - Score'a göre sıralı
        """
        if not query or not items:
            return []
        
        # Her item için relevance hesapla
        results = []
        for item in items:
            score = SearchEngine.calculate_relevance(query, item)
            if score >= threshold:
                results.append((item, score))
        
        # Score'a göre sırala (descending)
        results.sort(key=lambda x: x[1], reverse=True)
        
        # Limit uygula
        return results[:limit]
