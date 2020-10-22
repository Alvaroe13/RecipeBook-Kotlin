package com.example.recipereaderkotlin.repositories

import com.example.recipereaderkotlin.service.Api
import javax.inject.Inject

class RecipeListRepository @Inject constructor(
    private val connectionApi : Api
) {

    suspend fun fetchRecipeList(optionSelected: String?, pageNumber: Int)  =
                           connectionApi.getRecipeList(optionSelected, pageNumber)

    suspend fun getRecipeDetails(recipeId : String) =
                  connectionApi.getRecipeDetails(recipeId)
}