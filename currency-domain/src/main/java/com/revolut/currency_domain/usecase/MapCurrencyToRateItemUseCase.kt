package com.revolut.currency_domain.usecase

import com.revolut.currency_domain.models.RateItem
import com.revolut.currency_domain.models.Currency
import com.revolut.currency_domain.repository.CurrenciesRepository
import com.revolut.currency_domain.repository.RatesIdRepository
import io.reactivex.Observable
import io.reactivex.Single

interface MapCurrencyToRateItemUseCase {
    fun execute(currencies: List<Currency>, languageTag: String): Single<List<RateItem>>
}

const val DEFAULT_NAME_IF_ERROR = ""

internal class MapCurrencyToRateItemUseCaseImpl(
    private val ratesIdRepository: RatesIdRepository,
    private val currenciesRepository: CurrenciesRepository
) : MapCurrencyToRateItemUseCase {
    override fun execute(
        currencies: List<Currency>,
        languageTag: String
    ): Single<List<RateItem>> = Observable
        .fromIterable(currencies)
        .concatMapSingle { currency ->
            currenciesRepository
                .getCurrencyName(currency.currencyCode, languageTag)
                .map { Pair(currency, it) }
                .onErrorReturnItem(Pair(currency, DEFAULT_NAME_IF_ERROR))
        }
        .concatMapSingle { (currency, currencyName) ->
            ratesIdRepository
                .getStableId(currency.currencyCode)
                .map { stableId ->
                    RateItem(
                        stableId,
                        currency.currencyCode,
                        currencyName,
                        currency.amount
                    )
                }
        }
        .collectInto(mutableListOf<RateItem>()) { rateItems, rateItem ->
            rateItems.add(rateItem)
        }
        .map { it.toList() }
}
