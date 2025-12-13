package com.example.recipe_manager.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe_manager.R
import com.example.recipe_manager.model.Recipe
import com.example.recipe_manager.ui.detail.RecipeDetailActivity
import com.example.recipe_manager.ui.main.RecipeAdapter
import com.example.recipe_manager.viewmodel.RecipeViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: RecipeViewModel
    private lateinit var adapter: RecipeAdapter

    private lateinit var searchEditText: TextInputEditText
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var noResultsTextView: TextView
    private lateinit var chipSearchAll: Chip
    private lateinit var chipSearchName: Chip
    private lateinit var chipSearchIngredients: Chip

    private var allRecipes: List<Recipe> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Налаштування ActionBar
        supportActionBar?.title = getString(R.string.search_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Ініціалізація UI
        searchEditText = findViewById(R.id.searchEditText)
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView)
        noResultsTextView = findViewById(R.id.noResultsTextView)
        chipSearchAll = findViewById(R.id.chipSearchAll)
        chipSearchName = findViewById(R.id.chipSearchName)
        chipSearchIngredients = findViewById(R.id.chipSearchIngredients)

        // Налаштування RecyclerView
        adapter = RecipeAdapter { recipe ->
            openRecipeDetail(recipe)
        }

        searchResultsRecyclerView.adapter = adapter
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Ініціалізація ViewModel
        viewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

        // Завантаження всіх рецептів
        viewModel.allRecipes.observe(this) { recipes ->
            allRecipes = recipes
        }

        // Спостереження за результатами пошуку
        viewModel.searchResults.observe(this) { results ->
            if (results.isEmpty()) {
                searchResultsRecyclerView.visibility = View.GONE
                noResultsTextView.visibility = View.VISIBLE
            } else {
                searchResultsRecyclerView.visibility = View.VISIBLE
                noResultsTextView.visibility = View.GONE
                adapter.submitList(results)
            }
        }

        // Пошук при натисканні Enter
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        // Пошук при зміні типу пошуку
        chipSearchAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) performSearch()
        }

        chipSearchName.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) performSearch()
        }

        chipSearchIngredients.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) performSearch()
        }
    }

    private fun performSearch() {
        val query = searchEditText.text.toString().trim()

        if (query.isEmpty()) {
            noResultsTextView.visibility = View.VISIBLE
            searchResultsRecyclerView.visibility = View.GONE
            return
        }

        when {
            chipSearchAll.isChecked -> {
                // Комплексний пошук (використовує лінійний пошук)
                viewModel.searchRecipes(allRecipes, query)
            }
            chipSearchName.isChecked -> {
                // Бінарний пошук за точною назвою
                // Якщо користувач вводить точну назву
                if (query.length > 3) {
                    viewModel.searchByNameBinary(allRecipes, query)
                } else {
                    // Для коротких запитів використовуємо частковий пошук
                    viewModel.searchByPartialName(allRecipes, query)
                }
            }
            chipSearchIngredients.isChecked -> {
                // Лінійний пошук за інгредієнтами
                viewModel.searchByIngredients(allRecipes, query)
            }
        }
    }

    private fun openRecipeDetail(recipe: Recipe) {
        val intent = Intent(this, RecipeDetailActivity::class.java)
        intent.putExtra("RECIPE_ID", recipe.id)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}