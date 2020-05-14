package com.revolut.currency_domain.repository

import io.reactivex.Single

interface RatesIdRepository {
    fun getStableId(key: String): Single<Long>
}
