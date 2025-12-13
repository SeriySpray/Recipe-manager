📁 Структура файлів та папок
RecipeManager/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/recipemanager/
│   │   │   │   │
│   │   │   │   ├── model/
│   │   │   │   │   └── Recipe.kt              // Модель рецепту
│   │   │   │   │
│   │   │   │   ├── data/
│   │   │   │   │   ├── RecipeRepository.kt    // Репозиторій для роботи з даними
│   │   │   │   │   └── RecipeDatabase.kt      // Room Database
│   │   │   │   │
│   │   │   │   ├── algorithms/
│   │   │   │   │   ├── SearchAlgorithms.kt    // Лінійний і бінарний пошук
│   │   │   │   │   └── SortingAlgorithms.kt   // Швидке сортування
│   │   │   │   │
│   │   │   │   ├── ui/
│   │   │   │   │   ├── main/
│   │   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   │   └── RecipeAdapter.kt   // Adapter для RecyclerView
│   │   │   │   │   │
│   │   │   │   │   ├── add/
│   │   │   │   │   │   └── AddRecipeActivity.kt
│   │   │   │   │   │
│   │   │   │   │   ├── edit/
│   │   │   │   │   │   └── EditRecipeActivity.kt
│   │   │   │   │   │
│   │   │   │   │   ├── search/
│   │   │   │   │   │   └── SearchActivity.kt
│   │   │   │   │   │
│   │   │   │   │   └── detail/
│   │   │   │   │       └── RecipeDetailActivity.kt
│   │   │   │   │
│   │   │   │   └── viewmodel/
│   │   │   │       └── RecipeViewModel.kt     // ViewModel для MVVM
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_add_recipe.xml
│   │   │   │   │   ├── activity_edit_recipe.xml
│   │   │   │   │   ├── activity_search.xml
│   │   │   │   │   ├── activity_recipe_detail.xml
│   │   │   │   │   └── item_recipe.xml        // Layout для одного рецепту
│   │   │   │   │
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── ic_add.xml
│   │   │   │   │   ├── ic_search.xml
│   │   │   │   │   ├── ic_edit.xml
│   │   │   │   │   └── ic_delete.xml
│   │   │   │   │
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   │
│   │   │   │   └── menu/
│   │   │   │       └── main_menu.xml
│   │   │   │
│   │   │   └── AndroidManifest.xml
│   │   │
│   │   └── build.gradle (Module: app)
│   │
│   └── build.gradle (Project)
│
└── gradle.properties
🎯 Призначення алгоритмів у програмі
1. Лінійний пошук (SearchAlgorithms.kt)

Використання: Пошук рецептів за інгредієнтами
Причина: Потрібно перевіряти часткові збіги (рецепт може містити кілька інгредієнтів)

2. Бінарний пошук (SearchAlgorithms.kt)

Використання: Пошук рецепту за точною назвою
Причина: Ефективний для відсортованого списку назв

3. Швидке сортування (SortingAlgorithms.kt)

Використання: Сортування рецептів за:

Часом приготування (від меншого до більшого)
Складністю (легкі → середні → складні)
Алфавітним порядком назв


Причина: Необхідне для бінарного пошуку та зручного перегляду

📦 Залежності для build.gradle
kotlindependencies {
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    
    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
🎨 Основна структура даних
Recipe.kt матиме наступні поля:

id: Long - унікальний ідентифікатор
name: String - назва рецепту
ingredients: List<String> - список інгредієнтів
difficulty: String - складність (Легкий/Середній/Складний)
cookingTime: Int - час приготування (хвилини)
instructions: String - опис приготування
dateCreated: Long - дата створення

🔄 Workflow додатку

MainActivity → показує список рецептів (RecyclerView)
Menu → пункти: Додати рецепт, Пошук, Сортування
AddRecipeActivity → додавання нового рецепту
SearchActivity → пошук (використовує лінійний/бінарний пошук)
EditRecipeActivity → редагування рецепту
RecipeDetailActivity → детальний перегляд рецепту

🎨 Кольорова схема (themes.xml)
xml<color name="black">#000000</color>
<color name="white">#FFFFFF</color>
<color name="dark_gray">#1E1E1E</color>
<color name="light_gray">#2D2D2D</color>
<color name="accent">#FFFFFF</color>
