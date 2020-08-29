package com.example.recipereaderkotlin.models

import com.google.gson.annotations.SerializedName

class RecipeDetails(
    @SerializedName("recipe")
    val recipe : Recipe
)