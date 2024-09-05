package com.example.tagtrainermobile

import android.util.Log

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.tagtrainermobile.models.ListProductsAdapter
import com.example.tagtrainermobile.models.ListingProduct
import com.example.tagtrainermobile.utils.FirebaseAnalyticsHelper


class MainActivity : AppCompatActivity() {

    var listingProducts = ListingProduct.SingleList.singleListInstance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TagTrainerMobile)
        setContentView(R.layout.activity_main)
        displayListingPage()

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val menu = findViewById<SearchView>(R.id.searchViewId)

        menu.setSearchableInfo(searchManager.getSearchableInfo(componentName))
    }

    fun onClickedProducts(v: ListView, p: Int) {
        val intent = Intent(applicationContext, ProductActivity::class.java)

        val params = Bundle()
        params.putInt("id", p)
        intent.putExtras(params)

        startActivity(intent)

        val itemBundles = FirebaseAnalyticsHelper.createItemBundle(listingProducts[p].listProdName, 1,listingProducts[p].listProdPrice)
        val paramsEvent = FirebaseAnalyticsHelper.createEventParams(arrayOf(itemBundles), value = listingProducts[p].listProdPrice)
        FirebaseAnalyticsHelper.logEvent("select_item", paramsEvent)

        Log.d("testando", p.toString())
    }

    fun filteredProductsList() : ArrayList<ListingProduct> {
        val listCategory = intent.getStringExtra("listType")

        val categoryList = ArrayList<ListingProduct>()
        for(i in listingProducts) {
            if(i.listProdCat == listCategory) {
                categoryList.add(i)
            }
        }
        if (categoryList.size <= 0) return listingProducts
        return categoryList
    }

    fun displayListingPage() {
        val table: ListView = findViewById(R.id.tableID)
        val adapter = ListProductsAdapter(this, filteredProductsList())
        table.adapter = adapter
        table.setOnItemClickListener { parent, view, position, id ->
            onClickedProducts(table, filteredProductsList().get(position).listProdId-1)
        }

        val itemBundles = listingProducts.map { item ->
            FirebaseAnalyticsHelper.createItemBundle(item.listProdName, 1, item.listProdPrice)
        }.toTypedArray()

        val paramsEvent = FirebaseAnalyticsHelper.createEventParams(itemBundles)
        FirebaseAnalyticsHelper.logEvent("view_item_list", paramsEvent)
    }
}

