package com.dev.sk.xchangehub.data.local.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dev.sk.xchangehub.data.local.database.dao.ExchangeDao
import com.dev.sk.xchangehub.data.local.database.entities.ExchangeEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExchangeDaoTest {

    private lateinit var database: ExchangeDatabase
    private lateinit var exchangeDao: ExchangeDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ExchangeDatabase::class.java
        ).allowMainThreadQueries()
            .build()

        exchangeDao = database.exchangeDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insert_and_getExchangeRates() = runBlocking {
        val entity = ExchangeEntity(
            currencyCode = "USD",
            currencyName = "United States Dollar",
            conversionRate = 1.0
        )
        val id = exchangeDao.insert(entity)
        assertTrue(id > 0)

        val rates = exchangeDao.getExchangeRates()
        assertEquals(1, rates.size)
        assertEquals("USD", rates[0].currencyCode)
    }

    @Test
    fun update_and_getByCurrencyCode() = runBlocking {
        val entity =
            ExchangeEntity(currencyCode = "EUR", currencyName = "Euro", conversionRate = 0.9)
        exchangeDao.insert(entity)

        val updatedEntity = entity.copy(conversionRate = 1.1)
        val updateCount = exchangeDao.update(updatedEntity)
        assertEquals(1, updateCount)

        val retrievedEntity = exchangeDao.getByCurrencyCode("EUR")
        assertNotNull(retrievedEntity)
        assertEquals(1.1, retrievedEntity?.conversionRate)
    }

    @Test
    fun updateOrInsert_insertsWhenNotPresent() = runBlocking {
        val entity =
            ExchangeEntity(currencyCode = "GBP", currencyName = "Pound", conversionRate = 0.8)
        exchangeDao.updateOrInsert(entity)

        val retrievedEntity = exchangeDao.getByCurrencyCode("GBP")
        assertNotNull(retrievedEntity)
        assertEquals(0.8, retrievedEntity?.conversionRate)
    }

    @Test
    fun updateOrInsert_updatesWhenPresent() = runBlocking {
        val entity =
            ExchangeEntity(currencyCode = "JPY", currencyName = "Yen", conversionRate = 110.0)
        exchangeDao.insert(entity)

        val updatedEntity =
            ExchangeEntity(currencyCode = "JPY", currencyName = "Yen", conversionRate = 120.0)
        exchangeDao.updateOrInsert(updatedEntity)

        val retrievedEntity = exchangeDao.getByCurrencyCode("JPY")
        assertNotNull(retrievedEntity)
        assertEquals(120.0, retrievedEntity?.conversionRate)
    }
}