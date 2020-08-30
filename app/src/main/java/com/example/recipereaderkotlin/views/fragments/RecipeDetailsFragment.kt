package com.example.recipereaderkotlin.views.fragments

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.recipereaderkotlin.R
import com.example.recipereaderkotlin.utils.Constants
import com.example.recipereaderkotlin.utils.Resource
import com.example.recipereaderkotlin.viewModels.RecipeListViewModel
import com.example.recipereaderkotlin.views.MainActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_recipe_details.*
import kotlinx.coroutines.*

class RecipeDetailsFragment : Fragment(R.layout.fragment_recipe_details) {

    private lateinit var title: String
    private lateinit var image: String
    private lateinit var recipeId: String
    private lateinit var rating: String
    private lateinit var viewModel: RecipeListViewModel
    private lateinit var layout: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //viewModel wired from activity
        viewModel = (activity as MainActivity).viewModel
        //without this we can't launch SnackBar when handling network time out
        layout = view
        incomingData()
    }

    private fun incomingData() {
        if (arguments != null) {
            title = arguments?.getString("title")!!
            image = arguments?.getString("image")!!
            rating = arguments?.getDouble("rating").toString()
            recipeId = arguments?.getString("recipeId")!!

            //we set info in ui but we handle connection time out as well
            CoroutineScope(Dispatchers.IO).launch {
                secureRecipeDetailsRetrieval(title, image, rating)
            }

        }

    }

    /**
     * actual connection with server in order to request ingredients details
     */
    private fun requestToServer(recipeId: String) {
        viewModel.getRecipeDetails(recipeId)
        subscribeObserver()
    }

    private fun subscribeObserver() {
        viewModel.recipeDetail.observe(viewLifecycleOwner, Observer { response ->
            println("RecipeDetailsFragment, subscribeObserver, response = $response ")
            when (response) {
                is Resource.Success -> {
                    if (response.data != null) {
                        //erase ingredients from the view
                        llIngredients.removeAllViews()
                        //add ingredients to the view
                        for (i: String in response.data.recipe.recipeDetails) {
                            println("debugging, value of i = $i")
                            setIngredientList(i)
                            hideProgressBar()
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    println("RecipeListFragment, Error = ${response.message}")
                }
                is Resource.Loading -> {
                    showProgressBar()
                    println("RecipeListFragment, loading state...")
                }
            }
        })
    }

    private fun showProgressBar() {
        println("RecipeListFragment, progressBar show")
        pbRecipeDetails.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        println("RecipeListFragment, progressBar hide")
        pbRecipeDetails.visibility = View.INVISIBLE
    }


    /**
     * this one handles network timeout, it will only get triggered if network request takes longer than 3 secs
     */
    private suspend fun secureRecipeDetailsRetrieval(title: String?,image: String?,rating: String?) {
        withContext(Dispatchers.IO) {
            val job = withTimeoutOrNull(Constants.JOB_TIMEOUT) {
                withContext(Dispatchers.Main) {
                    println("RecipeListFragment, withContext called")
                    setData(title, image, rating)
                    requestToServer(recipeId)
                    hideProgressBar()
                }
            }
            if (job == null) {
                withContext(Dispatchers.Main) {
                    hideProgressBar()
                    ivRecipeDetails.visibility = View.VISIBLE
                    tvTitleRecipeDetails.visibility = View.VISIBLE
                    tvTitleRecipeDetails.text = "Error with the server..."
                    Snackbar.make(layout, "Something went wrong, try again!", Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    /**
     * we set the ingredients coming from the server request
     */
    private fun setIngredientList(i : String){

        val textField = TextView(context)
        textField.text = i
        textField.textSize = 15F
        val textLayout = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        textField.layoutParams = textLayout
        llIngredients.addView(textField)
        makeVisible()
    }

    /**
     * we set image, social rank and title with incoming data from RecipeListFragment
     */
    private fun setData(title: String?, image: String?, rating: String?) {
        Glide.with(this).load(image).into(ivRecipeDetails)
        tvTitleRecipeDetails.text = title
        tvRatingRecipeDetails.text = rating
    }

    /**
     * all views are invisible by default so we make them visible by using this method
     */
    private fun makeVisible() {
        ivRecipeDetails.visibility = View.VISIBLE
        tvTitleRecipeDetails.visibility = View.VISIBLE
        tvRatingRecipeDetails.visibility = View.VISIBLE
        tvIngredientsDetails.visibility = View.VISIBLE
        llIngredients.visibility = View.VISIBLE
    }


}







