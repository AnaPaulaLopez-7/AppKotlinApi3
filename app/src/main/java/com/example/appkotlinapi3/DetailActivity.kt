package com.example.appkotlinapi3

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView

class DetailActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = intent.getStringExtra(EXTRA_NAME).orEmpty()
        val description = intent.getStringExtra(EXTRA_DESCRIPTION).orEmpty()
        val price = intent.getStringExtra(EXTRA_PRICE).orEmpty()
        val inStock = intent.getBooleanExtra(EXTRA_STOCK, false)

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            addView(text(name, 24f, Typeface.BOLD))
            addView(text(price, 18f, Typeface.BOLD))
            addView(text(description, 16f, Typeface.NORMAL))
            addView(text(if (inStock) "Disponible" else "Sin stock", 16f, Typeface.NORMAL))
        }

        setContentView(container)
    }

    private fun text(value: String, size: Float, style: Int): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTypeface(typeface, style)
            setTextColor(0xFF222222.toInt())
            setPadding(0, 0, 0, 20)
        }
    }

    companion object {
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_PRICE = "extra_price"
        const val EXTRA_STOCK = "extra_stock"
    }
}
