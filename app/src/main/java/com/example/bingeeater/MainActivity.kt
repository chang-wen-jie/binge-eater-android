package com.example.bingeeater

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bingeeater.ui.screens.MainAppScreen
import com.example.bingeeater.viewmodel.FavoritesViewModel
import com.example.bingeeater.viewmodel.RecipeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val recipeViewModel: RecipeViewModel = viewModel()
            val favoritesViewModel: FavoritesViewModel = viewModel()

            MainAppScreen(
                recipeViewModel = recipeViewModel,
                favoritesViewModel = favoritesViewModel
            )
        }
    }
}