package com.example.recipereaderkotlin.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipereaderkotlin.models.Recipe
import com.example.recipereaderkotlin.models.RecipeDetails
import com.example.recipereaderkotlin.models.RecipeResponse
import com.example.recipereaderkotlin.repositories.RecipeListRepository
import com.example.recipereaderkotlin.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class RecipeListViewModel(
    private val repository : RecipeListRepository
)  : ViewModel() {


    val recipeListResponse: MutableLiveData<Resource<RecipeResponse>> = MutableLiveData()
    val recipeDetail : MutableLiveData<Resource<RecipeDetails>> = MutableLiveData()

    var pageNumber = 1

    //--------------------Recipe List section ---------------------------//

    fun getRecipeList(optionSelected: String)  = viewModelScope.launch{
        println("RecipeListViewModel, getRecipeList, option selected: $optionSelected")

        val response =  repository.fetchRecipeList(optionSelected, pageNumber)
        println("RecipeListViewModel, getRecipeList,  response : ${response}}")
        recipeListResponse.postValue(handleResponse(response))
    }

    /**
     * here we process the api response using the Resource class
     */
    private fun handleResponse(response: Response<RecipeResponse>) : Resource<RecipeResponse>{

        println("RecipeListViewModel, handleResponse, called")
          if(response.isSuccessful){
                response.body()?.let {
                    println("RecipeListViewModel, handleResponse, successful and body NOT null")
                    return Resource.Success(it)
               }
           }
        println("RecipeListViewModel, handleResponse, response : ${response.message()}")
        return Resource.Error(null, response.message())
    }

    //-----------------------Recipe details section-----------------------------//

    fun getRecipeDetails(recipeId : String)= viewModelScope.launch {
        println("RecipeListViewModel, getRecipeDetails,  recipeID = $recipeId")

        val recipeDetailsResponse = repository.getRecipeDetails(recipeId)
        println("RecipeListViewModel, getRecipeDetails, response : ${recipeDetailsResponse.body()?.recipe}")
        recipeDetail.postValue(processResponse(recipeDetailsResponse))
    }

    private fun processResponse(recipeDetailsResponse: Response<RecipeDetails>): Resource<RecipeDetails>? {

        println("RecipeListViewModel, processResponse,  called")
        if(recipeDetailsResponse.isSuccessful){
            recipeDetailsResponse.body()?.let {
                println("RecipeListViewModel, processResponse, successful and body NOT null")
                return Resource.Success(it)
            }
        }
        println("RecipeListViewModel, processResponse, response processed : ${recipeDetailsResponse.message()}")
        return Resource.Error(null, recipeDetailsResponse.message())
    }

}