package com.revolut.currency_data.api

import com.revolut.currency_data.models.BackEndCurrencies
import com.revolut.currency_domain.models.Rates
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

internal interface RatesApi {
    @GET("latest")
    fun getRates(@Query("base") baseCurrency: String): Single<BackEndCurrencies>
}
