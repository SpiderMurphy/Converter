package com.revolut.currency_data.models

import com.revolut.currency_domain.models.Rates
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BackEndCurrencies(
    @Json(name = "baseCurrency")
    override val baseCurrency: String,
    @Json(name = "rates")
    override val rates: Map<String, Double>
) : Rates
