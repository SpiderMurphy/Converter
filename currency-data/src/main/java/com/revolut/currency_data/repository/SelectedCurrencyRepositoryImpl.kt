package com.revolut.currency_data.repository

import com.revolut.currency_domain.models.Currency
import com.revolut.currency_domain.repository.SelectedCurrencyRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

private const val DEFAULT_CURRENCY = "EUR"
private const val DEFAULT_AMOUNT = 1.0
private const val DEFAULT_RATE = 1.0

class SelectedCurrencyRepositoryImpl(
    defaultCurrency: Currency = Currency(DEFAULT_CURRENCY, DEFAULT_AMOUNT, DEFAULT_RATE)
) : SelectedCurrencyRepository {
    private val currencySubject: Subject<Currency> =
        BehaviorSubject.createDefault(defaultCurrency)

    override fun observeSelectedCurrency(): Observable<Currency> = currencySubject.hide()

    override fun putSelectedCurrency(currency: Currency): Completable =
        Completable.fromAction {
            currencySubject.onNext(currency)
        }

}
