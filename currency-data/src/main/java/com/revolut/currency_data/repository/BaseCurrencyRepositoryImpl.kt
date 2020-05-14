package com.revolut.currency_data.repository

import com.revolut.currency_domain.models.Currency
import com.revolut.currency_domain.repository.BaseCurrencyRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

const val DEFAULT_BASE_CURRENCY = "EUR"

class BaseCurrencyRepositoryImpl(
    baseCurrency: Currency = Currency(DEFAULT_BASE_CURRENCY, 1.0, 1.0)
) : BaseCurrencyRepository {
    private val baseCurrencySubject: Subject<Currency> = BehaviorSubject.createDefault(baseCurrency)

    override fun observeBaseCurrency(): Observable<Currency> = baseCurrencySubject.hide()

    override fun putBaseCurrency(currency: Currency): Completable = Completable.fromAction {
        baseCurrencySubject.onNext(currency)
    }

}
