package com.example.appquanly.ThucDon

data class InventoryItem(
    var InventoryItemID: String,
    var InventoryItemCode: String?,
    var InventoryItemType: Int?,
    var InventoryItemName: String?,
    var UnitlD: String?,
    var Price:  Float?,
    var Description: String?,
    var Inactive: Boolean?,
    var CreatedDate: String,
    var CreatedBy: String?,
    var ModifiedBy: String?,
    var Color: String?,
    var IconFileName: String?,
    var UseCount: Int,



    )