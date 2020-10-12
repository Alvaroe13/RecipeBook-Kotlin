package com.example.recipereaderkotlin.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.recipereaderkotlin.R
import com.example.recipereaderkotlin.repositories.RecipeListRepository
import com.example.recipereaderkotlin.viewModels.RecipeViewModel
import com.example.recipereaderkotlin.viewModels.ViewModelProviderFactory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

   // lateinit var viewModel : RecipeViewModel

    private val viewModel : RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
     //  initViewModel()
    }

    /*
    fun initViewModel(){
        val repository  = RecipeListRepository()
       // val application = ContextApplication()
        val vmProviderFactory = ViewModelProviderFactory(repository,  application )
        viewModel = ViewModelProvider(this, vmProviderFactory).get(RecipeViewModel::class.java)
    }*/
}