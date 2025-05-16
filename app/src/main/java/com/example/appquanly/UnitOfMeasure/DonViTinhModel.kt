package com.example.appquanly.UnitOfMeasure

import java.util.UUID

data class DonViTinh(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var isSelected: Boolean = false
)
