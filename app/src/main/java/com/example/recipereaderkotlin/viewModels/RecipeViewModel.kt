package com.example.recipereaderkotlin.viewModels

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.recipereaderkotlin.models.RecipeDetails
import com.example.recipereaderkotlin.models.RecipeResponse
import com.example.recipereaderkotlin.repositories.RecipeListRepository
import com.example.recipereaderkotlin.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

/**
 * This viewModel will be shared with RecipeList and RecipeDetails fragments
 */

class RecipeViewModel @ViewModelInject constructor(
    private val repository: RecipeListRepository,
    @ApplicationContext private val application: Context,
    @Assisted private val savedStateHandle: SavedStateHandle //hilt stuff
) : ViewModel() {

    private val titleFromFragment: MutableLiveData<String> = MutableLiveData()

    val recipeListResponse: MutableLiveData<Resource<RecipeResponse>> = MutableLiveData()
    val recipeDetail: MutableLiveData<Resource<RecipeDetails>> = MutableLiveData()
    var recipeList: RecipeResponse? = null

    //----------------------------- connection between fragments ------------------------------//

    fun passCategorySelected(categorySelected: String) {
        println("RecipeViewModel, passCategorySelected, title= $categorySelected")
        titleFromFragment.value = categorySelected
    }

    fun getTitle() : LiveData<String>{
        return titleFromFragment
    }

    //------------------------------Recipe List section ---------------------------------------//

    fun getRecipeList(optionSelected: String?, pageNumber: Int) = viewModelScope.launch {
        println("RecipeListViewModel, getRecipeList, option selected: $optionSelected and page number: $pageNumber")
        recipeListResponse.postValue(Resource.Loading())

        try {
            if (checkInternetConnection()) {
                val response = repository.fetchRecipeList(optionSelected, pageNumber)
                println("RecipeListViewModel, getRecipeList,  response : ${response}}")
                recipeListResponse.postValue(handleResponse(response))
            } else {
                recipeListResponse.postValue(Resource.Error(null, "No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> recipeListResponse.postValue(
                    Resource.Error(
                        null,
                        "Network Failure"
                    )
                )
                else -> recipeListResponse.postValue(Resource.Error(null, "Conversion Error"))
            }
        }
    }

    /**  here we process the api response using the Resource class */
    private fun handleResponse(response: Response<RecipeResponse>): Resource<RecipeResponse> {

        println("RecipeListViewModel, handleResponse, called")
        if (response.isSuccessful) {
            response.body()?.let { apiResponse ->

                if (recipeList == null) {
                    println("RecipeListViewModel, handleResponse, recipeList is NULL")
                    recipeList = apiResponse
                } else {
                    println("RecipeListViewModel, handleResponse, recipeList is NOT NULL")
                    val oldRecipes = recipeList?.recipes
                    val newRecipes = apiResponse.recipes
                    oldRecipes?.addAll(newRecipes)
                }
                //here we pass recipeList, in case recipeList is null we pass apiResponse
                return Resource.Success(recipeList ?: apiResponse)
            }
        }
        println("RecipeListViewModel, handleResponse, response : ${response.message()}")
        return Resource.Error(null, response.message())
    }

    //------------------------------Recipe details section---------------------------------------//

    fun getRecipeDetails(recipeId: String) = viewModelScope.launch {
        println("RecipeListViewModel, getRecipeDetails,  recipeID = $recipeId")
        recipeDetail.postValue(Resource.Loading())
        try {
            if (checkInternetConnection()) {
                val recipeDetailsResponse = repository.getRecipeDetails(recipeId)
                println("RecipeListViewModel, getRecipeDetails, response : ${recipeDetailsResponse.body()?.recipe}")
                recipeDetail.postValue(processResponse(recipeDetailsResponse))
            } else {
                recipeDetail.postValue(Resource.Error(null, "No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> recipeDetail.postValue(Resource.Error(null, "Network Failure"))
                else -> recipeDetail.postValue(Resource.Error(null, "Conversion Error"))
            }
        }

    }

    private fun processResponse(recipeDetailsResponse: Response<RecipeDetails>): Resource<RecipeDetails>? {

        println("RecipeListViewModel, processResponse,  called")
        if (recipeDetailsResponse.isSuccessful) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            println("NewsFeedViewModel greater than 23 api : called!!")
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else { //we also handle if device's os version is less than 23 api level
            println("NewsFeedViewModel less than 23 api: called")
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
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