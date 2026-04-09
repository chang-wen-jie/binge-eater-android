package com.example.bingeeater.ui.screens

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bingeeater.model.Recipe
import com.example.bingeeater.ui.components.NetworkImage
import com.example.bingeeater.viewmodel.FavoritesViewModel

@Composable
fun RecipeDetailScreen(recipe: Recipe, favoritesViewModel: FavoritesViewModel) {
    val savedRecipes by favoritesViewModel.savedRecipes.collectAsState()
    val isFavorite = savedRecipes.any { it.idMeal == recipe.idMeal }

    val initialNote = savedRecipes.find { it.idMeal == recipe.idMeal }?.note ?: ""
    var noteText by remember(isFavorite) { mutableStateOf(initialNote) }
    var mealPhoto by remember { mutableStateOf<Bitmap?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        mealPhoto = bitmap
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(null)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color(0xFFFFB74D).copy(alpha = 0.3f))
        )

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Box(modifier = Modifier.fillMaxWidth().offset(y = (-50).dp), contentAlignment = Alignment.Center) {
                NetworkImage(
                    url = recipe.strMealThumb,
                    modifier = Modifier
                        .size(220.dp)
                        .shadow(elevation = 7.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .border(width = 4.dp, color = Color.White, shape = CircleShape)
                )
            }

            Text(text = recipe.strMeal, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = "Categorie: ${recipe.strCategory ?: "Categorieloos"}", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )

            Text(text = "Instructies", style = MaterialTheme.typography.titleLarge)
            Text(text = recipe.strInstructions ?: "Geen instructies beschikbaar...", modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.height(16.dp))

            if (isFavorite) {
                Text("Notities", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 10.dp))
                OutlinedTextField(
                    value = noteText,
                    onValueChange = {
                        noteText = it
                        favoritesViewModel.updateNote(recipe, it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(vertical = 8.dp)
                        .background(Color(0xFFFFE0B2), RoundedCornerShape(10.dp)),
                    // Sticky note kleuren forceren
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (mealPhoto != null) {
                Text("Mijn creatie:", style = MaterialTheme.typography.titleMedium)
                Image(
                    bitmap = mealPhoto!!.asImageBitmap(),
                    contentDescription = "Mijn gemaakte foto",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            Button(
                onClick = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Maak een foto van mijn gerecht", color = Color.White)
            }

            Button(
                onClick = { favoritesViewModel.toggleFavorite(recipe) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFavorite) Color.Red else Color.Blue
                ),
                modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (isFavorite) "Verwijder uit favorieten" else "Toevoegen aan favorieten", color = Color.White)
            }
        }
    }
}