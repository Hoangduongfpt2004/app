package com.example.appquanly.ChooseDish

import com.example.appquanly.data.sqlite.Entity.InventoryItem

data class DishSelection(
    val item: InventoryItem,
    var quantity: Int = 0
)
