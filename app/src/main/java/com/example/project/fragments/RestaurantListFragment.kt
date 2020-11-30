package com.example.project.fragments

import PaginationScrollListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.RestaurantAdapter
import com.example.project.models.ApiEndpoints
import com.example.project.models.ResponseData
import com.example.project.models.RestaurantData
import com.example.project.models.RetrofitClient
import com.example.project.vmodels.RestaurantListViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantListFragment : Fragment() {



    companion object {
        private var PAGE_START : Int = 0;
//        private var isLoading = false
//        private const val isLastPage = false
//        private const val TOTAL_PAGES = 3 //your total page
        private var currentPage = PAGE_START
        fun newInstance() = RestaurantListFragment()
    }

    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var request: ApiEndpoints
    private lateinit var viewModel: RestaurantListViewModel

    lateinit var adapter : RestaurantAdapter
    lateinit var recyclerView : RecyclerView
    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Restaurants"

        val your_layoutManager = recyclerView.layoutManager;
        recyclerView.addOnScrollListener(object : PaginationScrollListener(your_layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                progressBar.visibility = View.VISIBLE
                Log.d("bla","LOADING MORE ITEMS")
                //you have to call load more items to get more data
                getMoreItems()
            }
        })
    }

    private fun getMoreItems() {
        request.getRestaurants(currentPage).enqueue(object : Callback<ResponseData> {
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                if (response.isSuccessful) {
                    progressBar.visibility = View.GONE
                    if ( response.body()!!.total_entries >= adapter.itemCount ){
                        adapter.addMoreItems(response.body()!!.restaurants)
                    }
                    else{
                        Toast.makeText(view?.context, "No more restaurants to load!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                Toast.makeText(view?.context, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
        currentPage++
        isLoading = false
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.restaurant_list_fragment, container, false)

        request = RetrofitClient.buildService(ApiEndpoints::class.java)
        recyclerView = view.findViewById<RecyclerView>(R.id.restaurantList)
        progressBar = view.findViewById<CircularProgressIndicator>(R.id.progress_bar)

        request.getRestaurants(currentPage).enqueue(object : Callback<ResponseData> {
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                if (response.isSuccessful) {
                    progressBar.visibility = View.GONE
                    adapter = RestaurantAdapter(view.context, response.body()!!.restaurants)
                    recyclerView.adapter = adapter
                }
            }
            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                Toast.makeText(view.context, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.hasFixedSize()
        currentPage++
        return view
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RestaurantListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
