package com.example.recipe_manager.data

import com.example.recipe_manager.model.Recipe
import kotlinx.coroutines.flow.Flow

class RecipeRepository(private val recipeDao: RecipeDao) {

    val allRecipes: Flow<List<Recipe>> = recipeDao.getAllRecipes()

    suspend fun insert(recipe: Recipe): Long {
        return recipeDao.insert(recipe)
    }

    suspend fun update(recipe: Recipe) {
        recipeDao.update(recipe)
    }

    suspend fun delete(recipe: Recipe) {
        recipeDao.delete(recipe)
    }

    suspend fun getRecipeById(id: Long): Recipe? {
        return recipeDao.getRecipeById(id)
    }

    fun searchByName(query: String): Flow<List<Recipe>> {
        return recipeDao.searchByName(query)
    }

    fun filterByDifficulty(difficulty: String): Flow<List<Recipe>> {
        return recipeDao.filterByDifficulty(difficulty)
    }

    fun getAllRecipesSortedByTime(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipesSortedByTime()
    }

    fun getAllRecipesSortedByName(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipesSortedByName()
    }

    suspend fun deleteAll() {
        recipeDao.deleteAll()
    }
}