// Bu kodu InventoryFragment.kt'nin setupRealTimeSearch() metoduna EKLE
// Detaylı debug için

private fun setupRealTimeSearch() {
    android.util.Log.d("DEBUG", "======================================")
    android.util.Log.d("DEBUG", "setupRealTimeSearch() BAŞLADI")
    android.util.Log.d("DEBUG", "======================================")

    searchEditText.addTextChangedListener { editable ->
        val query = editable.toString().trim()

        android.util.Log.d("DEBUG", "----------------------------")
        android.util.Log.d("DEBUG", "Text değişti: '$query'")
        android.util.Log.d("DEBUG", "Query uzunluğu: ${query.length}")

        // Önceki arama job'ını iptal et
        searchJob?.cancel()
        android.util.Log.d("DEBUG", "Önceki job iptal edildi")

        // Eğer query boşsa önerileri gizle
        if (query.isEmpty()) {
            android.util.Log.d("DEBUG", "Query boş, öneriler gizleniyor")
            hideSuggestions()
            return@addTextChangedListener
        }

        // En az 2 karakter girilmişse ara
        if (query.length >= 2) {
            android.util.Log.d("DEBUG", "✅ Query >= 2 karakter, arama başlıyor...")

            searchJob = lifecycleScope.launch {
                android.util.Log.d("DEBUG", "Coroutine başladı")

                // 300ms bekle (debouncing)
                android.util.Log.d("DEBUG", "300ms bekleniyor (debouncing)...")
                delay(300)
                android.util.Log.d("DEBUG", "300ms tamamlandı")

                try {
                    android.util.Log.d("DEBUG", "🔍 Backend'e istek gönderiliyor...")
                    android.util.Log.d("DEBUG", "   Query: '$query'")
                    android.util.Log.d("DEBUG", "   Limit: 20")
                    android.util.Log.d("DEBUG", "   URL: ${RetrofitClient.BASE_URL}/api/ingredients/?q=$query&limit=20")

                    // Backend'den fuzzy search ile ara (IO thread'de)
                    val response = withContext(Dispatchers.IO) {
                        android.util.Log.d("DEBUG", "IO thread'e geçildi")
                        val result = RetrofitClient.apiService.searchIngredients(query, 20)
                        android.util.Log.d("DEBUG", "API çağrısı tamamlandı")
                        result
                    }

                    android.util.Log.d("DEBUG", "Main thread'e dönüldü")
                    android.util.Log.d("DEBUG", "📡 Response alındı")
                    android.util.Log.d("DEBUG", "   HTTP Code: ${response.code()}")
                    android.util.Log.d("DEBUG", "   Success: ${response.isSuccessful}")
                    android.util.Log.d("DEBUG", "   Body null?: ${response.body() == null}")

                    if (response.isSuccessful && response.body() != null) {
                        val results = response.body()!!.results
                        val ingredientNames = results.map { it.name }

                        android.util.Log.d("DEBUG", "✅ BAŞARILI!")
                        android.util.Log.d("DEBUG", "   Sonuç sayısı: ${ingredientNames.size}")
                        android.util.Log.d("DEBUG", "   İlk 5 sonuç: ${ingredientNames.take(5)}")

                        // UI güncelle
                        if (ingredientNames.isNotEmpty()) {
                            android.util.Log.d("DEBUG", "showSuggestions() çağrılıyor...")
                            showSuggestions(ingredientNames)
                            android.util.Log.d("DEBUG", "showSuggestions() tamamlandı")
                        } else {
                            android.util.Log.d("DEBUG", "Sonuç yok, öneriler gizleniyor")
                            hideSuggestions()
                        }
                    } else {
                        android.util.Log.e("DEBUG", "❌ API HATASI!")
                        android.util.Log.e("DEBUG", "   HTTP Code: ${response.code()}")
                        android.util.Log.e("DEBUG", "   Error Body: ${response.errorBody()?.string()}")
                        hideSuggestions()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("DEBUG", "💥 EXCEPTION!")
                    android.util.Log.e("DEBUG", "   Mesaj: ${e.message}")
                    android.util.Log.e("DEBUG", "   Tip: ${e.javaClass.simpleName}")
                    android.util.Log.e("DEBUG", "   Stack trace:")
                    e.printStackTrace()

                    Toast.makeText(
                        context,
                        "Backend hatası: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    hideSuggestions()
                }
            }
        } else {
            android.util.Log.d("DEBUG", "❌ Query < 2 karakter (${query.length}), arama yapılmıyor")
            hideSuggestions()
        }

        android.util.Log.d("DEBUG", "----------------------------")
    }

    android.util.Log.d("DEBUG", "======================================")
    android.util.Log.d("DEBUG", "setupRealTimeSearch() TAMAMLANDI")
    android.util.Log.d("DEBUG", "TextWatcher listener kuruldu")
    android.util.Log.d("DEBUG", "======================================")
}
