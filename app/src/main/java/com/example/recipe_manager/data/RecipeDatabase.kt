package com.example.recipe_manager.algorithms

import com.example.recipe_manager.model.Recipe

object SortingAlgorithms {

    /**
     * Швидке сортування (QuickSort) - основний алгоритм сортування
     * Складність: середня O(n log n), найгірша O(n²)
     */

    /**
     * Сортування рецептів за назвою (алфавітний порядок)
     */
    fun quickSortByName(recipes: MutableList<Recipe>, low: Int = 0, high: Int = recipes.size - 1) {
        if (low < high) {
            val pivotIndex = partitionByName(recipes, low, high)
            quickSortByName(recipes, low, pivotIndex - 1)
            quickSortByName(recipes, pivotIndex + 1, high)
        }
    }

    private fun partitionByName(recipes: MutableList<Recipe>, low: Int, high: Int): Int {
        val pivot = recipes[high].name.lowercase()
        var i = low - 1

        for (j in low until high) {
            if (recipes[j].name.lowercase() <= pivot) {
                i++
                // Обмін елементів
                val temp = recipes[i]
                recipes[i] = recipes[j]
                recipes[j] = temp
            }
        }

        // Обмін pivot елементу
        val temp = recipes[i + 1]
        recipes[i + 1] = recipes[high]
        recipes[high] = temp

        return i + 1
    }

    /**
     * Сортування рецептів за часом приготування (від меншого до більшого)
     */
    fun quickSortByTime(recipes: MutableList<Recipe>, low: Int = 0, high: Int = recipes.size - 1) {
        if (low < high) {
            val pivotIndex = partitionByTime(recipes, low, high)
            quickSortByTime(recipes, low, pivotIndex - 1)
            quickSortByTime(recipes, pivotIndex + 1, high)
        }
    }

    private fun partitionByTime(recipes: MutableList<Recipe>, low: Int, high: Int): Int {
        val pivot = recipes[high].cookingTime
        var i = low - 1

        for (j in low until high) {
            if (recipes[j].cookingTime <= pivot) {
                i++
                val temp = recipes[i]
                recipes[i] = recipes[j]
                recipes[j] = temp
            }
        }

        val temp = recipes[i + 1]
        recipes[i + 1] = recipes[high]
        recipes[high] = temp

        return i + 1
    }

    /**
     * Сортування рецептів за складністю (Легкий -> Середній -> Складний)
     */
    fun quickSortByDifficulty(recipes: MutableList<Recipe>, low: Int = 0, high: Int = recipes.size - 1) {
        if (low < high) {
            val pivotIndex = partitionByDifficulty(recipes, low, high)
            quickSortByDifficulty(recipes, low, pivotIndex - 1)
            quickSortByDifficulty(recipes, pivotIndex + 1, high)
        }
    }

    private fun partitionByDifficulty(recipes: MutableList<Recipe>, low: Int, high: Int): Int {
        val pivot = getDifficultyLevel(recipes[high].difficulty)
        var i = low - 1

        for (j in low until high) {
            if (getDifficultyLevel(recipes[j].difficulty) <= pivot) {
                i++
                val temp = recipes[i]
                recipes[i] = recipes[j]
                recipes[j] = temp
            }
        }

        val temp = recipes[i + 1]
        recipes[i + 1] = recipes[high]
        recipes[high] = temp

        return i + 1
    }

    /**
     * Конвертує складність у числове значення для порівняння
     */
    private fun getDifficultyLevel(difficulty: String): Int {
        return when (difficulty) {
            Recipe.DIFFICULTY_EASY -> 1
            Recipe.DIFFICULTY_MEDIUM -> 2
            Recipe.DIFFICULTY_HARD -> 3
            else -> 0
        }
    }

    /**
     * Сортування за датою створення (від новіших до старіших)
     */
    fun quickSortByDate(recipes: MutableList<Recipe>, low: Int = 0, high: Int = recipes.size - 1) {
        if (low < high) {
            val pivotIndex = partitionByDate(recipes, low, high)
            quickSortByDate(recipes, low, pivotIndex - 1)
            quickSortByDate(recipes, pivotIndex + 1, high)
        }
    }

    private fun partitionByDate(recipes: MutableList<Recipe>, low: Int, high: Int): Int {
        val pivot = recipes[high].dateCreated
        var i = low - 1

        for (j in low until high) {
            // Сортуємо у зворотному порядку (новіші спочатку)
            if (recipes[j].dateCreated >= pivot) {
                i++
                val temp = recipes[i]
                recipes[i] = recipes[j]
                recipes[j] = temp
            }
        }

        val temp = recipes[i + 1]
        recipes[i + 1] = recipes[high]
        recipes[high] = temp

        return i + 1
    }

    /**
     * Enum для типів сортування
     */
    enum class SortType {
        NAME,
        TIME,
        DIFFICULTY,
        DATE
    }

    /**
     * Універсальна функція сортування
     */
    fun sortRecipes(recipes: MutableList<Recipe>, sortType: SortType) {
        when (sortType) {
            SortType.NAME -> quickSortByName(recipes)
            SortType.TIME -> quickSortByTime(recipes)
            SortType.DIFFICULTY -> quickSortByDifficulty(recipes)
            SortType.DATE -> quickSortByDate(recipes)
        }
    }
}