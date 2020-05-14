package com.revolut.currency_domain.repository

import com.revolut.currency_domain.models.Currency
import io.reactivex.Completable
import io.reactivex.Observable

interface BaseCurrencyRepository {
    fun observeBaseCurrency(): Observable<Currency>
    fun putBaseCurrency(currency: Currency): Completable
}
