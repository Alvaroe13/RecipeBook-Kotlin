package com.example.recipereaderkotlin.service

import com.example.recipereaderkotlin.models.RecipeDetails
import com.example.recipereaderkotlin.models.RecipeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("api/search")  //fetch list of all recipes from server
    suspend fun getRecipeList(
        @Query("q") recipeType : String,
        @Query("page") pageNumber : String) : Response<RecipeResponse>

    @GET("api/get") //fetch recipes details from server
    suspend fun getRecipeDetails(
        @Query("rId") recipeId : String
    ) : Response<RecipeDetails>
}