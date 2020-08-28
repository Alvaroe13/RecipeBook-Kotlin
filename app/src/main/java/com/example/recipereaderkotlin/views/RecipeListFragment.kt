package com.example.recipereaderkotlin.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipereaderkotlin.R
import com.example.recipereaderkotlin.models.Recipe
import com.example.recipereaderkotlin.views.adapters.RecipeListAdapter
import kotlinx.android.synthetic.main.fragment_recipe_list.*


class RecipeListFragment : Fragment(R.layout.fragment_recipe_list) {

    lateinit var incomingInfo : String
    lateinit var adapterRecipeList : RecipeListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        incomingInfo = arguments?.getString("CategoryClicked")!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //toolbar title
        tvToolbarTitle.text = incomingInfo
        initRecycler()

    }

    private fun initRecycler(){
        adapterRecipeList = RecipeListAdapter()
        rvRecipeList.apply {
            adapter = adapterRecipeList
            layoutManager = LinearLayoutManager(activity)
        }

        showRecipeList()
    }

    /**
     * categories to show in the firstView
     */
    private fun setCategories(): MutableList<Recipe>{
        return mutableListOf(
            Recipe( "Barbecue", null, null, null, null, null, null, null),
            Recipe( "Barbecue", null, null, null, null, null, null, null),
            Recipe( "Barbecue", null, null, null, null, null, null, null),
            Recipe( "Barbecue", null, null, null, null, null, null, null),
            Recipe( "Barbecue", null, null, null, null, null, null, null),
            Recipe( "Barbecue", null, null, null, null, null, null, null),
            Recipe( "Barbecue", null, null, null, null, null, null, null)
        )
    }

    private fun showRecipeList(){
        adapterRecipeList.differAsync.submitList(setCategories())
    }

}