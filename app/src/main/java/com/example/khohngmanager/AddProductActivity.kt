package com.example.khohngmanager

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class AddProductActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    private lateinit var edtProductName: EditText
    private lateinit var edtProductPrice: EditText
    private lateinit var edtProductQuantity: EditText
    private lateinit var spCategory: Spinner
    private lateinit var swActive: SwitchCompat
    private lateinit var edtProductDescription: EditText
    private lateinit var btnSaveProduct: Button
    private lateinit var btnCancel: Button
    private var btnBackHeader: android.widget.ImageButton? = null

    private val categories = arrayOf("Điện tử", "Thực phẩm", "Văn phòng phẩm", "Gia dụng", "Khác")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        dbHelper = DatabaseHelper(this)

        edtProductName = findViewById(R.id.edtProductName)
        edtProductPrice = findViewById(R.id.edtProductPrice)
        edtProductQuantity = findViewById(R.id.edtProductQuantity)
        spCategory = findViewById(R.id.spCategory)
        swActive = findViewById(R.id.swActive)
        edtProductDescription = findViewById(R.id.edtProductDescription)
        btnSaveProduct = findViewById(R.id.btnSaveProduct)
        btnCancel = findViewById(R.id.btnCancel)
        btnBackHeader = findViewById(R.id.btnBackHeader)

        btnBackHeader?.setOnClickListener {
            finish()
        }

        // Thiết lập Adapter cho Spinner loại sản phẩm
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spCategory.adapter = spinnerAdapter

        btnSaveProduct.setOnClickListener {
            saveProduct()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveProduct() {
        val name = edtProductName.text.toString().trim()
        val priceStr = edtProductPrice.text.toString().trim()
        val quantityStr = edtProductQuantity.text.toString().trim()
        val category = spCategory.selectedItem?.toString() ?: categories[0]
        val active = if (swActive.isChecked) 1 else 0
        val description = edtProductDescription.text.toString().trim()

        if (name.isEmpty()) {
            edtProductName.error = "Tên sản phẩm không được để trống"
            edtProductName.requestFocus()
            Toast.makeText(this, "Tên sản phẩm không được để trống", Toast.LENGTH_SHORT).show()
            return
        }

        if (priceStr.isEmpty()) {
            edtProductPrice.error = "Vui lòng nhập giá bán"
            edtProductPrice.requestFocus()
            Toast.makeText(this, "Vui lòng nhập giá bán", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toIntOrNull()
        if (price == null || price <= 0) {
            edtProductPrice.error = "Giá bán phải lớn hơn 0"
            edtProductPrice.requestFocus()
            Toast.makeText(this, "Giá bán phải lớn hơn 0", Toast.LENGTH_SHORT).show()
            return
        }

        if (quantityStr.isEmpty()) {
            edtProductQuantity.error = "Vui lòng nhập số lượng"
            edtProductQuantity.requestFocus()
            Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show()
            return
        }

        val quantity = quantityStr.toIntOrNull()
        if (quantity == null || quantity < 0) {
            edtProductQuantity.error = "Số lượng không được âm"
            edtProductQuantity.requestFocus()
            Toast.makeText(this, "Số lượng không được âm", Toast.LENGTH_SHORT).show()
            return
        }

        val newProduct = Product(
            name = name,
            price = price,
            quantity = quantity,
            category = category,
            active = active,
            description = description
        )

        val insertedId = dbHelper.insertProduct(newProduct)
        if (insertedId > 0) {
            Toast.makeText(this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Có lỗi xảy ra khi lưu sản phẩm", Toast.LENGTH_SHORT).show()
        }
    }
}
