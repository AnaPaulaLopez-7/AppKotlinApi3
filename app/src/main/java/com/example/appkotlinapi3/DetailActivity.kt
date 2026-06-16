package com.example.appkotlinapi3

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class DetailActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = intent.getStringExtra(EXTRA_NAME).orEmpty()
        val description = intent.getStringExtra(EXTRA_DESCRIPTION).orEmpty()
        val price = intent.getStringExtra(EXTRA_PRICE).orEmpty()
        val inStock = intent.getBooleanExtra(EXTRA_STOCK, false)
        val imageRes = intent.getIntExtra(EXTRA_IMAGE_RES, R.drawable.product_pixel)

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(20))
            setBackgroundColor(Color.rgb(246, 247, 249))
            addView(text("Detalle del producto", 14f, Typeface.BOLD, Color.rgb(92, 101, 112)))
            addView(productCard(name, description, price, inStock, imageRes))
        }

        val scrollView = ScrollView(this)
        scrollView.addView(container)
        setContentView(scrollView)
    }

    private fun productCard(
        name: String,
        description: String,
        price: String,
        inStock: Boolean,
        imageRes: Int
    ): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(18), dp(20), dp(18))
            background = roundedBackground(Color.WHITE, dp(12), Color.rgb(225, 228, 232))
            addView(productImage(imageRes))
            addView(text(name, 28f, Typeface.BOLD, Color.rgb(31, 35, 40)))
            addView(text(price, 20f, Typeface.BOLD, Color.rgb(28, 94, 70)))
            addView(text(description, 16f, Typeface.NORMAL, Color.rgb(92, 101, 112)))
            addView(text(if (inStock) "Disponible" else "Sin stock", 16f, Typeface.BOLD, stockColor(inStock)))
        }
    }

    private fun productImage(imageRes: Int): ImageView {
        return ImageView(this).apply {
            setImageResource(imageRes)
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = roundedBackground(Color.rgb(241, 244, 248), dp(12), Color.TRANSPARENT)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(190)
            ).apply {
                setMargins(0, 0, 0, dp(18))
            }
        }
    }

    private fun text(value: String, size: Float, style: Int, color: Int): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTypeface(typeface, style)
            setTextColor(color)
            setPadding(0, 0, 0, dp(12))
        }
    }

    private fun stockColor(inStock: Boolean): Int {
        return if (inStock) Color.rgb(28, 94, 70) else Color.rgb(143, 68, 36)
    }

    private fun roundedBackground(color: Int, radius: Int, strokeColor: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius.toFloat()
            setStroke(dp(1), strokeColor)
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    companion object {
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_PRICE = "extra_price"
        const val EXTRA_STOCK = "extra_stock"
        const val EXTRA_IMAGE_RES = "extra_image_res"
    }
}
