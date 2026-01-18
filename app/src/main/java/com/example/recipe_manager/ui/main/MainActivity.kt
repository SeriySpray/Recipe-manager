package com.example.recipe_manager.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe_manager.R
import com.example.recipe_manager.algorithms.SortingAlgorithms
import com.example.recipe_manager.model.Recipe
import com.example.recipe_manager.model.Folder
import com.example.recipe_manager.ui.add.AddRecipeActivity
import com.example.recipe_manager.ui.detail.RecipeDetailActivity
import com.example.recipe_manager.ui.search.SearchActivity
import com.example.recipe_manager.viewmodel.RecipeViewModel
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: RecipeViewModel
    private lateinit var adapter: FolderRecipeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    
    private var currentFolders = listOf<Folder>()
    private var currentRecipes = listOf<Recipe>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ініціалізація UI
        recyclerView = findViewById(R.id.recyclerView)
        emptyTextView = findViewById(R.id.emptyTextView)
        
        // FAB кнопка для додавання рецепта з вибором папки
        val fabCreateFolder = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabCreateFolder)
        fabCreateFolder.setOnClickListener {
            showFolderPickerDialog()
        }

        // Налаштування RecyclerView
        adapter = FolderRecipeAdapter(
            onRecipeClick = { recipe ->
                openRecipeDetail(recipe)
            },
            onFolderClick = { folder ->
                viewModel.toggleFolderExpanded(folder)
            },
            onFolderRename = { folder ->
                showRenameFolderDialog(folder)
            },
            onFolderDelete = { folder ->
                showDeleteFolderDialog(folder)
            },
            onAddRecipeToFolder = { folder ->
                openAddRecipeForFolder(folder)
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Ініціалізація ViewModel
        viewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

        // Спостереження за папками
        viewModel.allFolders.observe(this) { folders ->
            currentFolders = folders
            updateAdapter()
        }

        // Спостереження за рецептами
        viewModel.allRecipes.observe(this) { recipes ->
            currentRecipes = recipes
            updateAdapter()
        }

        // Спостереження за відсортованими рецептами
        viewModel.sortedRecipes.observe(this) { recipes ->
            if (recipes.isNotEmpty()) {
                currentRecipes = recipes
                updateAdapter()
            }
        }
    }

    private fun updateAdapter() {
        if (currentFolders.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyTextView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyTextView.visibility = View.GONE
            adapter.submitList(currentFolders, currentRecipes)
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
                showCreateFolderDialog()
                true
            }
            R.id.action_sort -> {
                showSortDialog()
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
    
    private fun openAddRecipeForFolder(folder: Folder) {
        val intent = Intent(this, AddRecipeActivity::class.java)
        intent.putExtra("FOLDER_ID", folder.id)
        startActivity(intent)
    }
    
    private fun showFolderPickerDialog() {
        if (currentFolders.isEmpty()) {
            android.widget.Toast.makeText(this, R.string.create_folder_first, android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_folder_picker, null)
        val dialog = android.app.Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.folderListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        val adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            inner class FolderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
                val nameTextView: TextView = view.findViewById(R.id.folderNameTextView)
            }
            
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dialog_folder_item, parent, false)
                return FolderViewHolder(view)
            }
            
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val folder = currentFolders[position]
                (holder as FolderViewHolder).nameTextView.text = folder.name
                holder.itemView.setOnClickListener {
                    openAddRecipeForFolder(folder)
                    dialog.dismiss()
                }
            }
            
            override fun getItemCount() = currentFolders.size
        }
        
        recyclerView.adapter = adapter
        
        dialogView.findViewById<View>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }

    private fun showCreateFolderDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_folder_input, null)
        val dialog = android.app.Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        val titleTextView = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val input = dialogView.findViewById<android.widget.EditText>(R.id.folderNameInput)
        
        titleTextView.text = getString(R.string.create_folder)
        input.hint = getString(R.string.folder_name_hint)
        
        dialogView.findViewById<View>(R.id.okButton).setOnClickListener {
            val folderName = input.text.toString().trim()
            if (folderName.isNotEmpty()) {
                viewModel.insertFolder(Folder(name = folderName))
                android.widget.Toast.makeText(this, R.string.folder_created, android.widget.Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                android.widget.Toast.makeText(this, R.string.folder_name_required, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
        
        dialogView.findViewById<View>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }

    private fun showRenameFolderDialog(folder: Folder) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_folder_rename, null)
        val dialog = android.app.Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        val titleTextView = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val input = dialogView.findViewById<android.widget.EditText>(R.id.folderNameInput)
        
        titleTextView.text = getString(R.string.rename_folder)
        input.setText(folder.name)
        input.hint = getString(R.string.folder_name_hint)
        input.selectAll()
        
        dialogView.findViewById<View>(R.id.okButton).setOnClickListener {
            val folderName = input.text.toString().trim()
            if (folderName.isNotEmpty()) {
                val updatedFolder = folder.copy(name = folderName)
                viewModel.updateFolder(updatedFolder)
                android.widget.Toast.makeText(this, R.string.folder_updated, android.widget.Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                android.widget.Toast.makeText(this, R.string.folder_name_required, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
        
        dialogView.findViewById<View>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }

    private fun showDeleteFolderDialog(folder: Folder) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_delete_small, null)
        val dialog = android.app.Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        val titleTextView = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val messageTextView = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val confirmButton = dialogView.findViewById<android.widget.Button>(R.id.confirmButton)
        
        titleTextView.text = getString(R.string.delete_folder_confirmation, folder.name)
        messageTextView.text = getString(R.string.delete_folder_warning)
        confirmButton.text = getString(R.string.delete_folder)
        
        confirmButton.setOnClickListener {
            // Видалити всі рецепти з цієї папки
            currentRecipes.filter { it.folderId == folder.id }.forEach { recipe ->
                viewModel.delete(recipe)
            }
            
            viewModel.deleteFolder(folder)
            android.widget.Toast.makeText(this, R.string.folder_deleted, android.widget.Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        
        dialogView.findViewById<View>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun showSortDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_sort, null)
        val dialog = android.app.Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        dialogView.findViewById<View>(R.id.sortByName).setOnClickListener {
            sortRecipes(SortingAlgorithms.SortType.NAME)
            dialog.dismiss()
        }
        
        dialogView.findViewById<View>(R.id.sortByTime).setOnClickListener {
            sortRecipes(SortingAlgorithms.SortType.TIME_ASC)
            dialog.dismiss()
        }
        
        dialogView.findViewById<View>(R.id.sortByDifficulty).setOnClickListener {
            sortRecipes(SortingAlgorithms.SortType.DIFFICULTY)
            dialog.dismiss()
        }
        
        dialogView.findViewById<View>(R.id.sortByDate).setOnClickListener {
            sortRecipes(SortingAlgorithms.SortType.DATE)
            dialog.dismiss()
        }
        
        dialogView.findViewById<View>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
}