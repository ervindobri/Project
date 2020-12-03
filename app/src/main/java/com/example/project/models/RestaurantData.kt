package com.example.project.models

import java.io.Serializable

data class ResponseData(
    val total_entries: Int,
    val per_page: Int,
    val current_page: Int,
    val restaurants: ArrayList<RestaurantData>


) {
    override fun toString(): String {
        return "ResponseData(total_entries=$total_entries, per_page=$per_page, current_page=$current_page, restaurants=${restaurants.size})"
    }
}


data class RestaurantData(
    val address: String = "",
    val area: String = "",
    val city: String = "",
    val country: String = "",
    val id: Int = 0,
    val image_url: String = "https://www.elitetraveler.com/wp-content/uploads/2007/02/Caelis_Barcelona_alta2A0200-1-730x450.jpg",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val mobile_reserve_url: String = "",
    val name: String = "",
    val phone: String = "",
    val postal_code: String = "",
    val price: Int = 0,
    val reserve_url: String = "",
    val state: String = ""

) : Serializable {
    fun priceRange(): String {
        return when (price) {
            1 -> "$"
            2 -> "$$"
            3 -> "$$$"
            4 -> "$$$$"
            else -> "$"
        }
    }

    @JvmName("getId1")
    fun getId() :Int {
        return id
    }
    override fun toString(): String {
        return "RestaurantData(address='$address', area='$area', city='$city', country='$country', id=$id, image_url='$image_url', lat=$lat, lng=$lng, mobile_reserve_url='$mobile_reserve_url', name='$name', phone='$phone', postal_code='$postal_code', price=$price, reserve_url='$reserve_url', state='$state')"
    }

    @JvmName("getName1")
    fun getName(): String {
        return name
    }
}