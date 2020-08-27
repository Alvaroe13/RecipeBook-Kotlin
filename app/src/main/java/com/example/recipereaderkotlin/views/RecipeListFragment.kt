package com.example.recipereaderkotlin.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.recipereaderkotlin.R
import kotlinx.android.synthetic.main.fragment_recipe_list2.*


class RecipeListFragment : Fragment(R.layout.fragment_recipe_list2) {

    lateinit var incomingInfo : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        incomingInfo = arguments?.getString("CategoryClicked")!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //toolbar title
        tvToolbarTitle.text = incomingInfo

    }

}