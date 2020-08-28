package com.example.recipereaderkotlin.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipereaderkotlin.R
import com.example.recipereaderkotlin.models.Recipe
import com.example.recipereaderkotlin.utils.Resource
import com.example.recipereaderkotlin.viewModels.RecipeListViewModel
import com.example.recipereaderkotlin.views.adapters.RecipeListAdapter
import kotlinx.android.synthetic.main.fragment_recipe_list.*


class RecipeListFragment : Fragment(R.layout.fragment_recipe_list) {

    private lateinit var incomingInfo: String
    private lateinit var adapterRecipeList: RecipeListAdapter
    private lateinit var viewModel: RecipeListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        incomingInfo = arguments?.getString("CategoryClicked")!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel wired from activity
        viewModel = (activity as MainActivity).viewModel
        //toolbar title
        tvToolbarTitle.text = incomingInfo
        initRecycler()
        requestRecipeList(incomingInfo)
        subscribeObserver()

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
        println("RecipeListFragment, called retrieveRecipeList function")

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