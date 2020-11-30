package com.example.project
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.example.project.models.RestaurantData
import jp.wasabeef.glide.transformations.BlurTransformation

class RestaurantAdapter(
                            private val context: Context,
                            private val dataSource: ArrayList<RestaurantData>
) : RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {
    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var nameTextView: TextView
        lateinit var addressTextView: TextView
        lateinit var priceRangeTextView: TextView
        lateinit var thumbnailImageView: ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantAdapter.ViewHolder {
        val view = inflater.inflate(R.layout.list_item_restaurant, parent, false)

        val holder = RestaurantAdapter.ViewHolder(view)
        holder.thumbnailImageView = view.findViewById(R.id.restaurant_image) as ImageView
        holder.nameTextView = view.findViewById(R.id.restaurant_name) as TextView
        holder.addressTextView = view.findViewById(R.id.restaurant_address) as TextView
        holder.priceRangeTextView = view.findViewById(R.id.restaurant_price) as TextView

        return holder
    }

    override fun onBindViewHolder(holder: RestaurantAdapter.ViewHolder, position: Int) {
        // 1
        val restaurant = getItem(position) as RestaurantData

        holder.nameTextView.text = restaurant.name
        holder.addressTextView.text = restaurant.address
        holder.priceRangeTextView.text = restaurant.price.toString()
        Glide.with(context)
            .load("https://www.elitetraveler.com/wp-content/uploads/2007/02/Caelis_Barcelona_alta2A0200-1-730x450.jpg")
            .apply(bitmapTransform(BlurTransformation(25, 3)))
            .into(holder.thumbnailImageView)
    }

    private fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    fun addMoreItems(list : ArrayList<RestaurantData>){
        dataSource += list;
        Log.d("bla","ADDED! ${dataSource.size}")
        notifyDataSetChanged()
    }
}