package com.yemekonerisistemi.app.models

data class InventoryItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    var quantity: Int = 1,
    val unit: String = "adet",
    val category: String = "Diğer"
)
