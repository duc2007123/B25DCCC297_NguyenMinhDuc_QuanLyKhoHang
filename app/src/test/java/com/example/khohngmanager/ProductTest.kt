package com.example.khohngmanager

import org.junit.Assert.*
import org.junit.Test

class ProductTest {

    @Test
    fun testProductDefaultValues() {
        val product = Product()
        assertEquals(0, product.id)
        assertEquals("", product.name)
        assertEquals(0, product.price)
        assertEquals(0, product.quantity)
        assertEquals("", product.category)
        assertEquals(1, product.active)
        assertEquals("", product.description)
    }

    @Test
    fun testProductCreation() {
        val product = Product(
            id = 1,
            name = "Chuột không dây",
            price = 150000,
            quantity = 20,
            category = "Điện tử",
            active = 1,
            description = "Chuột quang"
        )
        assertEquals(1, product.id)
        assertEquals("Chuột không dây", product.name)
        assertEquals(150000, product.price)
        assertEquals(20, product.quantity)
        assertEquals("Điện tử", product.category)
        assertEquals(1, product.active)
        assertEquals("Chuột quang", product.description)
    }

    @Test
    fun testStockValueCalculation() {
        val product = Product(
            id = 1,
            name = "Bàn phím cơ",
            price = 450000,
            quantity = 3
        )
        val totalValue = product.price.toLong() * product.quantity
        assertEquals(1350000L, totalValue)
    }
}
