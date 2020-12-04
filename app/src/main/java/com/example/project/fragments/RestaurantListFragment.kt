package com.example.project.fragments

import PaginationScrollListener
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.view.Gravity.CENTER_VERTICAL
import android.view.Gravity.START
import android.view.inputmethod.InputMethodManager
import android.widget.*
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
import com.google.android.material.chip.Chip
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.google.android.material.transition.platform.MaterialSharedAxis
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RestaurantListFragment : Fragment(), SearchView.OnQueryTextListener, RestaurantAdapter.SelectedRestaurant {

    private lateinit var recyclerViewState: Parcelable
    private var  binding : RestaurantListFragmentBinding? = null
    private lateinit var searchView: SearchView
    private lateinit var progressBarLayout: LinearLayout
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var viewModel: RestaurantListViewModel

    var adapter : RestaurantAdapter? = null
    var recyclerView : RecyclerView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Restaurants"

        val your_layoutManager = recyclerView?.layoutManager;
        recyclerView?.addOnScrollListener(object : PaginationScrollListener(your_layoutManager as LinearLayoutManager) {

            override fun isLastPage(): Boolean {
                return viewModel.isLastPage
            }

            override fun isLoading(): Boolean {
                return viewModel.isLoading
            }

            override fun loadMoreItems() {
                if (viewModel.lastResponse.total_entries > adapter?.itemCount ?: 0) {
                    viewModel.isLoading = true
                    progressBar.visibility = View.VISIBLE
                    Log.d("bla", "LOADING MORE ITEMS")
                    //you have to call load more items to get more data
                    getMoreItems()
                }

            }
        })
        setClickListeners()
    }

    //TODO: refactor api call
    //TODO: fix filter layout

    private fun getMoreItems() {
        viewModel.request.filterRestaurants(viewModel.standardCountry, null, null, null, null, viewModel.currentPage).enqueue(
            object : Callback<ResponseData> {
                override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                    if (response.isSuccessful) {
                        progressBar.visibility = View.GONE
                        if (response.body()!!.total_entries >= adapter?.itemCount ?: 0) {
                            viewModel.lastResponse = response.body()!!
                            var list = response.body()!!.restaurants
                            if (viewModel.searchString != "") {
                                list = viewModel.filter(
                                    response.body()!!.restaurants,
                                    viewModel.searchString
                                ) as ArrayList<RestaurantData>
                            }
                            Log.d("counter", viewModel.currentPage.toString())
                            adapter?.addMoreItems(list)
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
        recyclerView?.scrollToPosition(0) //loading too many items quickfix
        viewModel.isLoading = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val view: View = inflater.inflate(R.layout.restaurant_list_fragment, container, false)
        binding = RestaurantListFragmentBinding.inflate(inflater, container, false)
//        if (  binding == null ) {
            viewModel = ViewModelProvider(this).get(RestaurantListViewModel::class.java)
            viewModel.currentPage=0
            setHasOptionsMenu(true);
            enterTransition = MaterialFadeThrough()
            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false).apply {
                duration = 80L
            }

            for (i in 1..4){
                val chip = inflater.inflate(R.layout.item_chip_choice,  binding!!.priceGroup, false) as Chip
                chip.text = "$".repeat(i)
                chip.setId(i);
                chip.setTag(i);
                chip.setCheckable(true);
                chip.textAlignment = CENTER_VERTICAL
                binding!!.priceGroup.addView(chip)
            }
            binding!!.priceGroup.check(1)


            val cadapter = ArrayAdapter(
                context!!,
                R.layout.country_menu_item,
                viewModel.countryMap.toList().map { it.second }
            )
            val editTextFilledExposedDropdown: AutoCompleteTextView =  binding!!.filledExposedDropdown
            editTextFilledExposedDropdown.setAdapter(cadapter)

            progressBar =  binding!!.progressBar
            progressBarLayout =  binding!!.progressLayout
            progressBarLayout.setVerticalGravity(Gravity.CENTER_VERTICAL)
            progressBarLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL)
            recyclerView =  binding!!.restaurantList

            if ( !viewModel.filtering){
                viewModel.request.filterRestaurants(viewModel.standardCountry, null, null, null, null, viewModel.currentPage).enqueue(
                    object : Callback<ResponseData>,
                        RestaurantAdapter.SelectedRestaurant {
                        override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                            Log.d("logresponse", response.body().toString())
                            if (response.isSuccessful) {
                                viewModel.lastResponse = response.body()!!
                                progressBarLayout.setVerticalGravity(Gravity.BOTTOM)
                                progressBar.visibility = View.GONE
                                adapter = RestaurantAdapter(
                                    binding!!.root.context,
                                    viewModel.nameComparator,
                                    this
                                )
                                viewModel.oldList = response.body()!!.restaurants
                                adapter!!.addMoreItems(response.body()!!.restaurants)
                                recyclerView!!.adapter = adapter
                            }
                        }

                        override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                            Toast.makeText( binding!!.root.context, "${t.message}", Toast.LENGTH_SHORT).show()
                        }

                        override fun showDetails(restaurant: RestaurantData) {
                            findNavController().navigate(
                                RestaurantListFragmentDirections.actionRestaurantListFragmentToDetailFragment(restaurant)
                            )
                        }
                    })
            }
        else{
                getFilteredItems()
        }

            viewModel.currentPage++
            binding!!.filterLayout.visibility = View.GONE
            recyclerView!!.layoutManager = LinearLayoutManager(this.context)
            recyclerView!!.hasFixedSize()
            recyclerViewState = recyclerView?.layoutManager?.onSaveInstanceState()!!
            adapter?.notifyDataSetChanged()
//        }
//        else{
//            recyclerView?.layoutManager?.onRestoreInstanceState(recyclerViewState)
            Log.d("size", "Size-" + adapter?.itemCount.toString())

//        }
        return  binding!!.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RestaurantListViewModel::class.java)
        adapter = RestaurantAdapter(view!!.context, viewModel.nameComparator, this)
        recyclerView?.adapter = adapter
    }

    private fun buildContainerTransformation() =
        MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
            duration = 300
            interpolator = FastOutSlowInInterpolator()
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
        }


