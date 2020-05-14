package com.revolut.currency_data.repository

import com.revolut.currency_domain.repository.CurrenciesRepository
import io.reactivex.Single
import java.util.Currency
import java.util.Locale

internal class CurrenciesRepositoryImpl : CurrenciesRepository {
    override fun getCurrencyName(currencyCode: String, languageTag: String): Single<String> =
        Single.fromCallable {
            Currency
                .getInstance(currencyCode)
                .getDisplayName(Locale.forLanguageTag(languageTag))
        }
}
