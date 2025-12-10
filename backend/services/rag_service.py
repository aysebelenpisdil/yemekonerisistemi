"""
RAG (Retrieval-Augmented Generation) Service
Combines semantic search with LLM for intelligent recipe recommendations
"""

import time
from typing import Dict, Any, List, Optional
from .semantic_service import SemanticSearchService
from .llm_service import OllamaLLMService
from models.user_context import UserContext
from utils.allergen_mapping import filter_recipes_by_allergens


class RAGService:
    """Service for RAG-based recipe recommendations"""

    SYSTEM_PROMPT = """Sen bir Türk mutfağı uzmanısın ve yemek danışmanısın.
Kullanıcının sorusuna verilen tariflere dayanarak Türkçe yanıt ver.
Yanıtların kısa, öz ve faydalı olsun.
Eğer uygun tarif bulunamazsa, genel bir öneri sun.
Tariflerin İngilizce isimlerini Türkçe'ye çevirerek kullan."""

    def __init__(self, semantic_service: SemanticSearchService = None, llm_service: OllamaLLMService = None):
        self.semantic_service = semantic_service or SemanticSearchService()
        self.llm_service = llm_service or OllamaLLMService()

    def answer(self, query: str, user_context: Dict[str, Any] = None, limit: int = 5) -> Dict[str, Any]:
        """
        Answer user query using RAG

        Args:
            query: User's question in Turkish
            user_context: Optional user context (available ingredients, preferences)
                          Can be dict or UserContext model
            limit: Number of recipes to retrieve

        Returns:
            Dictionary with answer, sources, and metadata
        """
        start_time = time.time()

        # Convert dict to UserContext if needed
        context_obj = None
        if user_context:
            if isinstance(user_context, UserContext):
                context_obj = user_context
            elif isinstance(user_context, dict):
                context_obj = UserContext(**user_context)

        # Step 1: Retrieve relevant recipes
        print(f"🔍 RAG: Searching for relevant recipes...")
        recipes = self.semantic_service.search_recipes(query, limit=limit * 2)  # Get more for filtering

        if not recipes:
            return {
                "answer": "Üzgünüm, bu sorguyla eşleşen tarif bulamadım. Lütfen farklı kelimelerle tekrar deneyin.",
                "sources": [],
                "confidence": 0.0,
                "latency_ms": round((time.time() - start_time) * 1000, 1)
            }

        # Step 1.5: Apply allergen hard filter
        if context_obj and context_obj.allergens:
            pre_filter_count = len(recipes)
            recipes = filter_recipes_by_allergens(recipes, context_obj.allergens)
            print(f"🛡️ RAG: Allergen filter: {pre_filter_count} -> {len(recipes)} recipes")

        # Apply cooking time and calorie filters
        if context_obj:
            if context_obj.max_cooking_time:
                recipes = [r for r in recipes if r.get('cooking_time', 999) <= context_obj.max_cooking_time]
            if context_obj.max_calories:
                recipes = [r for r in recipes if r.get('calories', 9999) <= context_obj.max_calories]

        # Limit after filtering
        recipes = recipes[:limit]

        if not recipes:
            allergen_msg = ""
            if context_obj and context_obj.allergens:
                allergen_msg = f" (alerjen filtresi: {', '.join(context_obj.allergens)})"
            return {
                "answer": f"Üzgünüm, kriterlerinize uygun tarif bulamadım{allergen_msg}. Lütfen farklı kelimelerle tekrar deneyin.",
                "sources": [],
                "confidence": 0.0,
                "latency_ms": round((time.time() - start_time) * 1000, 1)
            }

        # Step 2: Build context from recipes
        context = self._build_context(recipes, context_obj)

        # Step 3: Generate answer using LLM
        print(f"🤖 RAG: Generating response...")
        prompt = self._build_prompt(query, context, context_obj)

        llm_result = self.llm_service.generate(prompt)

        # Step 4: Calculate confidence
        avg_similarity = sum(r.get('similarity_score', 0) for r in recipes) / len(recipes)
        confidence = min(1.0, avg_similarity * 2)  # Scale to 0-1

        latency_ms = (time.time() - start_time) * 1000

        return {
            "answer": llm_result.get("response", "Yanıt oluşturulamadı."),
            "sources": [
                {
                    "id": r["id"],
                    "title": r["title"],
                    "similarity_score": r.get("similarity_score", 0)
                }
                for r in recipes
            ],
            "confidence": round(confidence, 2),
            "latency_ms": round(latency_ms, 1),
            "llm_latency_ms": llm_result.get("latency_ms", 0)
        }

    def _build_context(self, recipes: List[Dict], user_context: Dict = None) -> str:
        """Build context string from retrieved recipes"""
        context_parts = []

        for i, recipe in enumerate(recipes, 1):
            title = recipe.get('title', 'Bilinmeyen Tarif')
            ingredients = recipe.get('ingredients', [])
            instructions = recipe.get('instructions', '')

            # Truncate for context window
            ing_str = ', '.join(ingredients[:10]) if isinstance(ingredients, list) else str(ingredients)[:200]
            inst_str = str(instructions)[:300] if instructions else ''

            context_parts.append(f"""
Tarif {i}: {title}
Malzemeler: {ing_str}
Talimatlar: {inst_str}
""")

        return '\n'.join(context_parts)

    def _build_prompt(self, query: str, context: str, user_context = None) -> str:
        """
        Build the full prompt for LLM with user context

        Args:
            query: User's question
            context: Recipe context string
            user_context: UserContext object or dict
        """
        prompt = f"""{self.SYSTEM_PROMPT}

İşte bulduğum ilgili tarifler:
{context}

"""
        # Add user context to prompt
        if user_context:
            # Handle both UserContext object and dict
            if isinstance(user_context, UserContext):
                context_prompt = user_context.to_system_prompt()
                if context_prompt:
                    prompt += f"--- Kullanıcı Bağlamı ---\n{context_prompt}\n\n"
            elif isinstance(user_context, dict):
                if user_context.get('available_ingredients'):
                    ings = ', '.join(user_context['available_ingredients'][:10])
                    prompt += f"Kullanıcının mevcut malzemeleri: {ings}\n"

                if user_context.get('diet_types'):
                    prompt += f"Diyet tercihleri: {', '.join(user_context['diet_types'])}\n"

                if user_context.get('allergens'):
                    allergens = ', '.join(user_context['allergens'])
                    prompt += f"⚠️ UYARI: Kullanıcının şu alerjileri var: {allergens}. Bu malzemeleri KESİNLİKLE önerme!\n"

                if user_context.get('cuisines'):
                    prompt += f"Tercih edilen mutfaklar: {', '.join(user_context['cuisines'])}\n"

                if user_context.get('max_cooking_time'):
                    prompt += f"Maksimum pişirme süresi: {user_context['max_cooking_time']} dakika\n"

                if user_context.get('max_calories'):
                    prompt += f"Maksimum kalori: {user_context['max_calories']} kcal\n"

                if user_context.get('dietary_restrictions'):
                    prompt += f"Diyet kısıtlamaları: {', '.join(user_context['dietary_restrictions'])}\n"

        prompt += f"""
Kullanıcının sorusu: {query}

Lütfen yukarıdaki tariflere dayanarak Türkçe bir yanıt ver. Tarif isimlerini Türkçe'ye çevir."""

        return prompt

    def get_recipe_suggestions(self, ingredients: List[str], limit: int = 5) -> Dict[str, Any]:
        """
        Get recipe suggestions based on available ingredients

        Args:
            ingredients: List of available ingredients
            limit: Number of suggestions

        Returns:
            Dictionary with suggestions and metadata
        """
        start_time = time.time()

        # Build search query from ingredients
        query = f"recipes with {', '.join(ingredients[:5])}"

        # Search recipes
        recipes = self.semantic_service.search_recipes(query, limit=limit)

        latency_ms = (time.time() - start_time) * 1000

        return {
            "suggestions": recipes,
            "query_used": query,
            "latency_ms": round(latency_ms, 1)
        }

    def quick_recommend(self, query: str, limit: int = 3) -> Dict[str, Any]:
        """
        Quick recommendation without LLM (just semantic search)

        Args:
            query: Search query
            limit: Number of recommendations

        Returns:
            Dictionary with recommendations
        """
        start_time = time.time()

        recipes = self.semantic_service.search_recipes(query, limit=limit)

        latency_ms = (time.time() - start_time) * 1000

        return {
            "recommendations": recipes,
            "latency_ms": round(latency_ms, 1)
        }
