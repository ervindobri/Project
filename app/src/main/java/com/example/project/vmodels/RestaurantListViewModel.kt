package com.example.project.vmodels

import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.SortedList
import com.example.project.models.ApiEndpoints
import com.example.project.models.ResponseData
import com.example.project.models.RestaurantData
import com.example.project.models.RetrofitClient
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RestaurantListViewModel : ViewModel() {
    val standardCountry: String = "MX"

    val request = RetrofitClient.buildService(ApiEndpoints::class.java)

    var searchString: String = ""

    var position: Int = 0
    private var PAGE_START : Int = 0;
    var currentPage = PAGE_START
    var isLastPage: Boolean = false

    var isLoading: Boolean = false

    var filtering : Boolean = false
    var filters: List<String> = listOf()

    val nameComparator: Comparator<RestaurantData> =
        Comparator<RestaurantData> { a, b ->
            a.getName().compareTo(b.getName())
        }

    var oldList : ArrayList<RestaurantData> = ArrayList()

    lateinit var lastResponse : ResponseData
    fun filter(models: ArrayList<RestaurantData>, query: String): ArrayList<RestaurantData> {
        val lowerCaseQuery = query.toLowerCase(Locale.ROOT)
        val filteredModelList: ArrayList<RestaurantData> = ArrayList()
        for (model in models) {
            val text: String = model.getName().toLowerCase(Locale.ROOT)
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model)
            }
        }
        return filteredModelList
    }


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
    init {
        currentPage = 0
        filtering = false
    }
}