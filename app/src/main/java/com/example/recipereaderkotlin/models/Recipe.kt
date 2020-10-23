package com.example.recipereaderkotlin.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.recipereaderkotlin.utils.Constants.DATABASE_NAME_RECIPE_LIST
import com.google.gson.annotations.SerializedName

@Entity( tableName = DATABASE_NAME_RECIPE_LIST)
data class Recipe(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("_id")
    var id: String,

    var image_url: String,
    var publisher: String,
    var publisher_url: String,
    var recipe_id: String,
    var social_rank: Double,
    var source_url: String,
    var title: String,
    @SerializedName("ingredients")
    var recipeDetails : MutableList<String>
)