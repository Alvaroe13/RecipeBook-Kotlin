package com.example.recipereaderkotlin.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipereaderkotlin.R
import com.example.recipereaderkotlin.models.Recipe
import kotlinx.android.synthetic.main.recipe_list_item_layout.view.*

class RecipeListAdapter(
    private val listener : ClickHandler //needed to handle click event from fragment
) : RecyclerView.Adapter<RecipeListAdapter.RecipeListViewHolder>() {


    inner class RecipeListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        //--------------------------- click event handler--------------------------------//

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
             val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.itemClick(position)
            }
        }

    }

    interface ClickHandler{
        fun itemClick(position: Int)
    }

    //-------------------------diff util------------------------------//

    private val differInfoCallback = object : DiffUtil.ItemCallback<Recipe>() {

        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.source_url == newItem.source_url
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.equals(newItem)
        }

    }

    val differAsync = AsyncListDiffer(this, differInfoCallback)


    //--------------------recycler override funcs ------------------------//


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recipe_list_item_layout,parent, false)
        return RecipeListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return differAsync.currentList.size
    }

    override fun onBindViewHolder(holder: RecipeListViewHolder, position: Int) {
        val recipeList = differAsync.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(recipeList.image_url).into(ivRecipeList)
            tvTitleRecipeList.text = recipeList.title
            tvAuthorRecipeList.text = recipeList.publisher
            tvRatingRecipeList.text = recipeList.social_rank.toString()
        }
    }


}