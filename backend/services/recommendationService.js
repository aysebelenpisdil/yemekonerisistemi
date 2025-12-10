const pool = require('../config/database');

class RecommendationService {
    /**
     * Kullanıcının tercihlerini veritabanından çeker
     */
    async getUserPreferences(kullaniciId) {
        const [rows] = await pool.query(
            `SELECT tercih_edilen_mutfak, diyet_tercihi, alerji_bilgisi 
             FROM kullanici 
             WHERE kullanici_id = ?`,
            [kullaniciId]
        );
        return rows[0] || null;
    }

    /**
     * Tüm yemekleri özelliklerle birlikte çeker
     */
    async getAllRecipes() {
        const [rows] = await pool.query(
            `SELECT y.*, k.kategori_adi 
             FROM yemek y 
             LEFT JOIN kategori k ON y.kategori_id = k.kategori_id`
        );
        return rows;
    }

    /**
     * Content-based filtering ile yemek önerisi yapar
     * Tam eşleşme kullanır
     */
    async getRecommendations(kullaniciId, limit = 10) {
        const preferences = await this.getUserPreferences(kullaniciId);
        
        if (!preferences) {
            // Tercih yoksa popüler yemekleri döndür
            return this.getPopularRecipes(limit);
        }

        const recipes = await this.getAllRecipes();
        const scoredRecipes = [];

        for (const recipe of recipes) {
            let score = 0;
            let matchReasons = [];

            // Mutfak tercihi tam eşleşme kontrolü
            if (preferences.tercih_edilen_mutfak && recipe.mutfak_turu) {
                if (recipe.mutfak_turu === preferences.tercih_edilen_mutfak) {
                    score += 30;
                    matchReasons.push('Tercih edilen mutfak türü');
                }
            }

            // Diyet tercihi tam eşleşme kontrolü
            if (preferences.diyet_tercihi && recipe.diyet_turu) {
                if (recipe.diyet_turu === preferences.diyet_tercihi) {
                    score += 40;
                    matchReasons.push('Diyet tercihine uygun');
                }
            }

            // Alerji kontrolü (alerjen içermemeli - tam eşleşme)
            if (preferences.alerji_bilgisi && recipe.alerjenler) {
                const userAllergens = preferences.alerji_bilgisi.toLowerCase().split(',').map(a => a.trim());
                const recipeAllergens = recipe.alerjenler.toLowerCase().split(',').map(a => a.trim());
                
                const hasAllergen = userAllergens.some(allergen => 
                    recipeAllergens.includes(allergen)
                );

                if (hasAllergen) {
                    score = -100; // Alerjen içeriyorsa önerme
                    matchReasons = ['Alerjen içeriyor - önerilmez'];
                } else {
                    score += 20;
                    matchReasons.push('Alerjen içermiyor');
                }
            }

            if (score > 0) {
                scoredRecipes.push({
                    ...recipe,
                    matchScore: score,
                    matchReasons: matchReasons
                });
            }
        }

        // Skora göre sırala ve limitle
        scoredRecipes.sort((a, b) => b.matchScore - a.matchScore);
        return scoredRecipes.slice(0, limit);
    }

    /**
     * Tercih yoksa popüler yemekleri döndürür
     */
    async getPopularRecipes(limit = 10) {
        const [rows] = await pool.query(
            `SELECT y.*, k.kategori_adi, 0 as matchScore, 'Popüler yemek' as matchReasons
             FROM yemek y 
             LEFT JOIN kategori k ON y.kategori_id = k.kategori_id
             ORDER BY y.puan DESC, y.hazirlama_suresi ASC
             LIMIT ?`,
            [limit]
        );
        return rows;
    }
}

module.exports = new RecommendationService();
