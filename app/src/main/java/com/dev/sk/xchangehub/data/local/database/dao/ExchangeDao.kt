package com.dev.sk.xchangehub.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.dev.sk.xchangehub.data.local.database.entities.ExchangeEntity

@Dao
interface ExchangeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ExchangeEntity): Long


    @Update
    suspend fun update(entity: ExchangeEntity): Int


    @Query("SELECT * from exchangeRate")
    suspend fun getExchangeRates(): List<ExchangeEntity>

    @Query("SELECT * FROM exchangeRate WHERE currencyCode = :code LIMIT 1")
    suspend fun getByCurrencyCode(code: String): ExchangeEntity?

    @Transaction
    suspend fun updateOrInsert(entity: ExchangeEntity) {
        val existingEntity = getByCurrencyCode(entity.currencyCode)
        if (existingEntity == null) {
            insert(entity)
        } else {
            updateExchangeRate(existingEntity, entity)
        }
    }

    private suspend fun updateExchangeRate(
        existingEntity: ExchangeEntity,
        entity: ExchangeEntity
    ) {
        val updatedEntity = existingEntity.copy(conversionRate = entity.conversionRate)
        update(updatedEntity)
    }

    @Transaction
    suspend fun updateOrInsertBatch(entities: List<ExchangeEntity>) {
        entities.forEach { entity ->
            val existingEntity = getByCurrencyCode(entity.currencyCode)
            if (existingEntity == null) {
                insert(entity)
            } else {
                updateExchangeRate(existingEntity, entity)
            }
        }
    }

}