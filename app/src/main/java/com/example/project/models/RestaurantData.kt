package com.example.project.models

data class ResponseData(
    val total_entries: Int,
    val per_page: Int,
    val current_page: Int,
    val restaurants: ArrayList<RestaurantData>
)


data class RestaurantData(
    val address: String = "",
    val area: String = "",
    val city: String = "",
    val country: String = "",
    val id: Int = 0,
    val image_url: String = "https://i.imgur.com/tGbaZCY.jpg",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val mobile_reserve_url: String = "",
    val name: String = "",
    val phone: String = "",
    val postal_code: String = "",
    val price: Int = 0,
    val reserve_url: String = "",
    val state: String = ""


) {
    override fun toString(): String {
        return "RestaurantData(address='$address', area='$area', city='$city', country='$country', id=$id, image_url='$image_url', lat=$lat, lng=$lng, mobile_reserve_url='$mobile_reserve_url', name='$name', phone='$phone', postal_code='$postal_code', price=$price, reserve_url='$reserve_url', state='$state')"
    }
}