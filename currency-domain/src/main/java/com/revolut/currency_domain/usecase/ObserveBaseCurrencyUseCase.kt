package com.revolut.currency_domain.usecase

import com.revolut.currency_domain.models.Currency
import com.revolut.currency_domain.repository.BaseCurrencyRepository
import io.reactivex.Observable

interface ObserveBaseCurrencyUseCase {
    fun execute(): Observable<Currency>
}

internal class ObserveBaseCurrencyUseCaseImpl(
    private val baseCurrencyRepository: BaseCurrencyRepository
) : ObserveBaseCurrencyUseCase {
    override fun execute(): Observable<Currency> = baseCurrencyRepository
        .observeBaseCurrency()
        .distinctUntilChanged()
}
