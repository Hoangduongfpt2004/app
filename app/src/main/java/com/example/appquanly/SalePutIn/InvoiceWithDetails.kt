package com.example.appquanly.SalePutIn

import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem


data class InvoiceWithDetails (
    val invoice:SAInvoiceItem,
    val details: List<SAInvoiceDetail>
)
