package com.example.recipereaderkotlin.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipereaderkotlin.repositories.RecipeListRepository

/**
 * without this class we can't pass the repository class as param in the viewModel
 */
class ViewModelProviderFactory(  private val repository: RecipeListRepository): ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RecipeListViewModel( repository) as T
    }



}