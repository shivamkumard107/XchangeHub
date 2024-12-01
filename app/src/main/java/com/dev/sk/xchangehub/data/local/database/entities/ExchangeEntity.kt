package com.dev.sk.xchangehub.data.local.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchangeRate")
data class ExchangeEntity(
    @PrimaryKey val currencyCode: String,
    @ColumnInfo(name = "currencyName") val currencyName: String,
    @ColumnInfo(name = "exchangeRate") val conversionRate: Double?,
)