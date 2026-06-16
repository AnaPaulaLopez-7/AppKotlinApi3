package com.example.appkotlinapi3

data class ProductListUiState(
    val canShowProducts: Boolean,
    val items: List<ProductListItem>
) {
    companion object {
        fun from(products: List<Product>): ProductListUiState {
            val items = products.map { product ->
                ProductListItem(
                    name = product.name,
                    description = product.description,
                    price = product.formattedPrice(),
                    inStock = product.inStock
                )
            }

            return ProductListUiState(
                canShowProducts = items.isNotEmpty(),
                items = items
            )
        }
    }
}

data class ProductListItem(
    val name: String,
    val description: String,
    val price: String,
    val inStock: Boolean
)
