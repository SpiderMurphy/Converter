package com.revolut.currency_data.repository

import com.revolut.currency_data.api.RatesApi
import com.revolut.currency_data.repository.cache.RatesCache
import com.revolut.currency_domain.models.Rates
import com.revolut.currency_domain.repository.RatesRepository
import io.reactivex.Maybe
import java.util.concurrent.TimeUnit

const val DEFAULT_TIMEOUT_IN_MILLISECONDS = 1000L

internal class RatesRepositoryImpl(
    private val ratesApi: RatesApi,
    private val ratesCache: RatesCache,
    private val timeoutMilliseconds: Long = DEFAULT_TIMEOUT_IN_MILLISECONDS
) : RatesRepository {
    override fun getRates(baseCurrency: String): Maybe<Rates> = ratesApi
        .getRates(baseCurrency = baseCurrency)
        .timeout(timeoutMilliseconds, TimeUnit.MILLISECONDS)
        .flatMap { currencies ->
            ratesCache
                .add(baseCurrency, currencies)
                .toSingleDefault<Rates>(currencies)
        }
        .toMaybe()
        .onErrorResumeNext(
            ratesCache
                .getCache()
                .filter { it[baseCurrency] != null }
                .map { it[baseCurrency] }
        )

    override fun getRate(baseCurrency: String, targetCurrency: String): Maybe<Double> = ratesCache
        .getCache()
        .filter { currencies ->
            currencies.isNotEmpty() &&
                    currencies[baseCurrency] != null &&
                    currencies.getValue(baseCurrency).rates.containsKey(targetCurrency)
        }
        .map { it.getValue(baseCurrency).rates[targetCurrency] }
}
