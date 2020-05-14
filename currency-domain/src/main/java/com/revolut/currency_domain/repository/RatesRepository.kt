package com.revolut.currency_domain.repository

import com.revolut.currency_domain.models.Rates
import io.reactivex.Maybe

interface RatesRepository {
    fun getRates(baseCurrency: String): Maybe<Rates>
    fun getRate(baseCurrency: String, targetCurrency: String): Maybe<Double>
}
