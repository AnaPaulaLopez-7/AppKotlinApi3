package com.example.appkotlinapi3

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ProductApi {
    fun fetchProducts(): List<Product> {
        val connection = URL(API_URL).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000

        return try {
            if (connection.responseCode !in 200..299) {
                throw IllegalStateException("Error HTTP ${connection.responseCode}")
            }

            val response = connection.inputStream.bufferedReader().use { reader -> reader.readText() }
            parseProducts(response)
        } finally {
            connection.disconnect()
        }
    }

    private fun parseProducts(response: String): List<Product> {
        val productsJson = JSONObject(response).getJSONArray("products")
        return List(productsJson.length()) { index ->
            val item = productsJson.getJSONObject(index)
            Product(
                id = item.getInt("id"),
                name = item.getString("name"),
                description = item.getString("description"),
                price = item.getDouble("price"),
                currency = item.getString("currency"),
                inStock = item.getBoolean("in_stock")
            )
        }
    }

    companion object {
        private const val API_URL = "https://jsonkeeper.com/b/MX0A"
    }
}
