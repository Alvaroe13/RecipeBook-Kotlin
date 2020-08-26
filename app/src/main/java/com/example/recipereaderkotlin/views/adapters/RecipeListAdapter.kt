package com.example.recipereaderkotlin.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.recipereaderkotlin.R
import com.example.recipereaderkotlin.models.Recipe
import com.example.recipereaderkotlin.models.RecipeCategories
import kotlinx.android.synthetic.main.categories_list_layout.view.*

class RecipeListAdapter(
    var recipeCategories : List<RecipeCategories>
): RecyclerView.Adapter<RecipeListAdapter.RecipeListViewHolder> () {

    inner class RecipeListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.categories_list_layout, parent, false)
        return RecipeListViewHolder(itemView)
    }

    override fun getItemCount() = recipeCategories.size

    override fun onBindViewHolder(holder: RecipeListViewHolder, position: Int) {
        holder.itemView.apply {

           tvRecipe.text = recipeCategories[position].title
           cvRecipe.setImageResource(recipeCategories[position].image)

        }
    }

}