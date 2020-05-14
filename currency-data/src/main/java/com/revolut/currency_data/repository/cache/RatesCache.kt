package com.revolut.currency_data.repository.cache

import com.revolut.currency_domain.models.Rates
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

internal interface RatesCache {
    fun add(baseCurrency: String, rates: Rates): Completable
    fun getCache(): Single<Map<String, Rates>>
}

internal class RatesCacheImpl(
    defaultCacheData: Map<String, Rates> = emptyMap()
) : RatesCache {
    private val cacheSubject: Subject<Map<String, Rates>> = BehaviorSubject
        .createDefault(defaultCacheData)

    override fun add(baseCurrency: String, rates: Rates): Completable = cacheSubject
        .first(emptyMap())
        .flatMapCompletable { cacheData ->
            Completable.fromAction { cacheSubject.onNext(cacheData.plus(baseCurrency to rates)) }
        }

    override fun getCache(): Single<Map<String, Rates>> = cacheSubject.first(emptyMap())
}
