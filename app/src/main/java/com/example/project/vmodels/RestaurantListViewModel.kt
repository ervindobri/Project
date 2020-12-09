package com.example.project.vmodels

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.project.api.RetrofitClient
import com.example.project.models.*
import com.example.project.database.RestaurantDatabase
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RestaurantListViewModel(application: Application) : AndroidViewModel(application) {
    var progressVisibility: Int = View.GONE
    val standardCountryCode: String = "MX"
    val standardCountry: String = "Mexico"

    val request = RetrofitClient.api

    var searchString: String = ""

    var position: Int = 0
    private var PAGE_START : Int = 0;
    var currentPage = PAGE_START
    var isLastPage: Boolean = false

    var isLoading: Boolean = false

    var filtering : Boolean = false

    var filters: HashMap<String,Any?> = hashMapOf(
        "country" to standardCountryCode,
        "name" to "",
        "price" to 1,
        "address" to "",
        "city" to "",
        "zip" to "",
        "page" to currentPage
    )


    var oldList : ArrayList<RestaurantData> = ArrayList()

         var countryMap : MutableMap<String,String> = hashMapOf(
            "AE" to "United Arab Emirates",
            "AW" to "Aruba",
            "CA" to "Canada",
            "CH" to  "Switzerland" ,
            "CN" to  "China" ,
            "CR" to  "Costa Rica",
            "HK" to  "Hong Kong" ,
            "KN" to  "Saint Kitts and Nevis"  ,
            "KY" to  "Cayman Islands" ,
            "MC" to  "Monaco" ,
            "MO" to  "Macao" ,
            "MX" to  "Mexico" ,
            "MY" to  "Malaysia" ,
            "PT" to  "Portugal" ,
            "SA" to  "Saudi Arabia" ,
            "SG" to  "Singapore" ,
            "SV" to  "El Salvador" ,
            "US" to  "United States" ,
            "VI" to  "Virgin Islands,US"
        )


    val emptyList : MutableLiveData<Boolean> =  MutableLiveData<Boolean>()

    private val repository: RestaurantRepository
    var favoritesLive : LiveData<List<RestaurantData>>? = null
    var favorites : List<RestaurantData> = listOf()
    var restaurants : MutableLiveData<ArrayList<RestaurantData>> = MutableLiveData()
    lateinit var lastResponse : ResponseData

    init {
        currentPage = 0
        filtering = false
        val dao = RestaurantDatabase.getDatabase(application).restaurantDao()
        repository = RestaurantRepository(dao)
//        favorites = repository.favorites
        favoritesLive = repository.favoritesLive

//        setFilters(standardCountry,null,null,null,null,null,currentPage)
        getRestaurants()
    }

     fun updateRestaurant(obj : RestaurantUpdate){
         viewModelScope.launch {
             repository.update(obj)

         }
    }

    @JvmName("setFilters1")
    fun setFilters(
        country: String,
        name: String,
        price: Int?,
        address: String,
        city: String,
        zip: String,
        page: Int
    ) {
        Log.d("map", countryMap.size.toString())

        filters["country"] = countryMap.filterValues { it == country }.keys.first()
        filters["name"] = name
        filters["price"] = price
        filters["address"] = address
        filters["city"] = city
        filters["zip"] = if (zip == "null") "" else zip
        filters["page"] = page

    }

    fun getRestaurants(){
        viewModelScope.launch {
            progressVisibility = View.VISIBLE
            val response = repository.getAll(
                country = filters["country"] as String,
                name = filters["name"] as String,
                price = filters["price"] as Int?,
                address = filters["address"] as String,
                city = filters["city"] as String,
                zipCode = filters["zip"] as String,
                page = currentPage as Int?,
            )
            synchronized(restaurants){
                //get old list if no empty and add to temp list
                val temp: ArrayList<RestaurantData> = restaurants.value ?: ArrayList()
                temp.addAll(response.restaurants)

                //new list contains all values
                restaurants.value = temp
                lastResponse = response
                isLoading = false
                emptyList.value = temp.size == 0

            }
        }
    }


    fun addToFavorites(restaurant: RestaurantData){
        viewModelScope.launch {
            repository.insert(restaurant)
        }
    }
}