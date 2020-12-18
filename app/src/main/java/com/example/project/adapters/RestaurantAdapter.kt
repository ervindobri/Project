package com.example.project.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.project.R
import com.example.project.models.RestaurantData
import jp.wasabeef.glide.transformations.BlurTransformation

class RestaurantAdapter(
    private val context: Context,
    private var selectedRestaurant: SelectedRestaurant
) : RecyclerView.Adapter<RestaurantAdapter.ViewHolder>(){

    interface SelectedRestaurant {
        fun showDetails(restaurant: RestaurantData)
        fun addToFavorites(restaurant: RestaurantData)
    }

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    private var mSortedList: ArrayList<RestaurantData> = arrayListOf()

//    private val mComparator: Comparator<RestaurantData> = comparator

    class ViewHolder(itemView: View):  RecyclerView.ViewHolder(itemView) {
        lateinit var nameTextView: TextView
        lateinit var addressTextView: TextView
        lateinit var priceRangeTextView: TextView
        lateinit var thumbnailImageView: ImageView
        lateinit var favoriteToggle : ToggleButton
        lateinit var card : CardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.list_item_restaurant, parent, false)

        val holder = ViewHolder(view)
        holder.thumbnailImageView = view.findViewById(R.id.restaurant_image) as ImageView
        holder.nameTextView = view.findViewById(R.id.restaurant_name) as TextView
        holder.addressTextView = view.findViewById(R.id.restaurant_address) as TextView
        holder.priceRangeTextView = view.findViewById(R.id.restaurant_price) as TextView
        holder.favoriteToggle = view.findViewById(R.id.favoriteToggle)

        holder.card = view.findViewById(R.id.restaurantCard)
        holder.card.setOnClickListener(View.OnClickListener {
//            selectedRestaurant.showDetails(holder.itemView.)
            Log.d("message", "onCREATE")

        })

        return holder
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.favoriteToggle.setOnCheckedChangeListener(null)
        super.onViewRecycled(holder)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 1
        val restaurant = getItem(position)

        holder.nameTextView.text = restaurant.name
        holder.addressTextView.text = restaurant.address
        holder.priceRangeTextView.text = restaurant.priceRange()

        holder.favoriteToggle.isChecked = getItem(holder.adapterPosition).favorite

        holder.favoriteToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            restaurant.favorite = isChecked
            holder.favoriteToggle.isChecked = isChecked
            selectedRestaurant.addToFavorites(restaurant)
        }

        Glide.with(context)
            .load(restaurant.image_url)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
            .into(holder.thumbnailImageView)

        holder.card.setOnClickListener { v ->
            selectedRestaurant.showDetails(restaurant)
        }

    }

    private fun getItem(position: Int): RestaurantData {
        return mSortedList[position]
    }

    override fun getItemCount(): Int {
        return mSortedList.size
    }


    fun setItems(list: ArrayList<RestaurantData>) {
        mSortedList = list
        notifyDataSetChanged()
    }

    fun clearItems() {
        mSortedList.clear()
        notifyDataSetChanged()
    }


}