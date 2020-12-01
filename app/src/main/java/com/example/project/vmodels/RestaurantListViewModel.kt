package com.example.project.vmodels

import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.SortedList
import com.example.project.models.RestaurantData
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class RestaurantListViewModel : ViewModel() {
    private var PAGE_START : Int = 0;
    var currentPage = PAGE_START

    var isLastPage: Boolean = false
    var isLoading: Boolean = false


    val nameComparator: Comparator<RestaurantData> =
        Comparator<RestaurantData> { a, b ->
            a.getName().compareTo(b.getName())
        }

    var oldList : ArrayList<RestaurantData> = ArrayList()

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

}