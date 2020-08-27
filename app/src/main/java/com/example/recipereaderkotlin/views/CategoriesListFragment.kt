package com.example.recipereaderkotlin.views

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipereaderkotlin.R
import com.example.recipereaderkotlin.models.RecipeCategories
import com.example.recipereaderkotlin.views.adapters.RecipeListAdapter
import kotlinx.android.synthetic.main.fragment_categories_list.*

class CategoriesListFragment : Fragment(R.layout.fragment_categories_list), RecipeListAdapter.ClickHandler {

    lateinit var navController : NavController
    lateinit var adapterRecipes : RecipeListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        initRecyclerView()

    }

    /**
     * categories to show in the firstView
     */
    private fun setCategories(): MutableList<RecipeCategories>{

        return mutableListOf(
            RecipeCategories( "Barbecue", R.drawable.barbecue  ),
            RecipeCategories( "Beef", R.drawable.beef ),
            RecipeCategories(  "Breakfast", R.drawable.breakfast ),
            RecipeCategories("Brunch", R.drawable.brunch),
            RecipeCategories( "Chicken", R.drawable.chicken),
            RecipeCategories("Dinner",R.drawable.dinner  ),
            RecipeCategories("Italian",  R.drawable.italian),
            RecipeCategories(  "Wine", R.drawable.wine)
        )
    }
    private fun initRecyclerView() {

        adapterRecipes =  RecipeListAdapter(setCategories(), this )
        rvRecipeList.apply {
            adapter = adapterRecipes
            layoutManager = GridLayoutManager(activity, 2)
        }
    }

    //here we handle click event in items
    override fun itemClick(position: Int) {

        val categoryClicked = setCategories()[position]
        println("CategoriesListFragment, Item $position with name ${categoryClicked.title}")
        openCategory(categoryClicked.title)

    }

    private fun openCategory(title : String){

        val bundle = bundleOf("CategoryClicked" to title )
        findNavController().navigate(R.id.action_recipeListFragment_to_recipeListFragment2, bundle)

    }




}