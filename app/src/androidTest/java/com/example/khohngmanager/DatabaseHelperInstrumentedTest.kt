package com.example.khohngmanager

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseHelperInstrumentedTest {

    private lateinit var dbHelper: DatabaseHelper

    @Before
    fun setUp() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        dbHelper = DatabaseHelper(appContext)
    }

    @After
    fun tearDown() {
        dbHelper.close()
    }

    @Test
    fun testInitialSampleUser() {
        assertTrue("Admin login should succeed", dbHelper.checkLogin("admin", "123456"))
        assertFalse("Wrong password should fail", dbHelper.checkLogin("admin", "wrongpass"))
        assertFalse("Non-existent user should fail", dbHelper.checkLogin("user2", "123456"))
    }

    @Test
    fun testInitialSampleProducts() {
        val products = dbHelper.getAllProducts()
        assertTrue("Initial sample products count should be at least 3", products.size >= 3)

        val names = products.map { it.name }
        assertTrue(names.contains("Chuột không dây"))
        assertTrue(names.contains("Bàn phím cơ"))
        assertTrue(names.contains("Sổ tay A5"))
    }

    @Test
    fun testInsertAndGetProduct() {
        val newProduct = Product(
            name = "Tai nghe Bluetooth",
            price = 300000,
            quantity = 10,
            category = "Điện tử",
            active = 1,
            description = "Tai nghe chống ồn"
        )

        val id = dbHelper.insertProduct(newProduct)
        assertTrue("Inserted id should be > 0", id > 0)

        val retrieved = dbHelper.getProductById(id.toInt())
        assertNotNull(retrieved)
        assertEquals("Tai nghe Bluetooth", retrieved?.name)
        assertEquals(300000, retrieved?.price)
        assertEquals(10, retrieved?.quantity)
        assertEquals("Điện tử", retrieved?.category)
        assertEquals(1, retrieved?.active)
        assertEquals("Tai nghe chống ồn", retrieved?.description)
    }

    @Test
    fun testUpdateQuantity() {
        val products = dbHelper.getAllProducts()
        val firstProduct = products[0]

        val rowsAffected = dbHelper.updateQuantity(firstProduct.id, 25)
        assertEquals(1, rowsAffected)

        val updated = dbHelper.getProductById(firstProduct.id)
        assertEquals(25, updated?.quantity)
    }

    @Test
    fun testDeleteProduct() {
        val newProduct = Product(
            name = "Sản phẩm tạm",
            price = 10000,
            quantity = 2,
            category = "Khác",
            active = 1,
            description = ""
        )
        val id = dbHelper.insertProduct(newProduct).toInt()
        assertNotNull(dbHelper.getProductById(id))

        val deletedRows = dbHelper.deleteProduct(id)
        assertEquals(1, deletedRows)
        assertNull(dbHelper.getProductById(id))
    }
}
