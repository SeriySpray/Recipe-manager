package com.example.recipe_manager.algorithms

import com.example.recipe_manager.model.Recipe

object SearchAlgorithms {

    /**
     * Лінійний пошук - використовується для пошуку рецептів за інгредієнтами
     * Перевіряє кожен рецепт у списку, чи містить він шуканий інгредієнт
     * Складність: O(n)
     */
    fun linearSearchByIngredients(recipes: List<Recipe>, searchIngredient: String): List<Recipe> {
        val results = mutableListOf<Recipe>()
        val searchLower = searchIngredient.lowercase().trim()

        // Проходимо по кожному рецепту
        for (recipe in recipes) {
            // Перевіряємо кожен інгредієнт у рецепті
            for (ingredient in recipe.ingredients) {
                if (ingredient.lowercase().contains(searchLower)) {
                    results.add(recipe)
                    break // Знайдено збіг, переходимо до наступного рецепту
                }
            }
        }

        return results
    }

    /**
     * Лінійний пошук за складністю
     */
    fun linearSearchByDifficulty(recipes: List<Recipe>, difficulty: String): List<Recipe> {
        val results = mutableListOf<Recipe>()

        for (recipe in recipes) {
            if (recipe.difficulty.equals(difficulty, ignoreCase = true)) {
                results.add(recipe)
            }
        }

        return results
    }

    /**
     * Бінарний пошук - використовується для пошуку рецепту за точною назвою
     * ВАЖЛИВО: Список повинен бути відсортований за назвою!
     * Складність: O(log n)
     */
    fun binarySearchByName(sortedRecipes: List<Recipe>, searchName: String): Recipe? {
        var left = 0
        var right = sortedRecipes.size - 1
        val searchLower = searchName.lowercase().trim()

        while (left <= right) {
            val mid = left + (right - left) / 2
            val midName = sortedRecipes[mid].name.lowercase()

            when {
                midName == searchLower -> {
                    // Знайдено точний збіг
                    return sortedRecipes[mid]
                }
                midName < searchLower -> {
                    // Шукана назва знаходиться справа
                    left = mid + 1
                }
                else -> {
                    // Шукана назва знаходиться зліва
                    right = mid - 1
                }
            }
        }

        // Рецепт не знайдено
        return null
    }

    /**
     * Пошук за частковою назвою (для більш гнучкого пошуку)
     * Використовує лінійний пошук для часткових збігів
     */
    fun searchByPartialName(recipes: List<Recipe>, searchQuery: String): List<Recipe> {
        val results = mutableListOf<Recipe>()
        val searchLower = searchQuery.lowercase().trim()

        for (recipe in recipes) {
            if (recipe.name.lowercase().contains(searchLower)) {
                results.add(recipe)
            }
        }

        return results
    }

    /**
     * Комплексний пошук - шукає за назвою, інгредієнтами та складністю одночасно
     */
    fun complexSearch(
        recipes: List<Recipe>,
        searchQuery: String,
        searchByIngredients: Boolean = true,
        searchByName: Boolean = true,
        searchByDifficulty: Boolean = true
    ): List<Recipe> {
        val results = mutableSetOf<Recipe>() // Set для уникнення дублікатів
        val searchLower = searchQuery.lowercase().trim()

        for (recipe in recipes) {
            var found = false

            // Пошук за назвою
            if (searchByName && recipe.name.lowercase().contains(searchLower)) {
                found = true
            }

            // Пошук за інгредієнтами
            if (searchByIngredients && !found) {
                for (ingredient in recipe.ingredients) {
                    if (ingredient.lowercase().contains(searchLower)) {
                        found = true
                        break
                    }
                }
            }

            // Пошук за складністю
            if (searchByDifficulty && !found) {
                if (recipe.difficulty.lowercase().contains(searchLower)) {
                    found = true
                }
            }

            if (found) {
                results.add(recipe)
            }
        }

        return results.toList()
    }
}