package com.revolut.currency.rates

import com.revolut.currency_domain.models.RateItem

sealed class RatesIntent {
    data class GetRates(
        val baseCurrency: String,
        val languageTag: String
    ) : RatesIntent()

    data class SetCurrency(val currencyCode: String, val amount: Double) : RatesIntent()
    data class SelectedCurrencyChanged(
        val oldCurrencyCode: String,
        val newCurrencyCode: String
    ) : RatesIntent()
}

sealed class RatesResult {
    data class GetRates(val rates: List<RateItem>) : RatesResult()
    data class SelectedCurrencyChanged(val scrollToTop: Boolean) : RatesResult()
}

data class RatesAdapterData(
    val stableId: Long,
    val isSelected: Boolean,
    val currencyCode: String,
    val currencyName: String,
    val currencyAmount: String
)

data class RatesState(
    val scrollToTop: Boolean,
    val rates: List<RatesAdapterData>
)
