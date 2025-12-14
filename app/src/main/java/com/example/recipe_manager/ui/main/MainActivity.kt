package com.example.recipe_manager.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe_manager.R
import com.example.recipe_manager.algorithms.SortingAlgorithms
import com.example.recipe_manager.model.Recipe
import com.example.recipe_manager.ui.add.AddRecipeActivity
import com.example.recipe_manager.ui.detail.RecipeDetailActivity
import com.example.recipe_manager.ui.search.SearchActivity
import com.example.recipe_manager.viewmodel.RecipeViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: RecipeViewModel
    private lateinit var adapter: RecipeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private lateinit var fabAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ініціалізація UI
        recyclerView = findViewById(R.id.recyclerView)
        emptyTextView = findViewById(R.id.emptyTextView)
        fabAdd = findViewById(R.id.fabAdd)

        // Налаштування RecyclerView
        adapter = RecipeAdapter { recipe ->
            openRecipeDetail(recipe)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Ініціалізація ViewModel
        viewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

        // Спостереження за даними
        viewModel.allRecipes.observe(this) { recipes ->
            if (recipes.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyTextView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyTextView.visibility = View.GONE
                adapter.submitList(recipes)
            }
        }

        // Спостереження за відсортованими рецептами
        viewModel.sortedRecipes.observe(this) { recipes ->
            if (recipes.isNotEmpty()) {
                adapter.submitList(recipes)
            }
        }

        // FAB для додавання рецепту
        fabAdd.setOnClickListener {
            val intent = Intent(this, AddRecipeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_add -> {
                val intent = Intent(this, AddRecipeActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.sort_by_name -> {
                sortRecipes(SortingAlgorithms.SortType.NAME)
                true
            }
            R.id.sort_by_time_asc -> {
                sortRecipes(SortingAlgorithms.SortType.TIME_ASC)
                true
            }
            R.id.sort_by_difficulty -> {
                sortRecipes(SortingAlgorithms.SortType.DIFFICULTY)
                true
            }
            R.id.sort_by_date -> {
                sortRecipes(SortingAlgorithms.SortType.DATE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sortRecipes(sortType: SortingAlgorithms.SortType) {
        val currentRecipes = viewModel.allRecipes.value ?: return
        viewModel.sortRecipes(currentRecipes, sortType)
    }

    private fun openRecipeDetail(recipe: Recipe) {
        val intent = Intent(this, RecipeDetailActivity::class.java)
        intent.putExtra("RECIPE_ID", recipe.id)
        startActivity(intent)
    }
}