package com.example.recipereaderkotlin.views.fragments

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.recipereaderkotlin.R
import com.example.recipereaderkotlin.utils.Constants.Companion.JOB_TIMEOUT
import com.example.recipereaderkotlin.utils.Resource
import com.example.recipereaderkotlin.viewModels.RecipeListViewModel
import com.example.recipereaderkotlin.views.MainActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_recipe_details.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class RecipeDetailsFragment : Fragment(R.layout.fragment_recipe_details) {

    private lateinit var title: String
    private lateinit var image: String
    private lateinit var recipeId: String
    private lateinit var rating: String
    private lateinit var viewModel: RecipeListViewModel
    private lateinit var layout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        incomingData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //viewModel wired from activity
        viewModel = (activity as MainActivity).viewModel
        //without this we can't launch SnackBar when handling network time out
        layout = view
        getRecipeDetails()

    }

    private fun incomingData() {
        if (arguments != null) {
            title = arguments?.getString("title")!!
            image = arguments?.getString("image")!!
            rating = arguments?.getDouble("rating").toString()
            recipeId = arguments?.getString("recipeId")!!
            println("RecipeListFragment, incomingData called!")
        }
    }

    /**
     * this method is the one putting the connection to the server in motion
     */
    private fun getRecipeDetails() {

        CoroutineScope(IO).launch {
            val hasConnection = viewModel.checkInternetConnection()
            if (hasConnection) {
                secureRecipeDetailsRetrieval()
            } else {
                noInternetConnection()
            }
        }

    }


    /**
     * this one handles network timeout, it will only get triggered if network request takes longer than 3 secs
     */
    private suspend fun secureRecipeDetailsRetrieval() {


        withContext(IO) {
            val job = withTimeoutOrNull(JOB_TIMEOUT) {
                withContext(Main) {
                    println("RecipeListFragment, withContext called")
                    setData(title, image, rating)
                    requestToServer(recipeId)
                }
            }
            if (job == null) {
                withContext(Main) {
                    // hideProgressBar()
                    ivRecipeDetails.visibility = View.VISIBLE
                    tvTitleRecipeDetails.visibility = View.VISIBLE
                    tvTitleRecipeDetails.text = "Error with the server..."
                    Snackbar.make(layout, "Something went wrong, try again!", Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }


    }

    private suspend fun noInternetConnection() {
        println("RecipeDetailsFragment, NO internet connection")
        //use job timeout to make user experience better before showing error message in snackBar
        delay(JOB_TIMEOUT)
        withContext(Main){
            hideProgressBar()
            ivRecipeDetails.visibility = View.VISIBLE
            tvTitleRecipeDetails.visibility = View.VISIBLE
            tvTitleRecipeDetails.text = "No internet connection..."
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
                        }
                        hideProgressBar()
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
     * we set the ingredients coming from the server request
     */
    private fun setIngredientList(i: String) {

        val textField = TextView(context)
        textField.text = i
        textField.textSize = 15F
        val textLayout = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
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







