package com.example.recipe_manager.ui.detail

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.recipe_manager.R
import com.example.recipe_manager.model.Recipe
import com.example.recipe_manager.ui.edit.EditRecipeActivity
import com.example.recipe_manager.viewmodel.RecipeViewModel
import kotlinx.coroutines.launch

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: RecipeViewModel
    private var currentRecipe: Recipe? = null

    private lateinit var recipeNameTextView: TextView
    private lateinit var cookingTimeTextView: TextView
    private lateinit var difficultyTextView: TextView
    private lateinit var ingredientsTextView: TextView
    private lateinit var instructionsTextView: TextView
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        // Налаштування ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Ініціалізація UI
        recipeNameTextView = findViewById(R.id.recipeNameTextView)
        cookingTimeTextView = findViewById(R.id.cookingTimeTextView)
        difficultyTextView = findViewById(R.id.difficultyTextView)
        ingredientsTextView = findViewById(R.id.ingredientsTextView)
        instructionsTextView = findViewById(R.id.instructionsTextView)
        editButton = findViewById(R.id.editButton)
        deleteButton = findViewById(R.id.deleteButton)

        // Ініціалізація ViewModel
        viewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

        // Отримання ID рецепту
        val recipeId = intent.getLongExtra("RECIPE_ID", -1L)
        if (recipeId == -1L) {
            Toast.makeText(this, "Помилка завантаження рецепту", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Завантаження рецепту
        loadRecipe(recipeId)

        // Кнопка редагування
        editButton.setOnClickListener {
            currentRecipe?.let { recipe ->
                val intent = Intent(this, EditRecipeActivity::class.java)
                intent.putExtra("RECIPE_ID", recipe.id)
                startActivity(intent)
            }
        }

        // Кнопка видалення
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        // Перезавантажуємо рецепт при поверненні (на випадок редагування)
        val recipeId = intent.getLongExtra("RECIPE_ID", -1L)
        if (recipeId != -1L) {
            loadRecipe(recipeId)
        }
    }

    private fun loadRecipe(recipeId: Long) {
        lifecycleScope.launch {
            val recipe = viewModel.getRecipeById(recipeId)
            if (recipe != null) {
                currentRecipe = recipe
                displayRecipe(recipe)
            } else {
                Toast.makeText(this@RecipeDetailActivity, "Рецепт не знайдено", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun displayRecipe(recipe: Recipe) {
        supportActionBar?.title = recipe.name
        recipeNameTextView.text = recipe.name
        cookingTimeTextView.text = "${recipe.cookingTime} хв"
        difficultyTextView.text = recipe.difficulty

        // Форматування інгредієнтів з маркерами
        val ingredientsFormatted = recipe.ingredients.joinToString("\n") { "• $it" }
        ingredientsTextView.text = ingredientsFormatted

        instructionsTextView.text = recipe.instructions
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_recipe_title)
            .setMessage(R.string.delete_recipe_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                deleteRecipe()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun deleteRecipe() {
        currentRecipe?.let { recipe ->
            viewModel.delete(recipe)
            Toast.makeText(this, R.string.recipe_deleted, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}