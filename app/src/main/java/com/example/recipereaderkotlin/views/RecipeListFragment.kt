package com.example.recipereaderkotlin.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipereaderkotlin.R
import com.example.recipereaderkotlin.models.RecipeCategories
import com.example.recipereaderkotlin.views.adapters.RecipeListAdapter
import kotlinx.android.synthetic.main.fragment_recipe_list.*

class RecipeListFragment : Fragment(R.layout.fragment_recipe_list) {

    lateinit var navController : NavController
    lateinit var adapterRecipes : RecipeListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        initRecyclerView()

    }

    private fun setCategories(): MutableList<RecipeCategories>{

        return mutableListOf(
            RecipeCategories( "barbecue", R.drawable.barbecue  ),
            RecipeCategories( "beef", R.drawable.beef ),
            RecipeCategories(  "breakfast", R.drawable.breakfast ),
            RecipeCategories("brunch", R.drawable.brunch),
            RecipeCategories( "chicken", R.drawable.chicken),
            RecipeCategories("dinner",R.drawable.dinner  ),
            RecipeCategories("italian",  R.drawable.italian),
            RecipeCategories(  "wine", R.drawable.wine)
        )

    }
    private fun initRecyclerView() {

        adapterRecipes =  RecipeListAdapter(setCategories()  )
        rvRecipeList.apply {
            adapter = adapterRecipes
            layoutManager = GridLayoutManager(activity, 2)
        }
    }




}