package com.example.appkotlinapi3

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    private val container by lazy { LinearLayout(this) }
    private val api = ProductApi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupLayout()
        loadProducts()
    }

    private fun setupLayout() {
        container.orientation = LinearLayout.VERTICAL
        container.setPadding(32, 32, 32, 32)

        val scrollView = ScrollView(this)
        scrollView.addView(container)
        setContentView(scrollView)
    }

    private fun loadProducts() {
        container.removeAllViews()
        container.addView(title("Productos disponibles"))
        container.addView(ProgressBar(this))

        Thread {
            try {
                val products = api.fetchProducts()
                runOnUiThread { showProducts(products) }
            } catch (error: Exception) {
                runOnUiThread {
                    showError("No se pudieron cargar los productos.\n${error.message.orEmpty()}")
                }
            }
        }.start()
    }

    private fun showProducts(products: List<Product>) {
        container.removeAllViews()
        container.addView(title("Productos disponibles"))

        products.forEach { product ->
            container.addView(productRow(product))
        }
    }

    private fun showError(message: String) {
        container.removeAllViews()
        container.addView(title("Productos disponibles"))
        container.addView(text(message, 16f, Typeface.NORMAL))
    }

    private fun productRow(product: Product): View {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.VERTICAL
        row.setPadding(24, 24, 24, 24)
        row.setBackgroundColor(0xFFEFEFEF.toInt())
        row.addView(text(product.name, 18f, Typeface.BOLD))
        row.addView(text(product.formattedPrice(), 16f, Typeface.NORMAL))
        row.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_NAME, product.name)
                putExtra(DetailActivity.EXTRA_DESCRIPTION, product.description)
                putExtra(DetailActivity.EXTRA_PRICE, product.formattedPrice())
                putExtra(DetailActivity.EXTRA_STOCK, product.inStock)
            }
            startActivity(intent)
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 16, 0, 0)
        row.layoutParams = params
        return row
    }

    private fun title(value: String): TextView = text(value, 24f, Typeface.BOLD)

    private fun text(value: String, size: Float, style: Int): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTypeface(typeface, style)
            setTextColor(0xFF222222.toInt())
        }
    }
}
