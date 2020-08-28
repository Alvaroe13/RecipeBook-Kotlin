package com.example.recipereaderkotlin.views.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.recipereaderkotlin.R
import com.example.recipereaderkotlin.viewModels.RecipeListViewModel
import com.example.recipereaderkotlin.views.MainActivity
import kotlinx.android.synthetic.main.fragment_recipe_details.*

class RecipeDetailsFragment : Fragment(R.layout.fragment_recipe_details){

    private  lateinit var title: String
    private  lateinit var image: String
    private  lateinit var recipeId: String
    private lateinit var rating: String
    private lateinit var viewModel: RecipeListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //viewModel wired from activity
        viewModel = (activity as MainActivity).viewModel
        incomingData()

    }

    private fun incomingData(){
        if (arguments != null){
            title= arguments?.getString("title")!!
            image= arguments?.getString("image")!!
            rating = arguments?.getDouble("rating").toString()
            recipeId = arguments?.getString("recipeId")!!
            requestToServer(recipeId)
            setData(title,image,rating)
        }

    }

    private fun setData(title:String?,image:String?,rating:String?) {
        Glide.with(this).load(image).into(ivRecipeDetails)
        tvTitleRecipeDetails.text = title
        tvRatingRecipeDetails.text = rating
    }


    private fun requestToServer(recipeId: String){
        viewModel.getRecipeDetails(recipeId)
    }


}