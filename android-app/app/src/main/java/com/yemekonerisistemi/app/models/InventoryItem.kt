package com.yemekonerisistemi.app.models

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class InventoryItem(
    @SerializedName("id")
    val id: String = UUID.randomUUID().toString(),
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("quantity")
    val quantity: Int = 1,
    
    @SerializedName("unit")
    val unit: String = "adet",
    
    @SerializedName("category")
    val category: String = "",
    
    @SerializedName("expiry_date")
    val expiryDate: String? = null,
    
    @SerializedName("added_date")
    val addedDate: Long = System.currentTimeMillis()
)
