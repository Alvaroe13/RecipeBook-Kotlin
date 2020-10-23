package com.example.recipereaderkotlin.service.cache

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipereaderkotlin.models.Recipe

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRecipeList( recipeList : Recipe ) : Long

    @Query("SELECT * from recipe_list_db")
    fun getAllExistingRecipes(): LiveData<List<Recipe>>

}