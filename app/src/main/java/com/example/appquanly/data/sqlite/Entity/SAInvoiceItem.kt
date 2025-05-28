package com.example.appquanly.data.sqlite.Entity

import java.io.Serializable


data class SAInvoiceItem(
    val refId: String,
    val refType: Int,
    val refNo: String,
    val refDate: Long,
    var amount: Double,
    val returnAmount: Double,
    val receiveAmount: Double,
    val remainAmount: Double,
    val journalMemo: String?,
    val paymentStatus: Int,
    val numberOfPeople: Int,
    val tableName: String?,
    val listItemName: String? = null,
    val createdDate: Long?,
    val createdBy: String?,
    val modifiedDate: Long?,
    val modifiedBy: String?,
    var quantity: Int = 0
) : Serializable

