package com.dev.sk.xchangehub.data.local.database

import android.content.Context
import androidx.room.Room

object ExchangeDatabaseBuilder {
    @Volatile
    private var INSTANCE: ExchangeDatabase? = null

    fun getInstance(context: Context): ExchangeDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                ExchangeDatabase::class.java,
                "exchange_db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}