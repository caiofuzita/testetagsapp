package com.example.tagtrainermobile

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.tagtrainermobile.models.ListingProduct
import com.example.tagtrainermobile.models.Product
import com.example.tagtrainermobile.models.User
import com.example.tagtrainermobile.utils.FirebaseAnalyticsHelper
import java.text.DecimalFormat





class CartActivity : AppCompatActivity() {

    var listingProduct = ListingProduct.SingleList.singleListInstance
    var cartProducts = Product.SingleCart.singleCartinstance
    val user = User.sigleUser.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TagTrainerMobile)
        setContentView(R.layout.activity_cart)
        setTableForCartProducts()
        cartTotalPrice()
        setCheckoutButtonConfig()

        val itemBundles = cartProducts.map { item ->
            FirebaseAnalyticsHelper.createItemBundle(item.name, item.quantity, item.price)
        }.toTypedArray()
        val totalCart = FirebaseAnalyticsHelper.calculateTotalValue(itemBundles)
        val paramsEvent = FirebaseAnalyticsHelper.createEventParams(itemBundles, value = totalCart)
        FirebaseAnalyticsHelper.logEvent("view_cart", paramsEvent)
    }

    interface setRadioButtonsConfig {
        fun setRadioButtonsConfig()
    }

    fun setTableForCartProducts() {

        val totalValue = TextView(this)
        val table: TableLayout = findViewById(R.id.tableCartId)
        val tableTitle = TextView(this)
        tableTitle.gravity = Gravity.CENTER_HORIZONTAL
        tableTitle.text = "Cart Products"
        tableTitle.textSize = 24.0F
        tableTitle.typeface = Typeface.DEFAULT_BOLD
        table.addView(tableTitle)

        val tableRowHeader = TableRow(this)
        val prodNameHeader = TextView(this)
        prodNameHeader.text = "Produto"
        prodNameHeader.textSize = 14.0F
        prodNameHeader.typeface = Typeface.DEFAULT_BOLD
        val prodQuantityHeader = TextView(this)
        prodQuantityHeader.text = "Qtd."
        prodQuantityHeader.textSize = 14.0F
        prodQuantityHeader.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_START
        prodQuantityHeader.typeface = Typeface.DEFAULT_BOLD
        val prodPriceHeader = TextView(this)
        prodPriceHeader.text = "Preço"
        prodPriceHeader.textSize = 14.0F
        prodPriceHeader.typeface = Typeface.DEFAULT_BOLD

        tableRowHeader.addView(prodQuantityHeader)
        tableRowHeader.addView(prodNameHeader)
        tableRowHeader.addView(prodPriceHeader)

        table.addView(tableRowHeader)

        for (i in cartProducts.indices) {
            val tableRow = TableRow(this)
            val prodName = TextView(this)
            val prodQuantity = TextView(this)
            val prodPrice = TextView(this)
            val prodRemove = ImageButton(this)

            tableRow.gravity = Gravity.CENTER

            prodName.text = cartProducts.get(i).name
            prodQuantity.text = cartProducts.get(i).quantity.toString()
            val df = DecimalFormat("#.00")
            prodPrice.text = "R$ " +df.format(cartProducts.get(i).price).toString()
            prodRemove.setImageResource(R.drawable.img)
            prodRemove.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                   removeFromCart(cartProducts.get(i))
                    val df = DecimalFormat("#.00")
                    prodPrice.text = "R$ " +df.format(cartProducts.get(i).price).toString()
                    prodQuantity.text = cartProducts.get(i).quantity.toString()
                    totalValue.text = "R$ "+df.format(cartTotalPrice()).toString()
                        if(cartProducts.get(i).quantity <=0) {
                            table.removeView(tableRow)
                            cartProducts.remove(cartProducts.get(i))
                        }
                }
            })


            tableRow.addView(prodQuantity)
            tableRow.addView(prodName)
            tableRow.addView(prodPrice)
            tableRow.addView(prodRemove)


            table.addView(tableRow)


            }

        val totalCartRow = TableRow(this)
        totalCartRow.gravity = Gravity.CENTER_HORIZONTAL

        val df = DecimalFormat("#.00")
        val emptySlot = TextView(this)
        emptySlot.text = ""
        val emptySlot2 = TextView(this)
        emptySlot2.text = ""
        val totalText = TextView(this)
        totalText.text = "Total"
        totalText.textSize = 14.0F
        totalText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        totalText.typeface = Typeface.DEFAULT_BOLD

        totalValue.text = "R$ "+df.format(cartTotalPrice()).toString()
        totalValue.textSize = 24.0F
        totalValue.typeface = Typeface.DEFAULT_BOLD

        totalCartRow.addView(emptySlot)
        totalCartRow.addView(totalText)
        totalCartRow.addView(totalValue)
        totalCartRow.addView(emptySlot2)

        table.addView(totalCartRow)
    }

    fun removeFromCart(p : Product ) {
        val unitaryPrice = listingProduct.find({ it.listProdName == p.name })!!
        val existingProduct = cartProducts.find({ it.name == p.name })
        if (existingProduct !== null) {
            cartProducts.forEach {
                if (it.name == p.name) {
                    val itemBundles = FirebaseAnalyticsHelper.createItemBundle(it.name, 1, unitaryPrice.listProdPrice)
                    val paramsEvent = FirebaseAnalyticsHelper.createEventParams(arrayOf(itemBundles), value = unitaryPrice.listProdPrice)
                    FirebaseAnalyticsHelper.logEvent("remove_from_cart", paramsEvent)

                    it.price = it.price - unitaryPrice.listProdPrice
                    it.quantity--
                }
            }
        }

    }

    fun cartTotalPrice() : Double {
        var totalValue : Double = 0.0
        val totalQuantity : Int = 0
        for(i in cartProducts.indices) {
            cartProducts.get(i).quantity
            totalValue = totalValue + cartProducts.get(i).price
        }
        return totalValue
    }

    fun setCheckoutButtonConfig() {
        val checkoutBtn = findViewById<Button>(R.id.checkoutBtnId)
        val backButton = findViewById<Button>(R.id.backButtonCheckId)
        checkoutBtn.visibility = View.VISIBLE
        checkoutBtn.setOnClickListener( object : View.OnClickListener{
             override fun onClick(view: View?) {
                 startCheckoutFragment()
                 checkoutBtn.visibility = View.INVISIBLE
                 backButton.visibility = View.INVISIBLE
            }
        })
        backButton.visibility = View.VISIBLE
        backButton.setOnClickListener( object : View.OnClickListener{
            override fun onClick (view: View?) {
                goToListViewProducts()
            }
        })
    }

    fun startCheckoutFragment() {
        if(user.isLogged) {
            val paymentFragment = PaymentFragment()
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentPaymentId, paymentFragment).commit()
        } else {
            val loginFragment = LoginFragment()
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentPaymentId, loginFragment).commit()
        }
    }

    fun goToListViewProducts() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }

}

