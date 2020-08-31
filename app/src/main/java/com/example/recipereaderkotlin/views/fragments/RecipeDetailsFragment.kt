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
import com.example.recipereaderkotlin.viewModels.RecipeViewModel
import com.example.recipereaderkotlin.views.MainActivity
import kotlinx.android.synthetic.main.fragment_recipe_details.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class RecipeDetailsFragment : Fragment(R.layout.fragment_recipe_details) {

    private lateinit var title: String
    private lateinit var image: String
    private lateinit var recipeId: String
    private lateinit var rating: String
    private lateinit var viewModel: RecipeViewModel
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
        btnRetry()

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
                failLoadingInfoMessage("No internet connection...")
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
                    requestToServer(recipeId)
                }
            }
            if (job == null) {
                failLoadingInfoMessage("Error with the server...")
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


    private fun failLoadingInfoMessage(message: String) {
        println("RecipeDetailsFragment, failLoadingInfoMessage, called!")
        //use job timeout to make user experience better before showing error message in snackBar
        CoroutineScope(Main).launch {
            delay(JOB_TIMEOUT)
            hideProgressBar()
            makeVisible()
            btnRetryRecipeDetails.visibility = View.VISIBLE
            tvTitleRecipeDetails.text = message
        }

    }

    private fun subscribeObserver() {
        viewModel.recipeDetail.observe(viewLifecycleOwner, Observer { response ->
            println("RecipeDetailsFragment, subscribeObserver, response = $response ")
            when (response) {
                is Resource.Success -> {
                    if (response.data != null) {
                        setData(title, image, rating)
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
                    failLoadingInfoMessage("Error with the network")
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
        llIngredients.visibility = View.VISIBLE
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

    /**
     * button retries connection to the server when there was no internet in previous request
     */
    private fun btnRetry() {
        btnRetryRecipeDetails.setOnClickListener {
            btnRetryRecipeDetails.visibility = View.INVISIBLE
            ivRecipeDetails.visibility = View.INVISIBLE
            tvTitleRecipeDetails.visibility = View.INVISIBLE
            showProgressBar()
            incomingData()
            getRecipeDetails()
        }
    }


}







