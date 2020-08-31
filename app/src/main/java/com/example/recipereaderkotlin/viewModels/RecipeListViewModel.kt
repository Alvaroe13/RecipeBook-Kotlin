package com.example.recipereaderkotlin.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipereaderkotlin.models.RecipeDetails
import com.example.recipereaderkotlin.models.RecipeResponse
import com.example.recipereaderkotlin.repositories.RecipeListRepository
import com.example.recipereaderkotlin.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

/**
 * This viewModel will be shared with RecipeList and RecipeDetails fragments
 */
class RecipeListViewModel(
    private val repository : RecipeListRepository,
    private val application : Application
)  : ViewModel() {


    val recipeListResponse: MutableLiveData<Resource<RecipeResponse>> = MutableLiveData()
    val recipeDetail : MutableLiveData<Resource<RecipeDetails>> = MutableLiveData()

    var pageNumber = 1

    //------------------------------Recipe List section ---------------------------------------//

    fun getRecipeList(optionSelected: String)  = viewModelScope.launch{
        println("RecipeListViewModel, getRecipeList, option selected: $optionSelected")
        recipeListResponse.postValue(Resource.Loading())

        try {
            if (checkInternetConnection()){
                val response =  repository.fetchRecipeList(optionSelected, pageNumber)
                println("RecipeListViewModel, getRecipeList,  response : ${response}}")
                recipeListResponse.postValue(handleResponse(response))
            } else{
                recipeListResponse.postValue(Resource.Error(null, "No internet connection"))
            }
        }catch (t : Throwable){
            when(t){
                is IOException -> recipeListResponse.postValue(Resource.Error(null,"Network Failure"))
                else -> recipeListResponse.postValue(Resource.Error(null,"Conversion Error"))
            }
        }
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

    //------------------------------Recipe details section---------------------------------------//

    fun getRecipeDetails(recipeId : String)= viewModelScope.launch {
        println("RecipeListViewModel, getRecipeDetails,  recipeID = $recipeId")
        recipeDetail.postValue(Resource.Loading())
        try {
            if (checkInternetConnection()){
                val recipeDetailsResponse = repository.getRecipeDetails(recipeId)
                println("RecipeListViewModel, getRecipeDetails, response : ${recipeDetailsResponse.body()?.recipe}")
                recipeDetail.postValue(processResponse(recipeDetailsResponse))
            }else{
                recipeDetail.postValue(Resource.Error(null, "No internet connection"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> recipeDetail.postValue(Resource.Error(null,"Network Failure"))
                else -> recipeDetail.postValue(Resource.Error(null,"Conversion Error"))
            }
        }

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

    //------------------------------------ internet connection ---------------------------------//

    /**
     *this function checks if device has internet connection. Function created in this viewModel
     * since we're gonna use it in both RecipeList and RecipeDetails fragments
     */
    fun checkInternetConnection(): Boolean {
        println("NewsFeedViewModel, checkInternetConnection : called!!")
        val connectivityManager = application.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            println("NewsFeedViewModel greater than 23 api : called!!")
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else { //we also handle if device's os version is less than 23 api level
            println("NewsFeedViewModel less than 23 api: called")
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}