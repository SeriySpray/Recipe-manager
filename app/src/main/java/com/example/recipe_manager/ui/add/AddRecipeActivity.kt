package com.example.recipe_manager.ui.add

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.ScrollView
import androidx.lifecycle.ViewModelProvider
import com.example.recipe_manager.R
import com.example.recipe_manager.model.Recipe
import com.example.recipe_manager.viewmodel.RecipeViewModel
import com.google.android.material.textfield.TextInputEditText

class AddRecipeActivity : AppCompatActivity() {

    private lateinit var viewModel: RecipeViewModel

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
        setContentView(R.layout.activity_add_recipe)

        // Налаштування ActionBar
        supportActionBar?.title = getString(R.string.add_recipe_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Додаємо відступ зверху
        addTopPadding()

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

        // Кнопка збереження
        saveButton.setOnClickListener {
            saveRecipe()
        }

        // Кнопка скасування
        cancelButton.setOnClickListener {
            finish()
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

    private fun saveRecipe() {
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

        // Парсинг інгредієнтів (кожен рядок - окремий інгредієнт)
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

        // Створення рецепту
        val recipe = Recipe(
            name = name,
            ingredients = ingredients,
            difficulty = difficulty,
            cookingTime = cookingTime,
            instructions = instructions
        )

        // Збереження в базу даних
        viewModel.insert(recipe)

        Toast.makeText(this, R.string.recipe_added, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}