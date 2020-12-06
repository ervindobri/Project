package com.example.project.fragments

import com.example.project.helpers.PaginationScrollListener
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.view.Gravity.CENTER_VERTICAL
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
import com.example.project.adapters.RestaurantAdapter
import com.example.project.databinding.RestaurantListFragmentBinding

import com.example.project.models.RestaurantData
import com.example.project.vmodels.RestaurantListViewModel
import com.example.project.vmodels.RestaurantListViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.google.android.material.transition.platform.MaterialSharedAxis


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
                progressBar.visibility =  if(viewModel.isLoading) View.VISIBLE else View.GONE
                return viewModel.isLoading
            }

            override fun loadMoreItems() {
                //TODO: fix pagination
                if (viewModel.lastResponse.total_entries > adapter?.itemCount ?: 0) {
                    viewModel.isLoading = true
                    Log.d("bla", "LOADING MORE ITEMS")
                    getMoreItems()
                    viewModel.restaurants.observe(viewLifecycleOwner, { adapter!!.setItems(it)})
                    viewModel.isLoading = false
                }

            }
        })
        setClickListeners()
    }

    private fun getMoreItems() {
        viewModel.currentPage++
        viewModel.getRestaurants()
        recyclerView?.scrollToPosition(0) //loading too many items quickfix
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RestaurantListFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, RestaurantListViewModelFactory(requireActivity().application)).get(RestaurantListViewModel::class.java)
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
        progressBarLayout.setVerticalGravity(CENTER_VERTICAL)
        progressBarLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL)
        recyclerView =  binding!!.restaurantList
        progressBarLayout.setVerticalGravity(Gravity.BOTTOM)
        progressBar.visibility = viewModel.progressVisibility
        binding?.emptyLayout?.visibility = View.GONE

        adapter = RestaurantAdapter(
            binding!!.root.context,
            this
        )
        viewModel.restaurants.observe(viewLifecycleOwner, { adapter!!.setItems(it)})
        recyclerView!!.adapter = adapter

        viewModel.currentPage++
        binding!!.filterLayout.visibility = View.GONE
        recyclerView!!.layoutManager = LinearLayoutManager(this.context)
        recyclerView!!.hasFixedSize()
        recyclerViewState = recyclerView?.layoutManager?.onSaveInstanceState()!!
        adapter?.notifyDataSetChanged()
        Log.d("onviewcreate", "Adapter Size-" + adapter?.itemCount.toString())
        return  binding!!.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RestaurantListViewModel::class.java)
        adapter = RestaurantAdapter(view!!.context,this)
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
                 binding!!.priceGroup.check(viewModel.filters["price"].toString().toInt())
                 binding!!.addressTextField.editText?.setText(viewModel.filters["address"].toString())
                 binding!!.cityTextField.editText?.setText(viewModel.filters["city"].toString())
                 binding!!.zipCodeTextField.editText?.setText(viewModel.filters["zip"].toString())
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
            Log.d(
                "filters", ( binding!!.priceGroup.checkedChipId.toString() + ", "
                        +  binding!!.filledExposedDropdown.text + ", "
                        +  binding!!.cityTextField.editText?.text.toString() + ", "
                        +  binding!!.addressTextField.editText?.text.toString() + ", "
                        +  binding!!.zipCodeTextField.editText?.text.toString()
                        )
            )
            viewModel.setFilters(
                binding!!.filledExposedDropdown.text.toString(), //country
                "",
                binding!!.priceGroup.checkedChipId, //price
                binding!!.addressTextField.editText?.text.toString(), //address
                binding!!.cityTextField.editText?.text.toString(), //city
                binding!!.zipCodeTextField.editText?.text.toString(), //zip code
                1
            )
            viewModel.filtering = true
            viewModel.currentPage = 0
            getMoreItems();
            viewModel.restaurants.observe(viewLifecycleOwner, { adapter!!.setItems(it)})


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
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu);
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
//        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
//            override fun onClose(): Boolean {
////                adapter?.replaceItems(viewModel.oldList)
//                recyclerView?.scrollToPosition(0)
//                // Do your stuff
//                return false
//            }
//        })

        return (super.onCreateOptionsMenu(menu, inflater));
    }

    override fun onQueryTextChange(query: String): Boolean {
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        viewModel.setFilters(
            viewModel.standardCountry,
            query ?: "",null,"","","",
            1)
        viewModel.getRestaurants()
        recyclerView?.scrollToPosition(0)
        return true
    }

    override fun showDetails(restaurant: RestaurantData) {
        findNavController().navigate(
            RestaurantListFragmentDirections.actionRestaurantListFragmentToDetailFragment(restaurant)
        )
    }

    override fun addToFavorites(restaurant: RestaurantData) {
        viewModel.addToFavorites(restaurant)
    }

    override fun onDestroyView() {
        recyclerViewState = recyclerView?.layoutManager?.onSaveInstanceState()!!
        super.onDestroyView()
    }

}
