package com.example.project.fragments

import PaginationScrollListener
import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.RestaurantAdapter
import com.example.project.databinding.RestaurantListFragmentBinding
import com.example.project.models.ApiEndpoints
import com.example.project.models.ResponseData
import com.example.project.models.RestaurantData
import com.example.project.models.RetrofitClient
import com.example.project.vmodels.RestaurantListViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialFadeThrough
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantListFragment : Fragment(), SearchView.OnQueryTextListener   {

    private var searchString: String = ""
    private lateinit var binding : RestaurantListFragmentBinding
    private lateinit var searchView: SearchView
    private lateinit var progressBarLayout: LinearLayout
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var request: ApiEndpoints
    private lateinit var viewModel: RestaurantListViewModel

    lateinit var adapter : RestaurantAdapter
    lateinit var recyclerView : RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Restaurants"

        val your_layoutManager = recyclerView.layoutManager;
        recyclerView.addOnScrollListener(object : PaginationScrollListener(your_layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return viewModel.isLastPage
            }

            override fun isLoading(): Boolean {
                return viewModel.isLoading
            }

            override fun loadMoreItems() {
                viewModel.isLoading = true
                progressBar.visibility = View.VISIBLE
                Log.d("bla", "LOADING MORE ITEMS")
                //you have to call load more items to get more data
                getMoreItems()
            }
        })
        setClickListeners()

    }

    private fun getMoreItems() {
        request.getRestaurants(viewModel.currentPage).enqueue(object : Callback<ResponseData> {
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                if (response.isSuccessful) {
                    progressBar.visibility = View.GONE
                    if (response.body()!!.total_entries >= adapter.itemCount) {
                        var list = response.body()!!.restaurants
                        if (searchString != "") {
                            list = viewModel.filter(
                                response.body()!!.restaurants,
                                searchString
                            ) as ArrayList<RestaurantData>
                        }
                        Log.d("counter", viewModel.currentPage.toString())
                        adapter.addMoreItems(list)
                    } else {
                        Toast.makeText(view?.context, "No more restaurants to load!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                Toast.makeText(view?.context, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.currentPage++
        viewModel.isLoading = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val view: View = inflater.inflate(R.layout.restaurant_list_fragment, container, false)
        binding = RestaurantListFragmentBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true);
        enterTransition = MaterialFadeThrough()

        viewModel = ViewModelProvider(this).get(RestaurantListViewModel::class.java)
        viewModel.currentPage = 0
        progressBar = binding.progressBar
        progressBarLayout = binding.progressLayout
        progressBarLayout.setVerticalGravity(Gravity.CENTER_VERTICAL)
        progressBarLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL)

        request = RetrofitClient.buildService(ApiEndpoints::class.java)
        recyclerView = binding.root.findViewById(R.id.restaurantList)
        request.getRestaurants(viewModel.currentPage).enqueue(object : Callback<ResponseData>, RestaurantAdapter.SelectedRestaurant {
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                Log.d("logresponse", response.body().toString())
                if (response.isSuccessful) {
                    progressBarLayout.setVerticalGravity(Gravity.BOTTOM)
                    progressBar.visibility = View.GONE
                    adapter = RestaurantAdapter(
                        binding.root.context,
                        viewModel.nameComparator,
                        this
                    )
                    viewModel.oldList = response.body()!!.restaurants
                    adapter.addMoreItems(response.body()!!.restaurants)
                    recyclerView.adapter = adapter
                }
            }
            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                Toast.makeText(binding.root.context, "${t.message}", Toast.LENGTH_SHORT).show()
            }

            override fun showDetails(restaurant: RestaurantData) {
                findNavController().navigate(
                    RestaurantListFragmentDirections.actionRestaurantListFragmentToDetailFragment(restaurant)
                )
            }
        })
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.hasFixedSize()

        viewModel.currentPage++
        binding.filterLayout.visibility = View.GONE
        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RestaurantListViewModel::class.java)
    }

    private fun buildContainerTransformation() =
        MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
            duration = 300
            interpolator = FastOutSlowInInterpolator()
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
        }


    private fun setClickListeners() {

        binding.fabHome.setOnClickListener {
            val transition = buildContainerTransformation()
            transition.startView = binding.fabHome
            transition.endView = binding.filterLayout

            transition.addTarget(binding.filterLayout)

            TransitionManager.beginDelayedTransition(binding.root, transition)
            binding.filterLayout.visibility = View.VISIBLE
            binding.fabHome.visibility = View.INVISIBLE
        }
        binding.closeFilters.setOnClickListener{
            val transition = buildContainerTransformation()
            transition.startView = binding.filterLayout
            transition.endView = binding.fabHome

            transition.addTarget(binding.fabHome)

            TransitionManager.beginDelayedTransition(binding.root, transition)
            binding.filterLayout.visibility = View.GONE
            binding.fabHome.visibility = View.VISIBLE
        }
        binding.applyFilters.setOnClickListener{
            //Apply filters
            //TODO: APPLY FILTERS

            //Transition
            val transition = buildContainerTransformation()
            transition.startView = binding.filterLayout
            transition.endView = binding.fabHome
            transition.addTarget(binding.fabHome)

            TransitionManager.beginDelayedTransition(binding.root, transition)
            binding.filterLayout.visibility = View.GONE
            binding.fabHome.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu);
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                adapter.replaceItems(viewModel.oldList)
                recyclerView.scrollToPosition(0)
                // Do your stuff
                return false
            }
        })

        return (super.onCreateOptionsMenu(menu, inflater));
    }
    override fun onQueryTextChange(query: String): Boolean {
        // Here is where we are going to implement the filter logic
        val filteredModelList: ArrayList<RestaurantData> = viewModel.filter(viewModel.oldList, query) as ArrayList<RestaurantData>

        adapter.replaceItems(filteredModelList)
        searchString = query
        recyclerView.scrollToPosition(0)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

}
