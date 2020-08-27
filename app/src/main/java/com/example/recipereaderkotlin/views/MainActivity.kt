package com.example.recipereaderkotlin.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.recipereaderkotlin.R
import com.example.recipereaderkotlin.repositories.RecipeListRepository
import com.example.recipereaderkotlin.viewModels.RecipeListViewModel
import com.example.recipereaderkotlin.viewModels.ViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    lateinit var viewModel : RecipeListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       initViewModel()
    }

    fun initViewModel(){
        val repository  = RecipeListRepository()
        val vmProviderFactory = ViewModelProviderFactory(repository)
        viewModel = ViewModelProvider(this, vmProviderFactory).get(RecipeListViewModel::class.java)
    }
}