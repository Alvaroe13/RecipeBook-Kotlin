package com.example.recipereaderkotlin.views.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.recipereaderkotlin.R
import kotlinx.android.synthetic.main.fragment_recipe_web.*

class RecipeWebFragment : Fragment(R.layout.fragment_recipe_web) {

    private lateinit var incomingUrl : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        incomingUrl = arguments?.getString("url")!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("RecipeWebFragment, url = $incomingUrl")
        showArticle()
    }

    private fun showArticle() {
        wvRecipe.apply {
            webViewClient = WebViewClient()
            loadUrl(incomingUrl)
        }
    }

}