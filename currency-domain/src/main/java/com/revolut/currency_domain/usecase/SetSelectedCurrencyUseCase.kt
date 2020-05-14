package com.revolut.currency_domain.usecase

import com.revolut.currency_domain.models.Currency
import com.revolut.currency_domain.repository.BaseCurrencyRepository
import com.revolut.currency_domain.repository.RatesRepository
import com.revolut.currency_domain.repository.SelectedCurrencyRepository
import io.reactivex.Completable
import io.reactivex.Maybe

interface SetSelectedCurrencyUseCase {
    fun execute(currencyCode: String, amount: Double): Completable
}

const val DEFAULT_RATE_IF_EMPTY = 1.0

internal class SetSelectedCurrencyUseCaseImpl(
    private val selectedCurrencyRepository: SelectedCurrencyRepository,
    private val baseCurrencyRepository: BaseCurrencyRepository,
    private val ratesRepository: RatesRepository
) : SetSelectedCurrencyUseCase {
    override fun execute(currencyCode: String, amount: Double): Completable = baseCurrencyRepository
        .observeBaseCurrency()
        .firstOrError()
        .flatMapMaybe { baseCurrency ->
            getRate(baseCurrency, currencyCode)
                .map { Pair(baseCurrency, Currency(currencyCode, amount, it)) }
        }
        .flatMapCompletable { (baseCurrency, targetCurrency) ->
            baseCurrencyRepository
                .putBaseCurrency(
                    baseCurrency.copy(
                        amount = targetCurrency.amount.div(
                            targetCurrency.rate
                        )
                    )
                ).concatWith(selectedCurrencyRepository.putSelectedCurrency(targetCurrency))
        }

    private fun getRate(baseCurrency: Currency, currencyCode: String): Maybe<Double> =
        ratesRepository
            .getRate(baseCurrency.currencyCode, currencyCode)
            .switchIfEmpty(Maybe.just(DEFAULT_RATE_IF_EMPTY))
}
