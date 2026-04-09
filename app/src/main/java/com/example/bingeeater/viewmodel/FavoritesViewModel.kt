package com.example.bingeeater.viewmodel

import com.example.bingeeater.model.Recipe
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import androidx.core.content.edit

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val _savedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val savedRecipes: StateFlow<List<Recipe>> = _savedRecipes.asStateFlow()

    private val sharedPreferences = application.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    private val saveKey = "favorites"

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        val jsonString = sharedPreferences.getString(saveKey, null)

        if (jsonString != null) {
            try {
                val jsonArray = JSONArray(jsonString)
                val loadedRecipes = mutableListOf<Recipe>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    loadedRecipes.add(
                        Recipe(
                            idMeal = obj.getString("idMeal"),
                            strMeal = obj.getString("strMeal"),
                            strCategory = if (!obj.isNull("strCategory")) obj.getString("strCategory") else null,
                            strInstructions = if (!obj.isNull("strInstructions")) obj.getString("strInstructions") else null,
                            strMealThumb = obj.getString("strMealThumb"),
                            note = if (!obj.isNull("note")) obj.getString("note") else null
                        )
                    )
                }
                _savedRecipes.value = loadedRecipes
            } catch (e: Exception) {
                e.printStackTrace()
                _savedRecipes.value = emptyList()
            }
        } else {
            _savedRecipes.value = emptyList()
        }
    }

    private fun saveFavorites() {
        try {
            val jsonArray = JSONArray()

            for (recipe in _savedRecipes.value) {
                val obj = JSONObject()
                obj.put("idMeal", recipe.idMeal)
                obj.put("strMeal", recipe.strMeal)
                obj.put("strCategory", recipe.strCategory ?: JSONObject.NULL)
                obj.put("strInstructions", recipe.strInstructions ?: JSONObject.NULL)
                obj.put("strMealThumb", recipe.strMealThumb)
                obj.put("note", recipe.note ?: JSONObject.NULL)
                jsonArray.put(obj)
            }

            sharedPreferences.edit { putString(saveKey, jsonArray.toString()) }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun contains(recipe: Recipe): Boolean {
        return _savedRecipes.value.any { it.idMeal == recipe.idMeal }
    }

    fun toggleFavorite(recipe: Recipe) {
        val currentList = _savedRecipes.value.toMutableList() // Lijst is read-only

        if (contains(recipe)) {
            currentList.removeAll { it.idMeal == recipe.idMeal }
        } else {
            currentList.add(recipe)
        }
        _savedRecipes.value = currentList
        saveFavorites()
    }

    fun deleteRecipe(recipe: Recipe) {
        val currentList = _savedRecipes.value.toMutableList()
        currentList.removeAll { it.idMeal == recipe.idMeal }
        _savedRecipes.value = currentList
        saveFavorites()
    }

    fun updateNote(recipe: Recipe, note: String) {
        val currentList = _savedRecipes.value.toMutableList()
        val index = currentList.indexOfFirst { it.idMeal == recipe.idMeal }

        if (index != -1) {
            currentList[index] = currentList[index].copy(note = note)
            _savedRecipes.value = currentList
            saveFavorites()
        }
    }
}