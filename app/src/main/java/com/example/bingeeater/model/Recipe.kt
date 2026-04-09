package com.example.bingeeater.model

data class Recipe(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String?,
    val strInstructions: String?,
    val strMealThumb: String,
    var note: String? = null
)