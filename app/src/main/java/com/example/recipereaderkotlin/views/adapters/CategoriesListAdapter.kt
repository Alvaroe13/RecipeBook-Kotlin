package com.example.recipereaderkotlin.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipereaderkotlin.R
import com.example.recipereaderkotlin.models.RecipeCategories
import kotlinx.android.synthetic.main.categories_list_layout.view.*

class CategoriesListAdapter(
    private val recipeCategories : List<RecipeCategories>,
    private val clickListener : ClickHandler
): RecyclerView.Adapter<CategoriesListAdapter.RecipeListViewHolder> () {

    inner class RecipeListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        init{
            //to be able to handle clicks in fragment
            itemView.setOnClickListener(this)
        }

        //to be able to handle clicks in fragment
        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                clickListener.itemClick(position)
            }
        }
    }

    //to be able to handle clicks in fragment
    interface ClickHandler{
        fun itemClick(position : Int)
    }


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