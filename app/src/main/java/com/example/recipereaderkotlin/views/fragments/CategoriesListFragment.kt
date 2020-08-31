package com.example.recipereaderkotlin.views.fragments

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
import com.example.recipereaderkotlin.viewModels.RecipeViewModel
import com.example.recipereaderkotlin.views.MainActivity
import com.example.recipereaderkotlin.views.adapters.CategoriesListAdapter
import kotlinx.android.synthetic.main.fragment_categories_list.*

class CategoriesListFragment : Fragment(R.layout.fragment_categories_list), CategoriesListAdapter.ClickHandler {

    private lateinit var navController : NavController
    private lateinit var adapterRecipes : CategoriesListAdapter
    private lateinit var viewModel:RecipeViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //viewModel wired from activity
        viewModel = (activity as MainActivity).viewModel
        //nav component
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
        adapterRecipes =  CategoriesListAdapter(setCategories(), this )
        rvCategoriesList.apply {
            adapter = adapterRecipes
            layoutManager = GridLayoutManager(activity, 2)
        }
    }

    private fun openCategory(title : String){
        val bundle = bundleOf("CategoryClicked" to title )
        findNavController().navigate(R.id.action_recipeListFragment_to_recipeListFragment2, bundle)
    }


    /**
     * here we handle click event in items
     */
    override fun itemClick(position: Int) {
        val categoryClicked = setCategories()[position]
        println("CategoriesListFragment, Item $position with name ${categoryClicked.title}")
        openCategory(categoryClicked.title)
    }


}