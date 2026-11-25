package com.example.khohngmanager

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.DecimalFormat

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var productId: Int = -1
    private var currentProduct: Product? = null
    private val decimalFormat = DecimalFormat("#,###")

    private lateinit var tvDetailName: TextView
    private lateinit var tvDetailCategory: TextView
    private lateinit var tvDetailStatus: TextView
    private lateinit var tvDetailPrice: TextView
    private lateinit var tvDetailQuantity: TextView
    private lateinit var tvDetailTotalValue: TextView
    private lateinit var tvDetailLowStockWarning: TextView
    private lateinit var tvDetailDescription: TextView

    private lateinit var btnIncreaseQuantity: Button
    private lateinit var btnDecreaseQuantity: Button
    private lateinit var btnDeleteProduct: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        dbHelper = DatabaseHelper(this)
        productId = intent.getIntExtra("PRODUCT_ID", -1)

        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin sản phẩm", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tvDetailName = findViewById(R.id.tvDetailName)
        tvDetailCategory = findViewById(R.id.tvDetailCategory)
        tvDetailStatus = findViewById(R.id.tvDetailStatus)
        tvDetailPrice = findViewById(R.id.tvDetailPrice)
        tvDetailQuantity = findViewById(R.id.tvDetailQuantity)
        tvDetailTotalValue = findViewById(R.id.tvDetailTotalValue)
        tvDetailLowStockWarning = findViewById(R.id.tvDetailLowStockWarning)
        tvDetailDescription = findViewById(R.id.tvDetailDescription)

        btnIncreaseQuantity = findViewById(R.id.btnIncreaseQuantity)
        btnDecreaseQuantity = findViewById(R.id.btnDecreaseQuantity)
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct)
        btnBack = findViewById(R.id.btnBack)

        findViewById<android.widget.ImageButton>(R.id.btnBackHeader)?.setOnClickListener {
            finish()
        }

        btnIncreaseQuantity.setOnClickListener {
            currentProduct?.let { product ->
                val newQuantity = product.quantity + 1
                dbHelper.updateQuantity(product.id, newQuantity)
                loadProductDetails()
                Toast.makeText(this, "Đã nhập thêm 1 sản phẩm", Toast.LENGTH_SHORT).show()
            }
        }

        btnDecreaseQuantity.setOnClickListener {
            currentProduct?.let { product ->
                if (product.quantity == 0) {
                    Toast.makeText(this, "Hết hàng", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val newQuantity = product.quantity - 1
                dbHelper.updateQuantity(product.id, newQuantity)
                loadProductDetails()
                Toast.makeText(this, "Đã bán 1 sản phẩm", Toast.LENGTH_SHORT).show()
            }
        }

        btnDeleteProduct.setOnClickListener {
            showDeleteConfirmDialog()
        }

        btnBack.setOnClickListener {
            finish()
        }

        loadProductDetails()
    }

    private fun loadProductDetails() {
        val product = dbHelper.getProductById(productId)
        if (product == null) {
            Toast.makeText(this, "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        currentProduct = product

        tvDetailName.text = product.name
        tvDetailCategory.text = product.category
        tvDetailPrice.text = "${decimalFormat.format(product.price)} đ"
        tvDetailQuantity.text = product.quantity.toString()

        val totalValue = product.price.toLong() * product.quantity
        tvDetailTotalValue.text = "${decimalFormat.format(totalValue)} đ"

        if (product.active == 1) {
            tvDetailStatus.text = "Đang kinh doanh"
            tvDetailStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        } else {
            tvDetailStatus.text = "Ngừng kinh doanh"
            tvDetailStatus.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
        }

        tvDetailDescription.text = if (product.description.isNotEmpty()) {
            product.description
        } else {
            "Chưa có mô tả"
        }

        if (product.quantity < 5) {
            tvDetailLowStockWarning.visibility = View.VISIBLE
            tvDetailQuantity.setTextColor(ContextCompat.getColor(this, R.color.error_red))
        } else {
            tvDetailLowStockWarning.visibility = View.GONE
            tvDetailQuantity.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
        }
    }

    private fun showDeleteConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?")
            .setPositiveButton("Xóa") { _, _ ->
                currentProduct?.let { product ->
                    dbHelper.deleteProduct(product.id)
                    Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
