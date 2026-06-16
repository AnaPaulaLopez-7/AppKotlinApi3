package com.example.appkotlinapi3

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    private val productList by lazy { LinearLayout(this) }
    private val api = ProductApi()
    private val allFilterId = View.generateViewId()
    private val inStockFilterId = View.generateViewId()
    private val outOfStockFilterId = View.generateViewId()
    private lateinit var searchInput: EditText
    private var products: List<Product> = emptyList()
    private var stockFilter = StockFilter.ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupLayout()
        loadProducts()
    }

    private fun setupLayout() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(16))
            setBackgroundColor(Color.rgb(246, 247, 249))
        }

        root.addView(title("Productos"))
        root.addView(text("Catalogo disponible para la tienda", 15f, Typeface.NORMAL, Color.rgb(92, 101, 112)))
        root.addView(searchBox())
        root.addView(filterGroup())

        productList.orientation = LinearLayout.VERTICAL

        val scrollView = ScrollView(this)
        scrollView.addView(productList)
        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
    }

    private fun loadProducts() {
        productList.removeAllViews()
        productList.addView(loadingView())

        Thread {
            try {
                val result = api.fetchProducts()
                runOnUiThread { showProducts(result) }
            } catch (error: Exception) {
                runOnUiThread {
                    showError("No se pudieron cargar los productos.\n${error.message.orEmpty()}")
                }
            }
        }.start()
    }

    private fun showProducts(products: List<Product>) {
        this.products = products
        renderProducts()
    }

    private fun renderProducts() {
        productList.removeAllViews()

        val query = searchInput.text.toString().trim().lowercase()
        val filteredProducts = products.filter { product ->
            val matchesSearch = query.isBlank() ||
                product.name.lowercase().contains(query) ||
                product.description.lowercase().contains(query)
            val matchesStock = when (stockFilter) {
                StockFilter.ALL -> true
                StockFilter.IN_STOCK -> product.inStock
                StockFilter.OUT_OF_STOCK -> !product.inStock
            }
            matchesSearch && matchesStock
        }
        val state = ProductListUiState.from(filteredProducts)

        productList.addView(
            text(
                "${state.items.size} productos encontrados",
                14f,
                Typeface.BOLD,
                Color.rgb(92, 101, 112)
            )
        )

        if (!state.canShowProducts) {
            productList.addView(emptyState())
            return
        }

        filteredProducts.forEach { product ->
            productList.addView(productRow(product))
        }
    }

    private fun showError(message: String) {
        productList.removeAllViews()
        productList.addView(text(message, 16f, Typeface.NORMAL, Color.rgb(120, 33, 33)))
    }

    private fun productRow(product: Product): View {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.setPadding(dp(18), dp(16), dp(18), dp(16))
        row.background = roundedBackground(Color.WHITE, dp(12), Color.rgb(225, 228, 232))

        row.addView(productImage(product))
        row.addView(productInfo(product))

        row.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_NAME, product.name)
                putExtra(DetailActivity.EXTRA_DESCRIPTION, product.description)
                putExtra(DetailActivity.EXTRA_PRICE, product.formattedPrice())
                putExtra(DetailActivity.EXTRA_STOCK, product.inStock)
                putExtra(DetailActivity.EXTRA_IMAGE_RES, productImageRes(product))
            }
            startActivity(intent)
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, dp(12), 0, 0)
        row.layoutParams = params
        return row
    }

    private fun productImage(product: Product): ImageView {
        return ImageView(this).apply {
            setImageResource(productImageRes(product))
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = roundedBackground(Color.rgb(241, 244, 248), dp(10), Color.TRANSPARENT)
            layoutParams = LinearLayout.LayoutParams(dp(84), dp(84)).apply {
                setMargins(0, 0, dp(14), 0)
            }
        }
    }

    private fun productInfo(product: Product): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(text(product.name, 18f, Typeface.BOLD, Color.rgb(31, 35, 40)))
            addView(text(product.description, 14f, Typeface.NORMAL, Color.rgb(92, 101, 112)))
            addView(text(product.formattedPrice(), 17f, Typeface.BOLD, Color.rgb(28, 94, 70)))
            addView(stockText(product.inStock))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
    }

    private fun searchBox(): EditText {
        searchInput = EditText(this).apply {
            hint = "Buscar producto"
            textSize = 16f
            setSingleLine(true)
            setPadding(dp(16), dp(10), dp(16), dp(10))
            background = roundedBackground(Color.WHITE, dp(10), Color.rgb(210, 214, 220))
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
                override fun afterTextChanged(s: Editable?) {
                    if (products.isNotEmpty()) renderProducts()
                }
            })
        }

        searchInput.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, dp(18), 0, dp(12))
        }
        return searchInput
    }

    private fun filterGroup(): RadioGroup {
        return RadioGroup(this).apply {
            orientation = RadioGroup.HORIZONTAL
            addView(filterButton(allFilterId, "Todos"))
            addView(filterButton(inStockFilterId, "En stock"))
            addView(filterButton(outOfStockFilterId, "Sin stock"))
            check(allFilterId)
            setOnCheckedChangeListener { _, checkedId ->
                stockFilter = when (checkedId) {
                    inStockFilterId -> StockFilter.IN_STOCK
                    outOfStockFilterId -> StockFilter.OUT_OF_STOCK
                    else -> StockFilter.ALL
                }
                if (products.isNotEmpty()) renderProducts()
            }
        }
    }

    private fun filterButton(id: Int, value: String): RadioButton {
        return RadioButton(this).apply {
            this.id = id
            text = value
            textSize = 14f
            setTextColor(Color.rgb(31, 35, 40))
            setPadding(0, 0, dp(12), 0)
        }
    }

    private fun loadingView(): View {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER_HORIZONTAL
            setPadding(0, dp(32), 0, 0)
            addView(ProgressBar(this@MainActivity))
            addView(text("Cargando productos...", 15f, Typeface.NORMAL, Color.rgb(92, 101, 112)))
        }
    }

    private fun emptyState(): TextView {
        return text("No hay productos para esta busqueda.", 16f, Typeface.NORMAL, Color.rgb(92, 101, 112)).apply {
            setPadding(0, dp(24), 0, 0)
        }
    }

    private fun stockText(inStock: Boolean): TextView {
        val color = if (inStock) Color.rgb(28, 94, 70) else Color.rgb(143, 68, 36)
        return text(if (inStock) "Disponible" else "Sin stock", 14f, Typeface.BOLD, color)
    }

    private fun title(value: String): TextView = text(value, 30f, Typeface.BOLD, Color.rgb(31, 35, 40))

    private fun text(value: String, size: Float, style: Int, color: Int): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTypeface(typeface, style)
            setTextColor(color)
            setPadding(0, 0, 0, dp(6))
        }
    }

    private fun roundedBackground(color: Int, radius: Int, strokeColor: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius.toFloat()
            setStroke(dp(1), strokeColor)
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun productImageRes(product: Product): Int {
        return when (product.id) {
            1 -> R.drawable.product_iphone
            2 -> R.drawable.product_samsung
            3 -> R.drawable.product_pixel
            else -> R.drawable.product_pixel
        }
    }

    private enum class StockFilter {
        ALL,
        IN_STOCK,
        OUT_OF_STOCK
    }
}
