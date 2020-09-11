package com.example.recipereaderkotlin.repositories

import com.example.recipereaderkotlin.service.RetrofitGenerator

class RecipeListRepository {

    //this one retrieves the list of recipes
    suspend fun fetchRecipeList(optionSelected: String, pageNumber: Int)  =
        RetrofitGenerator.apiConnection.getRecipeList(optionSelected, pageNumber.toString())

    suspend fun getRecipeDetails(recipeId : String) =
        RetrofitGenerator.apiConnection.getRecipeDetails(recipeId)


}