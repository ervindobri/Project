package com.example.project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.project.R
import com.example.project.models.RestaurantData
import com.github.islamkhsh.CardSliderAdapter
import jp.wasabeef.glide.transformations.BlurTransformation

class FavoriteAdapter() : CardSliderAdapter<FavoriteAdapter.FavoriteViewHolder>() {

    private var restaurants : List<RestaurantData> = listOf()
    override fun getItemCount() = restaurants.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_favorite, parent, false)
        val holder = FavoriteViewHolder(view)
        holder.thumbnailImageView = view.findViewById(R.id.restaurant_image) as ImageView
        holder.nameTextView = view.findViewById(R.id.restaurant_name) as TextView
        holder.addressTextView = view.findViewById(R.id.restaurant_address) as TextView
        holder.priceRangeTextView = view.findViewById(R.id.restaurant_price) as TextView
        holder.card = view.findViewById(R.id.restaurantCard)
        return holder
    }

    override fun bindVH(holder: FavoriteViewHolder, position: Int) {
        //TODO bind item object with item layout view
        val restaurant = restaurants[position]
        holder.nameTextView.text = restaurant.name
        holder.addressTextView.text = restaurant.address
        holder.priceRangeTextView.text = restaurant.priceRange()
        Glide.with(holder.itemView.context)
            .load("https://www.elitetraveler.com/wp-content/uploads/2007/02/Caelis_Barcelona_alta2A0200-1-730x450.jpg")
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
            .into(holder.thumbnailImageView)
    }

    class FavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view){
        lateinit var nameTextView: TextView
        lateinit var addressTextView: TextView
        lateinit var priceRangeTextView: TextView
        lateinit var thumbnailImageView: ImageView

        lateinit var card : CardView
    }

    fun setData(list: List<RestaurantData>){
        this.restaurants = list
        notifyDataSetChanged()
    }

}