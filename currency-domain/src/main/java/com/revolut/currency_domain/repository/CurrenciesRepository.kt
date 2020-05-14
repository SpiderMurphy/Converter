package com.revolut.currency_domain.repository

import io.reactivex.Single

interface CurrenciesRepository {
    fun getCurrencyName(currencyCode: String, languageTag: String): Single<String>
}
