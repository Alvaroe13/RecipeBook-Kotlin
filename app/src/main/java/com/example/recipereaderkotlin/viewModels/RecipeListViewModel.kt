package com.example.recipereaderkotlin.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipereaderkotlin.models.Recipe
import com.example.recipereaderkotlin.repositories.RecipeListRepository
import com.example.recipereaderkotlin.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class RecipeListViewModel(
    private val repository : RecipeListRepository
)  : ViewModel() {


    val recipeListResponse: MutableLiveData<Resource<Recipe>> = MutableLiveData()

    var pageNumber = 1

    fun getRecipeList(optionSelected: String)  = viewModelScope.launch{
        println("RecipeListViewModel, option selected: $optionSelected")

        val response =  repository.fetchRecipeList(optionSelected, pageNumber)
        recipeListResponse.postValue(handleResponse(response))
        println("RecipeListViewModel, response : $response}")
    }

    /**
     * here we process the api response using the Resource class
     */
    private fun handleResponse(response: Response<Recipe>) : Resource<Recipe>{

        println("RecipeListViewModel, handleResponse called")
          if(response.isSuccessful){
                response.body()?.let {
                    println("RecipeListViewModel, successful and body NOT null")
                    return Resource.Success(it)
               }
           }
        println("RecipeListViewModel, response : ${response.message()}")
        return Resource.Error(null, response.message())
    }

}