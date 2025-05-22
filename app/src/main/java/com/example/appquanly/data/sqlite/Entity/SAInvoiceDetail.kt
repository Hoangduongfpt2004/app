package com.example.appquanly.data.sqlite.Entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SAInvoiceDetail(
    val RefDetailID: String,
    val RefDetailType: Int,
    val RefID: String,
    val InventoryItemID: String,
    val InventoryItemName: String,
    val UnitID: String,
    val UnitName: String,
    val Quantity: Float,
    val UnitPrice: Float,
    val Amount: Float,
    val Description: String,
    val SortOrder: Int,
    val CreatedDate: Long?,
    val CreatedBy: String,
    val ModifiedDate: Long?,
    val ModifiedBy: String
) : Parcelable

