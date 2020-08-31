package com.example.recipereaderkotlin.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipereaderkotlin.repositories.RecipeListRepository

/**
 * without this class we can't pass the repository class as param in the viewModel
 */
class ViewModelProviderFactory(  private val repository: RecipeListRepository, private val application: Application): ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RecipeViewModel( repository, application) as T
    }



}