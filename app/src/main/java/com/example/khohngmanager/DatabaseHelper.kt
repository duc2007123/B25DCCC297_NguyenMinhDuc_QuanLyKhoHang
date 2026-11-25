package com.example.khohngmanager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "warehouse.db"
        private const val DATABASE_VERSION = 1

        // Table users
        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USER_USERNAME = "username"
        const val COLUMN_USER_PASSWORD = "password"

        // Table products
        const val TABLE_PRODUCTS = "products"
        const val COLUMN_PRODUCT_ID = "id"
        const val COLUMN_PRODUCT_NAME = "name"
        const val COLUMN_PRODUCT_PRICE = "price"
        const val COLUMN_PRODUCT_QUANTITY = "quantity"
        const val COLUMN_PRODUCT_CATEGORY = "category"
        const val COLUMN_PRODUCT_ACTIVE = "active"
        const val COLUMN_PRODUCT_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_USERNAME TEXT NOT NULL UNIQUE,
                $COLUMN_USER_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

        val createProductsTable = """
            CREATE TABLE $TABLE_PRODUCTS (
                $COLUMN_PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PRODUCT_NAME TEXT NOT NULL,
                $COLUMN_PRODUCT_PRICE INTEGER NOT NULL,
                $COLUMN_PRODUCT_QUANTITY INTEGER NOT NULL,
                $COLUMN_PRODUCT_CATEGORY TEXT NOT NULL,
                $COLUMN_PRODUCT_ACTIVE INTEGER NOT NULL,
                $COLUMN_PRODUCT_DESCRIPTION TEXT
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createProductsTable)

        // Chèn tài khoản mẫu: admin / 123456
        val userValues = ContentValues().apply {
            put(COLUMN_USER_USERNAME, "admin")
            put(COLUMN_USER_PASSWORD, "123456")
        }
        db.insert(TABLE_USERS, null, userValues)

        // Chèn 3 sản phẩm mẫu
        val sample1 = ContentValues().apply {
            put(COLUMN_PRODUCT_NAME, "Chuột không dây")
            put(COLUMN_PRODUCT_PRICE, 150000)
            put(COLUMN_PRODUCT_QUANTITY, 20)
            put(COLUMN_PRODUCT_CATEGORY, "Điện tử")
            put(COLUMN_PRODUCT_ACTIVE, 1)
            put(COLUMN_PRODUCT_DESCRIPTION, "Chuột quang không dây tiện lợi")
        }
        val sample2 = ContentValues().apply {
            put(COLUMN_PRODUCT_NAME, "Bàn phím cơ")
            put(COLUMN_PRODUCT_PRICE, 450000)
            put(COLUMN_PRODUCT_QUANTITY, 3)
            put(COLUMN_PRODUCT_CATEGORY, "Điện tử")
            put(COLUMN_PRODUCT_ACTIVE, 1)
            put(COLUMN_PRODUCT_DESCRIPTION, "Bàn phím cơ gõ êm, có đèn LED")
        }
        val sample3 = ContentValues().apply {
            put(COLUMN_PRODUCT_NAME, "Sổ tay A5")
            put(COLUMN_PRODUCT_PRICE, 25000)
            put(COLUMN_PRODUCT_QUANTITY, 40)
            put(COLUMN_PRODUCT_CATEGORY, "Văn phòng phẩm")
            put(COLUMN_PRODUCT_ACTIVE, 1)
            put(COLUMN_PRODUCT_DESCRIPTION, "Sổ tay ghi chép kích thước A5")
        }
        db.insert(TABLE_PRODUCTS, null, sample1)
        db.insert(TABLE_PRODUCTS, null, sample2)
        db.insert(TABLE_PRODUCTS, null, sample3)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        onCreate(db)
    }

    fun checkLogin(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID),
            "$COLUMN_USER_USERNAME = ? AND $COLUMN_USER_PASSWORD = ?",
            arrayOf(username, password),
            null, null, null
        )
        val hasMatch = cursor.count > 0
        cursor.close()
        return hasMatch
    }

    fun insertUser(username: String, password: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_USERNAME, username)
            put(COLUMN_USER_PASSWORD, password)
        }
        return db.insert(TABLE_USERS, null, values)
    }

    fun insertProduct(p: Product): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PRODUCT_NAME, p.name)
            put(COLUMN_PRODUCT_PRICE, p.price)
            put(COLUMN_PRODUCT_QUANTITY, p.quantity)
            put(COLUMN_PRODUCT_CATEGORY, p.category)
            put(COLUMN_PRODUCT_ACTIVE, p.active)
            put(COLUMN_PRODUCT_DESCRIPTION, p.description)
        }
        return db.insert(TABLE_PRODUCTS, null, values)
    }

    fun getAllProducts(): ArrayList<Product> {
        val productList = ArrayList<Product>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_PRODUCTS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_PRODUCT_ID ASC"
        )

        if (cursor.moveToFirst()) {
            do {
                val product = Product(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)),
                    price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)),
                    quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY)),
                    category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY)),
                    active = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ACTIVE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION)) ?: ""
                )
                productList.add(product)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return productList
    }

    fun getProductById(id: Int): Product? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_PRODUCTS,
            null,
            "$COLUMN_PRODUCT_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var product: Product? = null
        if (cursor.moveToFirst()) {
            product = Product(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)),
                price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)),
                quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY)),
                category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY)),
                active = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ACTIVE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION)) ?: ""
            )
        }
        cursor.close()
        return product
    }

    fun updateQuantity(id: Int, quantity: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PRODUCT_QUANTITY, quantity)
        }
        return db.update(
            TABLE_PRODUCTS,
            values,
            "$COLUMN_PRODUCT_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun deleteProduct(id: Int): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_PRODUCTS,
            "$COLUMN_PRODUCT_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun getTotalValue(): Long {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM($COLUMN_PRODUCT_PRICE * $COLUMN_PRODUCT_QUANTITY) FROM $TABLE_PRODUCTS",
            null
        )
        var totalValue: Long = 0
        if (cursor.moveToFirst()) {
            totalValue = cursor.getLong(0)
        }
        cursor.close()
        return totalValue
    }

    fun countLowStock(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_QUANTITY < 5",
            null
        )
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }
}
