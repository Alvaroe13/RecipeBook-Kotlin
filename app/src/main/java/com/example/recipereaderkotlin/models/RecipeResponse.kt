package com.example.recipereaderkotlin.models

import com.example.recipereaderkotlin.models.Recipe

data class RecipeResponse(
    val count: Int,
    val recipes: List<Recipe>
)