package com.example.recipereaderkotlin.service.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.recipereaderkotlin.models.Recipe

@Database( entities = [Recipe::class], version = 1)
abstract class RecipeDatabase: RoomDatabase() {

    abstract fun getDao() : RecipeDao
}