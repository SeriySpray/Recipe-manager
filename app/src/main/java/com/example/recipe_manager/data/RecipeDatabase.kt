package com.example.recipe_manager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import com.example.recipe_manager.model.Converters
import com.example.recipe_manager.model.Recipe
import kotlinx.coroutines.flow.Flow

@Database(entities = [Recipe::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RecipeDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        fun getDatabase(context: Context): RecipeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecipeDatabase::class.java,
                    "recipe_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY dateCreated DESC")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: Long): Recipe?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: Recipe): Long

    @Update
    suspend fun update(recipe: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)

    @Query("DELETE FROM recipes")
    suspend fun deleteAll()

    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :searchQuery || '%'")
    fun searchByName(searchQuery: String): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE difficulty = :difficulty")
    fun filterByDifficulty(difficulty: String): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes ORDER BY cookingTime ASC")
    fun getAllRecipesSortedByTime(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getAllRecipesSortedByName(): Flow<List<Recipe>>
}