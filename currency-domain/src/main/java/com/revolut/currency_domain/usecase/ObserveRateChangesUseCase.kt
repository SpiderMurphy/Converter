package com.revolut.currency_domain.usecase

import com.revolut.currency_domain.models.Rates
import com.revolut.currency_domain.repository.RatesRepository
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

interface ObserveRatesChangeUseCase {
    fun execute(baseCurrency: String): Observable<Rates>
}

const val INTERVAL_DELAY_MS = 1000L
const val INITIAL_DELAY_MS = 0L

internal class ObserveRatesChangeUseCaseImpl(
    private val ratesRepository: RatesRepository,
    private val scheduler: Scheduler = Schedulers.computation()
) : ObserveRatesChangeUseCase {
    override fun execute(baseCurrency: String): Observable<Rates> = Observable
        .interval(INITIAL_DELAY_MS, INTERVAL_DELAY_MS, TimeUnit.MILLISECONDS, scheduler)
        .switchMapMaybe {
            ratesRepository
                .getRates(baseCurrency)
                .onErrorResumeNext(Maybe.empty())
        }
}
