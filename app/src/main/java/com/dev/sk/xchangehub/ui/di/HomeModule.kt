package com.dev.sk.xchangehub.ui.di

import com.dev.sk.xchangehub.domain.base.DataState
import com.dev.sk.xchangehub.domain.base.SynchronousUseCase
import com.dev.sk.xchangehub.domain.base.UseCase
import com.dev.sk.xchangehub.domain.datasource.CurrencyDataSource
import com.dev.sk.xchangehub.domain.datasource.DefaultLocalDataSource
import com.dev.sk.xchangehub.domain.datasource.LocalDataSource
import com.dev.sk.xchangehub.domain.datasource.NetworkDataSource
import com.dev.sk.xchangehub.domain.helper.CurrencyConverterHelper
import com.dev.sk.xchangehub.domain.model.CurrencyDTO
import com.dev.sk.xchangehub.domain.model.CurrencyRequest
import com.dev.sk.xchangehub.domain.repository.CurrencyRepository
import com.dev.sk.xchangehub.domain.repository.DefaultCurrencyRepository
import com.dev.sk.xchangehub.domain.usecase.GetCurrenciesUseCase
import com.dev.sk.xchangehub.domain.usecase.GetCurrencyConversionUseCase
import com.dev.sk.xchangehub.domain.usecase.GetSynchronousTimestampUseCase
import com.dev.sk.xchangehub.domain.usecase.SyncDataUseCase
import com.dev.sk.xchangehub.domain.usecase.UpdateSynchronousTimestampUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.flow.Flow

@Module
@InstallIn(ViewModelComponent::class)
interface HomeModule {
    @Binds
    fun bindLocalDataSource(localDataSource: DefaultLocalDataSource): LocalDataSource

    @Binds
    fun bindNetworkDataSource(networkDataSource: NetworkDataSource): CurrencyDataSource

    @Binds
    fun bindCurrencyRepository(currencyRepository: DefaultCurrencyRepository): CurrencyRepository

    companion object {

        @Provides
        fun provideGetCurrenciesUseCase(repository: CurrencyRepository): UseCase<Unit, Flow<DataState<List<CurrencyDTO>>>> {
            return GetCurrenciesUseCase(repository)
        }

        @Provides
        fun provideGetCurrencyConversionUseCase(
            repository: CurrencyRepository,
            currencyConverterHelper: CurrencyConverterHelper
        ): UseCase<CurrencyRequest, Flow<DataState<Map<CurrencyDTO, Double>>>> {
            return GetCurrencyConversionUseCase(repository, currencyConverterHelper)
        }

        @Provides
        fun provideSyncDataUseCase(repository: CurrencyRepository): UseCase<Unit, Result<Boolean>> {
            return SyncDataUseCase(repository)
        }

        @Provides
        fun provideGetSyncTimestampUseCase(repository: CurrencyRepository): SynchronousUseCase<Unit, Long> {
            return GetSynchronousTimestampUseCase(repository)
        }

        @Provides
        fun provideUpdateSyncTimestampUseCase(repository: CurrencyRepository): SynchronousUseCase<Unit, Unit> {
            return UpdateSynchronousTimestampUseCase(repository)
        }
    }
}