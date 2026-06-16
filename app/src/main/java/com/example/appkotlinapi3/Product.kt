package com.example.appkotlinapi3

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val currency: String,
    val inStock: Boolean
) {
    fun formattedPrice(): String = "$currency %.2f".format(price)
}
