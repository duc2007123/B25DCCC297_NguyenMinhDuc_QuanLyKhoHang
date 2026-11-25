package com.example.khohngmanager

data class Product(
    var id: Int = 0,
    var name: String = "",
    var price: Int = 0,
    var quantity: Int = 0,
    var category: String = "",
    var active: Int = 1,
    var description: String = ""
)
