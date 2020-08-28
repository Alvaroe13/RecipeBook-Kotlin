package com.example.recipereaderkotlin.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipereaderkotlin.R
import com.example.recipereaderkotlin.models.Recipe
import com.example.recipereaderkotlin.utils.Constants.Companion.JOB_TIMEOUT
import com.example.recipereaderkotlin.utils.Resource
import com.example.recipereaderkotlin.viewModels.RecipeListViewModel
import com.example.recipereaderkotlin.views.adapters.RecipeListAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_recipe_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull


class RecipeListFragment : Fragment(R.layout.fragment_recipe_list) {

    private lateinit var incomingInfo: String
    private lateinit var adapterRecipeList: RecipeListAdapter
    private lateinit var viewModel: RecipeListViewModel
    private lateinit var layout: View



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        incomingInfo = arguments?.getString("CategoryClicked")!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //without this we can't launch SnackBar when handling network time out
        layout = view
        //viewModel wired from activity
        viewModel = (activity as MainActivity).viewModel
        //toolbar title
        tvToolbarTitle.text = incomingInfo
        initRecycler()
        requestRecipeList(incomingInfo)


        CoroutineScope(IO).launch {
            secureRecipeRetrieval()
        }

    }

    private fun initRecycler() {
        adapterRecipeList = RecipeListAdapter()
        rvRecipeList.apply {
            adapter = adapterRecipeList
            layoutManager = LinearLayoutManager(activity)
        }
    }


    /**
     * this one makes the request to the api
     */
    private fun requestRecipeList(title: String) {
        viewModel.getRecipeList(title)
        println("RecipeListFragment, retrieveRecipeList called with $title")
    }

    /**
     * this one retrieves the responds from the api
     */
    private fun subscribeObserver() {

            viewModel.recipeListResponse.observe(viewLifecycleOwner, Observer { apiResponse ->
                when (apiResponse) {
                    is Resource.Success -> {
                        hideProgressBar()
                        if (apiResponse.data != null) {
                            println("RecipeListFragment, response = successful with SIZE=${apiResponse.data.recipes.size}")
                            showRecipeList(apiResponse.data.recipes)
                        } else {
                            println("RecipeListFragment, response = successful but null")
                        }
                    }
                    is Resource.Error -> {
                        hideProgressBar()
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
     * this one handles network timeout, it will only get triggered if network request takes longer than 3 secs
     */
    private suspend fun secureRecipeRetrieval(){
        withContext(IO){
            val job = withTimeoutOrNull(JOB_TIMEOUT){
                withContext(Main){
                    println("RecipeListFragment, withContext called")
                    subscribeObserver()
                }
            }
            if (job == null){
                withContext(Main){
                    hideProgressBar()
                    ivTimeOut.visibility = View.VISIBLE
                    Snackbar.make(layout, "Something went wrong, check internet connection", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * here we feed the DiffUtil list in adapter
     */
    private fun showRecipeList(list: MutableList<Recipe>) {
        println("RecipeListFragment fed")
        adapterRecipeList.differAsync.submitList(list)
    }

    private fun showProgressBar() {
        println("RecipeListFragment, progressBar show")
        pbRecipeList.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        println("RecipeListFragment, progressBar hide")
        pbRecipeList.visibility = View.INVISIBLE
    }

}