//   TODO:display current filters on top of list

    private fun setClickListeners() {
         binding!!.fabHome.setOnClickListener {
            //Load back current filters if there are
            if ( viewModel.filters.isNotEmpty()){
                 binding!!.priceGroup.check(viewModel.filters[1].toInt())
                 binding!!.addressTextField.editText?.setText(viewModel.filters[2])
                 binding!!.cityTextField.editText?.setText(viewModel.filters[3])
                 binding!!.zipCodeTextField.editText?.setText(viewModel.filters[4])
            }
             binding!!.filledExposedDropdown.setAdapter (ArrayAdapter(
                 context!!,
                 R.layout.country_menu_item,
                 viewModel.countryMap.toList().map { it.second }
             ))

            val transition = buildContainerTransformation()
            transition.startView =  binding!!.fabHome
            transition.endView =  binding!!.filterLayout
            transition.addTarget( binding!!.filterLayout)

            TransitionManager.beginDelayedTransition( binding!!.root, transition)
             binding!!.filterLayout.visibility = View.VISIBLE
             binding!!.fabHome.visibility = View.INVISIBLE
        }
         binding!!.closeFilters.setOnClickListener{
            val transition = buildContainerTransformation()
            transition.startView =  binding!!.filterLayout
            transition.endView =  binding!!.fabHome

            transition.addTarget( binding!!.fabHome)

            TransitionManager.beginDelayedTransition( binding!!.root, transition)
             binding!!.filterLayout.visibility = View.GONE
             binding!!.fabHome.visibility = View.VISIBLE
        }
         binding!!.applyFilters.setOnClickListener{
            //Apply filters
            //TODO: APPLY FILTERS
            Log.d(
                "filters", ( binding!!.priceGroup.checkedChipId.toString() + ", "
                        +  binding!!.filledExposedDropdown.text + ", "
                        +  binding!!.cityTextField.editText?.text.toString() + ", "
                        +  binding!!.addressTextField.editText?.text.toString() + ", "
                        +  binding!!.zipCodeTextField.editText?.text.toString() //zip code
                        )
            )
            val filters = listOf<String>(
                 binding!!.filledExposedDropdown.text.toString(), //country
                 binding!!.priceGroup.checkedChipId.toString(), //price
                 binding!!.addressTextField.editText?.text.toString(), //address
                 binding!!.cityTextField.editText?.text.toString(), //city
                 binding!!.zipCodeTextField.editText?.text.toString() //zip code
            )
            Log.d("filtersize", filters.size.toString())
            viewModel.filters = filters
            viewModel.filtering = true
            viewModel.currentPage = 0
            getFilteredItems();
             binding!!.root.hideKeyboard()
            //Transition
            val transition = buildContainerTransformation()
            transition.startView =  binding!!.filterLayout
            transition.endView =  binding!!.fabHome
            transition.addTarget( binding!!.fabHome)

            TransitionManager.beginDelayedTransition( binding!!.root, transition)
             binding!!.filterLayout.visibility = View.GONE
             binding!!.fabHome.visibility = View.VISIBLE
        }
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun getFilteredItems() {
        viewModel.request.filterRestaurants(
            viewModel.countryMap.filterValues { it == viewModel.filters[0] }.keys.first(), //country
            viewModel.filters[1].toInt(),  //price
            viewModel.filters[2], //address
            viewModel.filters[3], //city
            if (viewModel.filters[4] != "") viewModel.filters[4].toInt() else null, //zipcode,
            viewModel.currentPage
        ).enqueue(object : Callback<ResponseData>,
            RestaurantAdapter.SelectedRestaurant {
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                Log.d("logresponse", response.body().toString())
                if (response.isSuccessful) {
                    viewModel.lastResponse = response.body()!!

                    progressBarLayout.setVerticalGravity(Gravity.BOTTOM)
                    progressBar.visibility = View.GONE
                    adapter = RestaurantAdapter(
                         binding!!.root.context,
                        viewModel.nameComparator,
                        this
                    )
                    viewModel.oldList = response.body()!!.restaurants
                    adapter!!.addMoreItems(response.body()!!.restaurants)
                    recyclerView?.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                Toast.makeText( binding!!.root.context, "${t.message}", Toast.LENGTH_SHORT).show()
            }

            override fun showDetails(restaurant: RestaurantData) {
                findNavController().navigate(
                    RestaurantListFragmentDirections.actionRestaurantListFragmentToDetailFragment(restaurant)
                )
            }
        })
        viewModel.currentPage++
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu);
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                adapter?.replaceItems(viewModel.oldList)
                recyclerView?.scrollToPosition(0)
                // Do your stuff
                return false
            }
        })

        return (super.onCreateOptionsMenu(menu, inflater));
    }
    override fun onQueryTextChange(query: String): Boolean {
        // Here is where we are going to implement the filter logic
        val filteredModelList: ArrayList<RestaurantData> = viewModel.filter(viewModel.oldList, query) as ArrayList<RestaurantData>

        adapter?.replaceItems(filteredModelList)
        viewModel.searchString = query
        recyclerView?.scrollToPosition(0)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun showDetails(restaurant: RestaurantData) {
        findNavController().navigate(
            RestaurantListFragmentDirections.actionRestaurantListFragmentToDetailFragment(restaurant)
        )
    }

    override fun onDestroyView() {
        recyclerViewState = recyclerView?.layoutManager?.onSaveInstanceState()!!
        super.onDestroyView()
    }

}
