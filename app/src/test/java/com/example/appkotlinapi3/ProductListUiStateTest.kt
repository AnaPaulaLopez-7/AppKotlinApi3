package com.example.appkotlinapi3

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductListUiStateTest {
    @Test
    fun givenProductList_whenBuildUiState_thenInformationCanBeDisplayed() {
        val products = listOf(
            Product(
                id = 1,
                name = "iPhone 13",
                description = "The latest iPhone from Apple",
                price = 999.99,
                currency = "USD",
                inStock = true
            )
        )

        val state = ProductListUiState.from(products)

        assertTrue(state.canShowProducts)
        assertEquals(1, state.items.size)
        assertEquals("iPhone 13", state.items.first().name)
        assertEquals("USD 999.99", state.items.first().price)
    }

    @Test
    fun givenEmptyProductList_whenBuildUiState_thenInformationCannotBeDisplayed() {
        val state = ProductListUiState.from(emptyList())

        assertFalse(state.canShowProducts)
        assertTrue(state.items.isEmpty())
    }
}
