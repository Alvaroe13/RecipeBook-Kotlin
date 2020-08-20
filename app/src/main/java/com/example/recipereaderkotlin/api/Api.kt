package com.example.recipereaderkotlin.api

import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("api/search")  //fetch list of all recipes from server
    suspend fun getRecipeList(
        @Query("q") recipeType : String,
        @Query("page") pageNumber : String)

    @GET("api/get") //fetch recipes details from server
    suspend fun getRecipeDtails(
        @Query("rId") recipeId : String
    )
}