package com.example.recipe_manager.ui.detail

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.ScrollView
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
    private lateinit var cookingCounterTextView: TextView
    private lateinit var incrementButton: TextView
    private lateinit var decrementButton: TextView

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
        cookingCounterTextView = findViewById(R.id.cookingCounterTextView)
        incrementButton = findViewById(R.id.incrementButton)
        decrementButton = findViewById(R.id.decrementButton)

        // Додаємо відступ зверху ПІСЛЯ ініціалізації UI
        addTopPadding()

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

        // Кнопка збільшення лічильника
        incrementButton.setOnClickListener {
            currentRecipe?.let { recipe ->
                val updatedRecipe = recipe.copy(
                    timesCooked = recipe.timesCooked + 1,
                    wasCooked = true
                )
                viewModel.update(updatedRecipe)
                currentRecipe = updatedRecipe
                updateCookingCounter(updatedRecipe)
            }
        }

        // Кнопка зменшення лічильника
        decrementButton.setOnClickListener {
            currentRecipe?.let { recipe ->
                if (recipe.timesCooked > 0) {
                    val newCount = recipe.timesCooked - 1
                    val updatedRecipe = recipe.copy(
                        timesCooked = newCount,
                        wasCooked = newCount > 0
                    )
                    viewModel.update(updatedRecipe)
                    currentRecipe = updatedRecipe
                    updateCookingCounter(updatedRecipe)
                }
            }
        }
    }

    private fun addTopPadding() {
        // Отримуємо висоту ActionBar
        val actionBarHeight = supportActionBar?.height ?: 0

        val actionBarSize = if (actionBarHeight > 0) {
            actionBarHeight
        } else {
            val styledAttributes = theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
            val size = styledAttributes.getDimension(0, 0f).toInt()
            styledAttributes.recycle()
            size
        }

        // Знаходимо ScrollView і додаємо padding
        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        scrollView?.setPadding(
            scrollView.paddingLeft,
            scrollView.paddingTop + actionBarSize,
            scrollView.paddingRight,
            scrollView.paddingBottom
        )
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

        // Відображення лічильника приготувань
        updateCookingCounter(recipe)
    }

    private fun updateCookingCounter(recipe: Recipe) {
        cookingCounterTextView.text = recipe.timesCooked.toString()
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