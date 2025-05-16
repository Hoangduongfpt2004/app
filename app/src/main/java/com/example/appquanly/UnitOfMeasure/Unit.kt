package com.example.appquanly.UnitOfMeasure

import java.time.LocalDateTime

data class UnitOfMeasure(
    var UnitID: String,
    var UnitName: String,
    var Description: String,
    var Inactive: Boolean,
    var CreatedBy: String,
    var CreatedDate: LocalDateTime,
    var ModifiedDate: LocalDateTime,
    var ModifiedBy: String
)
