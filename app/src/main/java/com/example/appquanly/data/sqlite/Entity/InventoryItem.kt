package com.example.appquanly.data.sqlite.Entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InventoryItem(
    var InventoryItemID: String,
    var InventoryItemCode: String?,
    var InventoryItemType: Int?,
    var InventoryItemName: String?,
    var UnitID: String?,
    var Price: Float?,
    var Description: String?,
    var Inactive: Boolean?,
    var CreatedDate: String,
    var CreatedBy: String?,
    var ModifiedBy: String?,
    var Color: String?,
    var IconFileName: String?,
    var UseCount: Int,
    var quantity: Int = 0
) : Parcelable

