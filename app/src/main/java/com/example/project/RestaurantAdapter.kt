package com.example.project
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.example.project.models.RestaurantData
import jp.wasabeef.glide.transformations.BlurTransformation


class RestaurantAdapter(
    private val context: Context,
    comparator: Comparator<RestaurantData>,
    private var selectedRestaurant: SelectedRestaurant
) : RecyclerView.Adapter<RestaurantAdapter.ViewHolder>(){

    public interface SelectedRestaurant {
        fun showDetails(restaurant: RestaurantData)
    }

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private val mSortedList: SortedList<RestaurantData> =
        SortedList(RestaurantData::class.java, object : SortedList.Callback<RestaurantData>() {
            override fun compare(a: RestaurantData, b: RestaurantData): Int {
                return mComparator.compare(a, b)
            }

            override fun onInserted(position: Int, count: Int) {
                notifyItemRangeInserted(position, count)
            }

            override fun onRemoved(position: Int, count: Int) {
                notifyItemRangeRemoved(position, count)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                notifyItemMoved(fromPosition, toPosition)
            }

            override fun onChanged(position: Int, count: Int) {
                notifyItemRangeChanged(position, count)
            }

            override fun areContentsTheSame(oldItem: RestaurantData, newItem: RestaurantData): Boolean {
                return oldItem.equals(newItem)
            }

            override fun areItemsTheSame(item1: RestaurantData, item2: RestaurantData): Boolean {
                return item1.getId() == item2.getId()
            }
        })
    private val mComparator: Comparator<RestaurantData> = comparator

    class ViewHolder(itemView: View):  RecyclerView.ViewHolder(itemView) {
        lateinit var nameTextView: TextView
        lateinit var addressTextView: TextView
        lateinit var priceRangeTextView: TextView
        lateinit var thumbnailImageView: ImageView

        lateinit var card : CardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.list_item_restaurant, parent, false)

        val holder = ViewHolder(view)
        holder.thumbnailImageView = view.findViewById(R.id.restaurant_image) as ImageView
        holder.nameTextView = view.findViewById(R.id.restaurant_name) as TextView
        holder.addressTextView = view.findViewById(R.id.restaurant_address) as TextView
        holder.priceRangeTextView = view.findViewById(R.id.restaurant_price) as TextView
        holder.card = view.findViewById(R.id.restaurantCard)
        holder.card.setOnClickListener(View.OnClickListener {
//            selectedRestaurant.showDetails(holder.itemView.)
            Log.d("message", "onCREATE")

        })
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 1
        val restaurant = getItem(position) as RestaurantData

        holder.nameTextView.text = restaurant.name
        holder.addressTextView.text = restaurant.address
        holder.priceRangeTextView.text = restaurant.priceRange()
        Glide.with(context)
            .load("https://www.elitetraveler.com/wp-content/uploads/2007/02/Caelis_Barcelona_alta2A0200-1-730x450.jpg")
            .apply(bitmapTransform(BlurTransformation(25, 3)))
            .into(holder.thumbnailImageView)

        holder.card.setOnClickListener { v ->
            Log.d("message", "Clicked!")
            selectedRestaurant.showDetails(restaurant)
        }

    }

    private fun getItem(position: Int): Any {
        return mSortedList[position]
    }

    override fun getItemCount(): Int {
        return mSortedList.size()
    }

    fun replaceItems(models: ArrayList<RestaurantData>){
        mSortedList.beginBatchedUpdates()
        for (i in mSortedList.size() - 1 downTo 0) {
            val model: RestaurantData = mSortedList[i]
            if (!models.contains(model)) {
                mSortedList.remove(model)
            }
        }
        mSortedList.addAll(models)
        mSortedList.endBatchedUpdates()
        notifyDataSetChanged()
    }

    fun addMoreItems(list: ArrayList<RestaurantData>){
        mSortedList.beginBatchedUpdates()
        mSortedList.addAll(list)
        mSortedList.endBatchedUpdates()
        notifyDataSetChanged()
    }

    fun getItems(): SortedList<RestaurantData> {
        return mSortedList
    }


}