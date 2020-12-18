package com.example.project.vmodels

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.project.api.RetrofitClient
import com.example.project.database.RestaurantDatabase
import com.example.project.models.ResponseData
import com.example.project.models.RestaurantData
import com.example.project.models.RestaurantRepository
import kotlinx.coroutines.launch

class RestaurantListViewModel(application: Application) : AndroidViewModel(application) {
    val price: Int = 2
    var progressVisibility: Int = View.GONE
    private val standardCountryCode: String = "US"
    val standardCountry: String = "United States"

    val request = RetrofitClient.api

    private var startPage : Int = 0
    var currentPage = startPage
    var isLastPage: Boolean = false

    var isLoading: Boolean = false

    var filtering : Boolean = false

    var filters: HashMap<String,Any?> = hashMapOf(
        "country" to standardCountryCode,
        "name" to "",
        "price" to price,
        "address" to "",
        "city" to "",
        "zip" to "",
        "page" to currentPage
    )


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
            "RO" to "Romania",
            "VI" to  "Virgin Islands,US",
        )

    val emptyList : MutableLiveData<Boolean> =  MutableLiveData<Boolean>()
    private val repository: RestaurantRepository
    var favoritesLive : LiveData<List<RestaurantData>>? = null
    var restaurants : MutableLiveData<ArrayList<RestaurantData>> = MutableLiveData()
    lateinit var lastResponse : ResponseData

    init {
        currentPage = 0
        filtering = false
        val dao = RestaurantDatabase.getDatabase(application).restaurantDao()
        repository = RestaurantRepository(dao)
        favoritesLive = repository.favoritesLive
        getRestaurants()
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

    fun findRestaurant(id : Long): MutableLiveData<RestaurantData> {
        val result = MutableLiveData<RestaurantData>()
        viewModelScope.launch {
            val temp =  repository.findRestaurant(id)
            result.postValue(temp)
        }
        return result
    }

    fun addToFavorites(restaurant: RestaurantData){
        viewModelScope.launch {
            repository.insert(restaurant)
        }
    }
}