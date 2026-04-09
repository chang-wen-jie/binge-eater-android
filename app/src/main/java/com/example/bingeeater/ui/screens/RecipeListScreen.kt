package com.example.bingeeater.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bingeeater.ui.components.RecipeRow
import com.example.bingeeater.viewmodel.RecipeViewModel

@Composable
fun RecipeListScreen(viewModel: RecipeViewModel, navController: NavController) {
    val recipes by viewModel.recipes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showSettingsDialog by remember { mutableStateOf(false) }
    var tempDefaultQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchRecipes(searchQuery)
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Instellingen") },
            text = {
                Column {
                    Text("Stel je standaard zoekterm in:")
                    OutlinedTextField(
                        value = tempDefaultQuery,
                        onValueChange = { tempDefaultQuery = it },
                        singleLine = true,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.saveDefaultQuery(tempDefaultQuery)
                    showSettingsDialog = false
                }) {
                    Text("Opslaan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Annuleren")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                label = { Text("Zoek een gerecht...") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                trailingIcon = {
                    Button(onClick = { viewModel.fetchRecipes(searchQuery) }, modifier = Modifier.padding(end = 4.dp)) {
                        Text("Zoek")
                    }
                }
            )

            IconButton(onClick = {
                tempDefaultQuery = searchQuery
                showSettingsDialog = true
            }) {
                Icon(Icons.Default.Settings, contentDescription = "Instellingen")
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(recipes) { recipe ->
                Box(modifier = Modifier.clickable {
                    navController.navigate("recipeDetail/${recipe.idMeal}")
                }) {
                    RecipeRow(recipe = recipe)
                }
            }
        }
    }
}