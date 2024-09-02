package com.example.tagtrainermobile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.tagtrainermobile.models.Product
import com.example.tagtrainermobile.models.User
import com.example.tagtrainermobile.utils.FirebaseAnalyticsHelper


class LoginFragment : Fragment() {

    val user = User.sigleUser.instance

    val cartProducts = Product.SingleCart.singleCartinstance

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProgressBar()
        setButtonLoginConfig()

        val itemBundles = cartProducts.map { item ->

            Bundle().apply {
                putString("item_name", item.name)
                putInt("quantity", item.quantity)
                putDouble("price", item.price)
                putString("currency", "BRL")
            }
        }.toTypedArray()

        var total: Double = 0.0

        for (bundle in itemBundles) {
            val quantity = bundle.getInt("quantity", 0) // Valor padrão 0 se não estiver presente
            val price = bundle.getDouble("price", 0.0) // Valor padrão 0.0 se não estiver presente
            total += quantity * price
        }

        val paramsEvent = Bundle().apply {
            putParcelableArray("items", itemBundles)
            putString("currency", "BRL")
            putDouble("value", total)
        }

        FirebaseAnalyticsHelper.logEvent("begin_checkout", paramsEvent)
    }

    fun setProgressBar() {
        val progressBar = getView()?.findViewById<ProgressBar>(R.id.progressBar)
        progressBar?.progress = 33
    }

    fun setButtonLoginConfig() {
        val button = getView()?.findViewById<Button>(R.id.enterLoginId)
        button?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                user.isLogged = true
                val paymentFragment = PaymentFragment()
              getFragmentManager()?.beginTransaction()?.add(R.id.fragmentPaymentId, paymentFragment)?.commit()
            }
        })
    }
}