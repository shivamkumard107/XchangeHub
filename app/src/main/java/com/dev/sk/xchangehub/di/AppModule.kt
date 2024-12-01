package com.dev.sk.xchangehub.di

import android.content.Context
import android.content.SharedPreferences
import com.dev.sk.xchangehub.BuildConfig
import com.dev.sk.xchangehub.data.local.database.ExchangeDatabase
import com.dev.sk.xchangehub.data.local.database.ExchangeDatabaseBuilder
import com.dev.sk.xchangehub.data.local.database.dao.ExchangeDao
import com.dev.sk.xchangehub.data.local.sharedpref.DefaultSharedPreferenceService
import com.dev.sk.xchangehub.data.local.sharedpref.SharedPreferenceService
import com.dev.sk.xchangehub.domain.helper.CurrencyConverterHelper
import com.dev.sk.xchangehub.domain.helper.DefaultCurrencyConverterHelper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    @Binds
    fun bindCurrencyConverterHelper(currencyConverterHelper: DefaultCurrencyConverterHelper): CurrencyConverterHelper

    companion object {

        @Provides
        @ApiKey
        fun provideApiKey(): String = BuildConfig.API_KEY

        @Provides
        @BaseUrl
        fun provideBaseUrl(): String = "https://openexchangerates.org/"

        @Provides
        @Singleton
        fun getExchangeDatabase(@ApplicationContext context: Context): ExchangeDatabase {
            return ExchangeDatabaseBuilder.getInstance(context)
        }

        @Provides
        @Singleton
        fun getExchangeDao(dataBase: ExchangeDatabase): ExchangeDao {
            return dataBase.exchangeDao()
        }

        @Provides
        @Singleton
        fun provideSharedPreferences(
            @ApplicationContext context: Context
        ): SharedPreferences {
            return context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        }

        @Provides
        @Singleton
        fun provideSharedPreferencesService(
            sharedPreferences: SharedPreferences
        ): SharedPreferenceService {
            return DefaultSharedPreferenceService(sharedPreferences)
        }
    }
}