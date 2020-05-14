package com.revolut.currency_data.repository

import com.revolut.currency_domain.repository.RatesIdRepository
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

internal class RatesIdRepositoryImpl(
    defaultIds: Map<String, Long> = emptyMap(),
    startIdsCounter: Long = 0
) : RatesIdRepository {
    private val stableIdsSubject: Subject<Pair<Map<String, Long>, Long>> =
        BehaviorSubject.createDefault(
            Pair(defaultIds, startIdsCounter)
        )

    override fun getStableId(key: String): Single<Long> = stableIdsSubject
        .firstElement()
        .filter { (idsMap, _) -> idsMap.containsKey(key).not() }
        .map { (idsMap, counter) -> Pair(idsMap.plus(Pair(key, counter + 1)), counter + 1) }
        .flatMap { (map, newCounter) ->
            Completable
                .fromAction { stableIdsSubject.onNext(Pair(map, newCounter)) }
                .andThen(Maybe.just(newCounter))
        }
        .switchIfEmpty(
            stableIdsSubject
                .firstOrError()
                .map { (map, _) -> map.getValue(key) }
        )
}
