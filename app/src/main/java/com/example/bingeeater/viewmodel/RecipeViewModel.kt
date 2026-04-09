package com.example.bingeeater.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bingeeater.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import androidx.core.content.edit

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    private val defaultQueryKey = "default_search_query"

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    private val initialQuery = sharedPreferences.getString(defaultQueryKey, "chicken") ?: "chicken"
    private val _searchQuery = MutableStateFlow(initialQuery)
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun saveDefaultQuery(query: String) {
        sharedPreferences.edit { putString(defaultQueryKey, query) }
        _searchQuery.value = query
        fetchRecipes(query)
    }

    fun fetchRecipes(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val safeQuery = URLEncoder.encode(query, "UTF-8")
                val url = URL("https://www.themealdb.com/api/json/v1/1/search.php?s=$safeQuery")

                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val responseText = connection.inputStream.bufferedReader().use { it.readText() } // Lees als Text
                    val jsonObject = JSONObject(responseText)
                    val mealsArray = jsonObject.optJSONArray("meals")
                    val parsedRecipes = mutableListOf<Recipe>()

                    if (mealsArray != null) {
                        for (i in 0 until mealsArray.length()) {
                            val mealObj = mealsArray.getJSONObject(i)
                            parsedRecipes.add(
                                Recipe(
                                    idMeal = mealObj.getString("idMeal"),
                                    strMeal = mealObj.getString("strMeal"),
                                    strCategory = mealObj.optString("strCategory", null),
                                    strInstructions = mealObj.optString("strInstructions", null),
                                    strMealThumb = mealObj.getString("strMealThumb")
                                )
                            )
                        }
                    }
                    _recipes.value = parsedRecipes
                } else {
                    Log.e("RecipeViewModel", "Foutieve URL of Response")
                }
                connection.disconnect()
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Fout bij ophalen data: ${e.localizedMessage}")
            }
        }
    }
}