package com.example.khohngmanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ProductAdapter
    private val productList = ArrayList<Product>()
    private val decimalFormat = DecimalFormat("#,###")

    private lateinit var tvTotalProducts: TextView
    private lateinit var tvLowStockCount: TextView
    private lateinit var tvTotalValue: TextView
    private lateinit var tvEmpty: TextView
    private lateinit var lvProducts: ListView
    private lateinit var btnAddProduct: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        tvTotalProducts = findViewById(R.id.tvTotalProducts)
        tvLowStockCount = findViewById(R.id.tvLowStockCount)
        tvTotalValue = findViewById(R.id.tvTotalValue)
        tvEmpty = findViewById(R.id.tvEmpty)
        lvProducts = findViewById(R.id.lvProducts)
        btnAddProduct = findViewById(R.id.btnAddProduct)

        adapter = ProductAdapter(this, productList)
        lvProducts.adapter = adapter

        lvProducts.setOnItemClickListener { _, _, position, _ ->
            val product = adapter.getItem(position)
            val intent = Intent(this, ProductDetailActivity::class.java).apply {
                putExtra("PRODUCT_ID", product.id)
            }
            startActivity(intent)
        }

        btnAddProduct.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val products = dbHelper.getAllProducts()
        val totalValue = dbHelper.getTotalValue()
        val lowStockCount = dbHelper.countLowStock()

        tvTotalProducts.text = products.size.toString()
        tvLowStockCount.text = lowStockCount.toString()
        tvTotalValue.text = "${decimalFormat.format(totalValue)} đ"

        if (products.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            lvProducts.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            lvProducts.visibility = View.VISIBLE
        }

        adapter.updateData(products)
    }
}
