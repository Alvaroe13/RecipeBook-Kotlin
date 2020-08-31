package com.example.recipereaderkotlin.views.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipereaderkotlin.R
import com.example.recipereaderkotlin.models.Recipe
import com.example.recipereaderkotlin.utils.Constants.Companion.JOB_TIMEOUT
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

    private lateinit var incomingInfo: String
    private lateinit var adapterRecipeList: RecipeListAdapter
    private lateinit var viewModel: RecipeViewModel
    private lateinit var layout: View
    private lateinit var navController: NavController


    private var recipeList = listOf<Recipe>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //by getting the category title here we can send the request to the server
        incomingInfo = arguments?.getString("CategoryClicked")!!
        //viewModel wired from activity
        viewModel = (activity as MainActivity).viewModel
        requestRecipeList(incomingInfo)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //without this we can't launch SnackBar when handling network time out
        layout = view
        //toolbar title
        tvToolbarTitle.text = incomingInfo
        //nav component
        navController = Navigation.findNavController(view)
        initRecycler()
        connectionToServer()
        retryButton()
    }

    private fun retryButton() {
        btnRetryRecipeList.setOnClickListener{
            btnRetryRecipeList.visibility= View.INVISIBLE
            showProgressBar()
            incomingInfo = arguments?.getString("CategoryClicked")!!
            println("Debugging, incomingInfo = $incomingInfo")
            requestRecipeList(incomingInfo)
            connectionToServer()
        }
    }

    /**
     * this one makes the request to the api
     */
    private fun requestRecipeList(title: String) {
        viewModel.getRecipeList(title)
        println("RecipeListFragment, retrieveRecipeList called with $title")
    }

    private fun initRecycler() {
        adapterRecipeList = RecipeListAdapter(this)
        rvRecipeList.apply {
            adapter = adapterRecipeList
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun connectionToServer() {

        CoroutineScope(IO).launch {

            val hasInternet = viewModel.checkInternetConnection()

            if (hasInternet) {
                secureRecipeRetrieval()
            } else {
                //use job timeout to make user experience better before showing error message in snackBar
                delay(JOB_TIMEOUT)
                errorLoadingMessage("No internet connection")
            }

        }

    }

    /**
     * this one handles network timeout, it will only get triggered if network request takes longer than 3 secs
     */
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
        CoroutineScope(Main).launch {
            hideProgressBar()
            btnRetryRecipeList.visibility = View.VISIBLE
            Snackbar.make(layout, message, Snackbar.LENGTH_LONG).show()
            println("RecipeListFragment, NO internet connection")
        }
    }


    /**
     * this one retrieves the responds from the api
     */
    private fun subscribeObserver() {

        viewModel.recipeListResponse.observe(viewLifecycleOwner, Observer { apiResponse ->
            when (apiResponse) {
                is Resource.Success -> {
                    if (apiResponse.data != null) {
                        btnRetryRecipeList.visibility = View.INVISIBLE
                        println("RecipeListFragment, response = successful with SIZE=${apiResponse.data.recipes.size}")
                        showRecipeList(apiResponse.data.recipes)
                    } else {
                        println("RecipeListFragment, response = successful but null")
                    }
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



    /**
     * here we feed the DiffUtil list in adapter
     */
    private fun showRecipeList(list: MutableList<Recipe>) {
        /*by setting recipeList = list we can then fetch recipe's info (title and
        author in this case) in itemClick function when item is pressed and sent it to RecipeDetailsFragment*/
        recipeList = list
        println("RecipeListFragment fed")
        adapterRecipeList.differAsync.submitList(list)
        hideProgressBar()
    }

    private fun showProgressBar() {
        println("RecipeListFragment, progressBar show")
        pbRecipeList.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        println("RecipeListFragment, progressBar hide")
        pbRecipeList.visibility = View.INVISIBLE
    }


    private fun openRecipe(title: String, image: String, rating: Double, recipeId: String) {
        val bundle =
            bundleOf("title" to title, "image" to image, "rating" to rating, "recipeId" to recipeId)
        findNavController().navigate( R.id.action_recipeListFragment2_to_recipeDetailsFragment, bundle )
    }

    override fun itemClick(position: Int) {
        val recipes = recipeList[position]
        openRecipe(recipes.title, recipes.image_url, recipes.social_rank, recipes.recipe_id)
    }

}