package com.dev.sk.xchangehub.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dev.sk.xchangehub.data.local.database.dao.ExchangeDao
import com.dev.sk.xchangehub.data.local.database.entities.ExchangeEntity


@Database(entities = [ExchangeEntity::class], version = 1, exportSchema = false)
abstract class ExchangeDatabase : RoomDatabase() {
    abstract fun exchangeDao(): ExchangeDao
}