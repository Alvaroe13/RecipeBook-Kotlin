package com.example.recipereaderkotlin.models

import com.google.gson.annotations.SerializedName

data class Recipe(
    @SerializedName("_id")
    val id: String ,
    val image_url: String ,
    val publisher: String ,
    val publisher_url: String ,
    val recipe_id: String ,
    val social_rank: Double ,
    val source_url: String ,
    val title: String
)