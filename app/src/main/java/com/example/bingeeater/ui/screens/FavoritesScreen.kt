package com.example.bingeeater.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bingeeater.ui.components.RecipeRow
import com.example.bingeeater.viewmodel.FavoritesViewModel

@Composable
fun FavoritesScreen(viewModel: FavoritesViewModel, navController: NavController) {
    val favorites by viewModel.savedRecipes.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Favorieten", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(favorites) { recipe ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable {
                        navController.navigate("recipeDetail/${recipe.idMeal}")
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        RecipeRow(recipe = recipe)
                    }
                    IconButton(onClick = { viewModel.deleteRecipe(recipe) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}