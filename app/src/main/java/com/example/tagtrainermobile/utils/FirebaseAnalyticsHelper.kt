package com.example.tagtrainermobile.utils

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object FirebaseAnalyticsHelper {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun init(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    }

    fun logEvent(eventName: String, params: Bundle?) {
        firebaseAnalytics.logEvent(eventName, params)
    }

    fun createItemBundle(name: String, quantity: Int, price: Double, currency: String = "BRL"): Bundle {
        return Bundle().apply {
            putString("item_name", name)
            putInt("quantity", quantity)
            putDouble("price", price)
            putString("currency", currency)
        }
    }

    fun createEventParams(items: Array<Bundle>, currency: String = "BRL", value: Double? = null): Bundle {
        return Bundle().apply {
            putParcelableArray("items", items)
            putString("currency", currency)
            value?.let { putDouble("value", it) }
        }
    }

    fun calculateTotalValue(items: Array<Bundle>): Double {
        return items.sumOf { bundle ->
            val quantity = bundle.getInt("quantity", 0)
            val price = bundle.getDouble("price", 0.0)
            quantity * price
        }
    }
}
