package com.revolut.currency_domain.repository

import com.revolut.currency_domain.models.Currency
import io.reactivex.Completable
import io.reactivex.Observable

interface SelectedCurrencyRepository {
    fun observeSelectedCurrency(): Observable<Currency>
    fun putSelectedCurrency(currency: Currency): Completable
}
