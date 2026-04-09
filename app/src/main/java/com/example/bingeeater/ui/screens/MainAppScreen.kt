package com.example.bingeeater.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.example.bingeeater.viewmodel.FavoritesViewModel
import com.example.bingeeater.viewmodel.RecipeViewModel

@Composable
fun MainAppScreen(recipeViewModel: RecipeViewModel, favoritesViewModel: FavoritesViewModel) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = "Gerechten") },
                    label = { Text("Gerechten") },
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        navController.navigate("recipeList") { launchSingleTop = true }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorieten") },
                    label = { Text("Favorieten") },
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        navController.navigate("favorites") { launchSingleTop = true }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = "recipeList", Modifier.padding(innerPadding)) {
            composable("recipeList") {
                RecipeListScreen(recipeViewModel, navController)
            }
            composable("favorites") {
                FavoritesScreen(favoritesViewModel, navController)
            }
            composable("recipeDetail/{recipeId}") { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId")

                // UI meteen updaten
                val recipes by recipeViewModel.recipes.collectAsState()
                val favorites by favoritesViewModel.savedRecipes.collectAsState()

                val recipe = recipes.find { it.idMeal == recipeId }
                    ?: favorites.find { it.idMeal == recipeId }

                if (recipe != null) {
                    RecipeDetailScreen(recipe, favoritesViewModel)
                }
            }
        }
    }
}