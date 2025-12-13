package com.example.recipe_manager.ui.edit

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.recipe_manager.R
import com.example.recipe_manager.model.Recipe
import com.example.recipe_manager.viewmodel.RecipeViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class EditRecipeActivity : AppCompatActivity() {

    private lateinit var viewModel: RecipeViewModel
    private var currentRecipe: Recipe? = null

    private lateinit var nameEditText: TextInputEditText
    private lateinit var ingredientsEditText: TextInputEditText
    private lateinit var timeEditText: TextInputEditText
    private lateinit var instructionsEditText: TextInputEditText
    private lateinit var easyRadioButton: RadioButton
    private lateinit var mediumRadioButton: RadioButton
    private lateinit var hardRadioButton: RadioButton
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)

        // Налаштування ActionBar
        supportActionBar?.title = getString(R.string.edit_recipe_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Ініціалізація UI
        nameEditText = findViewById(R.id.nameEditText)
        ingredientsEditText = findViewById(R.id.ingredientsEditText)
        timeEditText = findViewById(R.id.timeEditText)
        instructionsEditText = findViewById(R.id.instructionsEditText)
        easyRadioButton = findViewById(R.id.easyRadioButton)
        mediumRadioButton = findViewById(R.id.mediumRadioButton)
        hardRadioButton = findViewById(R.id.hardRadioButton)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)

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

        // Кнопка збереження
        saveButton.setOnClickListener {
            updateRecipe()
        }

        // Кнопка скасування
        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun loadRecipe(recipeId: Long) {
        lifecycleScope.launch {
            val recipe = viewModel.getRecipeById(recipeId)
            if (recipe != null) {
                currentRecipe = recipe
                fillFormWithRecipeData(recipe)
            } else {
                Toast.makeText(this@EditRecipeActivity, "Рецепт не знайдено", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun fillFormWithRecipeData(recipe: Recipe) {
        nameEditText.setText(recipe.name)

        // Інгредієнти - кожен з нового рядка
        val ingredientsText = recipe.ingredients.joinToString("\n")
        ingredientsEditText.setText(ingredientsText)

        timeEditText.setText(recipe.cookingTime.toString())
        instructionsEditText.setText(recipe.instructions)

        // Встановлення складності
        when (recipe.difficulty) {
            Recipe.DIFFICULTY_EASY -> easyRadioButton.isChecked = true
            Recipe.DIFFICULTY_MEDIUM -> mediumRadioButton.isChecked = true
            Recipe.DIFFICULTY_HARD -> hardRadioButton.isChecked = true
        }
    }

    private fun updateRecipe() {
        val recipe = currentRecipe ?: return

        val name = nameEditText.text.toString().trim()
        val ingredientsText = ingredientsEditText.text.toString().trim()
        val timeText = timeEditText.text.toString().trim()
        val instructions = instructionsEditText.text.toString().trim()

        // Валідація
        if (name.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_name, Toast.LENGTH_SHORT).show()
            return
        }

        if (ingredientsText.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_ingredients, Toast.LENGTH_SHORT).show()
            return
        }

        if (timeText.isEmpty()) {
            Toast.makeText(this, R.string.error_invalid_time, Toast.LENGTH_SHORT).show()
            return
        }

        if (instructions.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_instructions, Toast.LENGTH_SHORT).show()
            return
        }

        val cookingTime = timeText.toIntOrNull()
        if (cookingTime == null || cookingTime <= 0) {
            Toast.makeText(this, R.string.error_invalid_time, Toast.LENGTH_SHORT).show()
            return
        }

        // Парсинг інгредієнтів
        val ingredients = ingredientsText.split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (ingredients.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_ingredients, Toast.LENGTH_SHORT).show()
            return
        }

        // Визначення складності
        val difficulty = when {
            easyRadioButton.isChecked -> Recipe.DIFFICULTY_EASY
            mediumRadioButton.isChecked -> Recipe.DIFFICULTY_MEDIUM
            hardRadioButton.isChecked -> Recipe.DIFFICULTY_HARD
            else -> Recipe.DIFFICULTY_EASY
        }

        // Оновлення рецепту
        val updatedRecipe = recipe.copy(
            name = name,
            ingredients = ingredients,
            difficulty = difficulty,
            cookingTime = cookingTime,
            instructions = instructions
        )

        viewModel.update(updatedRecipe)

        Toast.makeText(this, R.string.recipe_updated, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}