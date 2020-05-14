package com.revolut.currency_domain.usecase

import com.revolut.currency_domain.models.Currency
import com.revolut.currency_domain.repository.SelectedCurrencyRepository
import io.reactivex.Observable
import io.reactivex.Single

interface ObserveSelectedCurrencyUseCase {
    fun execute(): Observable<Currency>
}

internal class ObserveSelectedCurrencyUseCaseImpl(
    private val selectedCurrencyRepository: SelectedCurrencyRepository
) : ObserveSelectedCurrencyUseCase {
    override fun execute(): Observable<Currency> = selectedCurrencyRepository
        .observeSelectedCurrency()
        .distinctUntilChanged()
}
