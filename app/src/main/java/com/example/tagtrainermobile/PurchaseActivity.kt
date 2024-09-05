package com.example.tagtrainermobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tagtrainermobile.models.Product
import com.example.tagtrainermobile.models.cartProductsAdapter
import com.example.tagtrainermobile.utils.FirebaseAnalyticsHelper
import java.text.DecimalFormat

class PurchaseActivity : AppCompatActivity() {

    var cartProducts = Product.SingleCart.singleCartinstance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TagTrainerMobile)
        setContentView(R.layout.activity_purchase)

        val transactionId = setRandomTransactionCode()

        val value = cartTotalPrice()
        setTransactionInfo(transactionId)
        setProgressBar()
        displayPurchaseItems()

        val itemBundles = cartProducts.map { item ->
            FirebaseAnalyticsHelper.createItemBundle(item.name, item.quantity, item.price)
        }.toTypedArray()

        val paramsEvent = FirebaseAnalyticsHelper.createEventParams(itemBundles, value = value)

        paramsEvent.putString("transaction_id", transactionId)

        FirebaseAnalyticsHelper.logEvent("purchase", paramsEvent)
    }

    fun cartTotalPrice() : Double {
        var totalValue: Double = 0.0
        val totalQuantity: Int = 0
        for (i in cartProducts.indices) {
            totalValue += cartProducts.get(i).price * cartProducts.get(i).quantity
        }
        return totalValue
    }

    fun setRandomTransactionCode() : String {
        val numbers = (0..40103430).random()
        val character = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val randChar = character.random()
        return numbers.toString()+randChar
    }

    fun setTransactionInfo(transactionId : String) {
        val df = DecimalFormat("#.00")
        val txtTransactioId = findViewById<TextView>(R.id.transactioId)
        txtTransactioId.text = "Sua Compra: "+transactionId
        val txtTransactionTotal = findViewById<TextView>(R.id.transactioTotalId)
        txtTransactionTotal.text = "Total: R$ "+df.format(cartTotalPrice())
    }

    fun setProgressBar() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar2)
        progressBar.progress = 100
    }

    fun displayPurchaseItems() {
        val purchaseTable = findViewById<ListView>(R.id.diplayPurchaseId)
        val adapter = cartProductsAdapter(this, cartProducts)
        purchaseTable.adapter = adapter
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

}