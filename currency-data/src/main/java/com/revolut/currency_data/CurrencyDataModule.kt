package com.revolut.currency_data

import com.revolut.currency_data.api.RatesApi
import com.revolut.currency_data.repository.*
import com.revolut.currency_data.repository.CurrenciesRepositoryImpl
import com.revolut.currency_data.repository.RatesIdRepositoryImpl
import com.revolut.currency_data.repository.RatesRepositoryImpl
import com.revolut.currency_data.repository.cache.RatesCache
import com.revolut.currency_data.repository.cache.RatesCacheImpl
import com.revolut.currency_domain.models.RateItem
import com.revolut.currency_domain.repository.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_API_URL = "https://hiring.revolut.codes/api/android/"

val currencyDataModule = module {
    single<RatesRepository> { RatesRepositoryImpl(get(), get()) }
    single<CurrenciesRepository> { CurrenciesRepositoryImpl() }
    single<RatesIdRepository> { RatesIdRepositoryImpl() }
    single<SelectedCurrencyRepository> { SelectedCurrencyRepositoryImpl() }
    single<BaseCurrencyRepository> { BaseCurrencyRepositoryImpl() }
    factory<RatesCache> { RatesCacheImpl() }

    single<RatesApi> {
        Retrofit
            .Builder()
            .baseUrl(BASE_API_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(RatesApi::class.java)
    }
}
