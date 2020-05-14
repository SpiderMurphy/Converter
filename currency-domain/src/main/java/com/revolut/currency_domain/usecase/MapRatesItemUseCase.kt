package com.revolut.currency_domain.usecase

import com.revolut.currency_domain.repository.RatesIdRepository
import com.revolut.currency_domain.models.RateItem
import com.revolut.currency_domain.models.Rates
import com.revolut.currency_domain.repository.CurrenciesRepository
import io.reactivex.Observable
import io.reactivex.Single

interface MapRatesItemUseCase {
    fun execute(rates: Rates, languageTag: String, amount: Double): Single<List<RateItem>>
}

const val DEFAULT_NAME_ON_ERROR = ""

internal class MapRatesItemUseCaseImpl(
    private val currenciesRepository: CurrenciesRepository,
    private val ratesIdRepository: RatesIdRepository
) : MapRatesItemUseCase {
    override fun execute(
        rates: Rates,
        languageTag: String,
        amount: Double
    ): Single<List<RateItem>> = Observable
        .fromIterable(rates.rates.entries)
        .concatMapSingle { (currencyCode, rateValue) ->
            ratesIdRepository
                .getStableId(currencyCode)
                .map { Triple(currencyCode, rateValue, it) }
        }
        .concatMapSingle { (currencyCode, rateValue, stableId) ->
            currenciesRepository
                .getCurrencyName(currencyCode, languageTag)
                .onErrorReturnItem(DEFAULT_NAME_ON_ERROR)
                .map { currencyName ->
                    RateItem(
                        stableId,
                        currencyCode,
                        currencyName,
                        rateValue.times(amount)
                    )
                }
        }
        .collectInto<MutableList<RateItem>>(mutableListOf()) { ratesItems, rateItem ->
            ratesItems.add(rateItem)
        }
        .map { it.toList() }
}
