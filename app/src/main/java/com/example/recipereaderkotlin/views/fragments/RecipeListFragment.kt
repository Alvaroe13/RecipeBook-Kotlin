package com.example.recipereaderkotlin.views.fragments

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipereaderkotlin.R
import com.example.recipereaderkotlin.models.Recipe
import com.example.recipereaderkotlin.models.RecipeResponse
import com.example.recipereaderkotlin.utils.Constants.Companion.JOB_TIMEOUT
import com.example.recipereaderkotlin.utils.Constants.Companion.PAGE_NUMBER
import com.example.recipereaderkotlin.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.example.recipereaderkotlin.utils.Resource
import com.example.recipereaderkotlin.viewModels.RecipeViewModel
import com.example.recipereaderkotlin.views.MainActivity
import com.example.recipereaderkotlin.views.adapters.RecipeListAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_recipe_list.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main


class RecipeListFragment : Fragment(R.layout.fragment_recipe_list), RecipeListAdapter.ClickHandler {

    private lateinit var optionSelected: String
    private lateinit var adapterRecipeList: RecipeListAdapter
    private lateinit var viewModel: RecipeViewModel
    private lateinit var layout: View
    private lateinit var navController: NavController
    private var recipeList = listOf<Recipe>()

    //pagination
    private var pageNumber = PAGE_NUMBER
    private var isLoading = false
    private var isScrolling = false
    private var resultsNumber = 0
    lateinit var layoutManagerRecycler: LinearLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewModel wired from activity
        viewModel = (activity as MainActivity).viewModel
        viewModel.recipeList = null
        //by getting the category title here we can send the request to the server
        incomingData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //without this we can't launch SnackBar when handling network time out
        layout = view
        tvToolbarTitle.text = optionSelected
        navController = Navigation.findNavController(view)
        initRecycler()
        connectionToServer()
        retryButton()
    }


    private fun incomingData() {
        optionSelected = arguments?.getString("CategoryClicked")!!
        println("RecipeListFragment, incomingInfo = $optionSelected")
    }

    /**  this one makes the request to the api */
    private fun requestRecipeList(title: String, pageNumber: Int) {
        isLoading = true
        viewModel.getRecipeList(title, pageNumber)
        println("RecipeListFragment, retrieveRecipeList called with $title")
    }

    private fun initRecycler() {
        adapterRecipeList = RecipeListAdapter(this)
        layoutManagerRecycler = LinearLayoutManager(activity)
        rvRecipeList.apply {
            adapter = adapterRecipeList
            layoutManager = layoutManagerRecycler
            addOnScrollListener(customScrollListener)
        }
    }

    private fun connectionToServer() {

        CoroutineScope(IO).launch {

            val hasInternet = viewModel.checkInternetConnection()

            if (hasInternet) {
                requestRecipeList(optionSelected, pageNumber)
                secureRecipeRetrieval()
            } else {
                //use job timeout to make user experience better before showing error message in snackBar
                delay(JOB_TIMEOUT)
                errorLoadingMessage("No internet connection")
            }

        }

    }

    /** this one handles network timeout, it will only get triggered if network request takes longer than 3 secs */
    private suspend fun secureRecipeRetrieval() {

        val job = withTimeoutOrNull(JOB_TIMEOUT) {
            withContext(Main) {
                println("RecipeListFragment, withContext called")
                subscribeObserver()
            }
        }
        if (job == null) {
            withContext(Main) {
                errorLoadingMessage("Something went wrong, try again")
            }
        }
    }

    private fun errorLoadingMessage(message: String) {
        println("RecipeListFragment, errorLoadingMessage called")
        MainScope().launch {
            hideProgressBar()
            btnRetryRecipeList.visibility = View.VISIBLE
            Snackbar.make(layout, message, Snackbar.LENGTH_LONG).show()
        }
    }


    /** this one retrieves the responds from the api  */
    private fun subscribeObserver() {

        viewModel.recipeListResponse.observe(viewLifecycleOwner, Observer { apiResponse ->
            when (apiResponse) {
                is Resource.Success -> {
                    successfulResponse(apiResponse)
                }
                is Resource.Error -> {
                    errorLoadingMessage("Network error...")
                    println("RecipeListFragment, Error = ${apiResponse.message}")
                }
                is Resource.Loading -> {
                    showProgressBar()
                    println("RecipeListFragment, loading state...")
                }
            }

        })

    }

    private fun successfulResponse(apiResponse: Resource.Success<RecipeResponse>) {
        println("RecipeListFragment, successfulResponse called!")
        if (apiResponse.data != null) {
            //these two are needed for pagination
            isLoading = false
            resultsNumber = apiResponse.data.count
            println("RecipeListFragment, DEBUG, result number is = $resultsNumber")

            if (apiResponse.data.recipes.size > 0) {
                btnRetryRecipeList.visibility = View.INVISIBLE
                println("RecipeListFragment, response = successful with SIZE=${apiResponse.data.recipes.size}")
                showRecipeList(apiResponse.data.recipes.toList())
            } else {
                showImageNotFound()
            }
        }


    }

    private fun showImageNotFound() {
        println("RecipeListFragment, showImageNotFound called, response = NO RESULT FOUND :(")
        MainScope().launch {
            delay(500L)
            hideProgressBar()
            ivResultNotFound.visibility = View.VISIBLE
        }
    }

    /** here we feed the DiffUtil list in adapter */
    private fun showRecipeList(list: List<Recipe>) {
        /*by setting recipeList = list we can then fetch recipe's info (title and
        author in this case) in itemClick function when item is pressed and sent it to RecipeDetailsFragment*/
        recipeList = list
        println("RecipeListFragment fed")
        adapterRecipeList.differAsync.submitList(list)
        hideProgressBar()
    }

    private fun showProgressBar() {
        isLoading = true
        pbRecipeList.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        isLoading = false
        pbRecipeList.visibility = View.INVISIBLE
    }

    /** button retries connection to the server when there was no internet in previous request*/
    private fun retryButton() {
        btnRetryRecipeList.setOnClickListener {
            btnRetryRecipeList.visibility = View.INVISIBLE
            showProgressBar()
            incomingData()
            connectionToServer()
        }
    }

    //-------------------------------Pagination Section-------------------------------------------//

    private val customScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val totalItemVisible = layoutManagerRecycler.childCount
            val firstItemVisible = layoutManagerRecycler.findFirstCompletelyVisibleItemPosition()
            val recyclerSize = adapterRecipeList.itemCount
            val reachEndOfList = totalItemVisible + firstItemVisible >= recyclerSize
            val moreResultLeftToFetch = resultsNumber >= QUERY_PAGE_SIZE
            println("RecipeListFragment, DEBUG, more result left to fetch = $moreResultLeftToFetch")

            val shouldPaginate = !isLoading && reachEndOfList && moreResultLeftToFetch

            if (shouldPaginate) {
                val newPage = pageNumber + 1
                requestRecipeList(optionSelected, newPage)
                println("RecipeListFragment, DEBUG, shouldPaginate called !, pageNumber requested = $newPage!!")
            }

        }
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    //-----------------------------Pagination Section ends here ---------------------------------//

    private fun openRecipe(title: String, image: String, rating: Double, recipeId: String) {
        val bundle = bundleOf("title" to title, "image" to image, "rating" to rating, "recipeId" to recipeId)
        findNavController().navigate( R.id.action_recipeListFragment2_to_recipeDetailsFragment, bundle)
    }

    override fun itemClick(position: Int) {
        val recipes = recipeList[position]
        openRecipe(recipes.title, recipes.image_url, recipes.social_rank, recipes.recipe_id)
    }

    override fun onPause() {
        super.onPause()
        println("DEBUG, onPause() called")
        viewModel.recipeList = null
        ivResultNotFound.visibility = View.GONE
        /*we set observable null here to make sure next time the user selects a recipe category it doesn't sho
        recipe from the previous time*/
        viewModel.recipeListResponse.postValue(null)
    }
}