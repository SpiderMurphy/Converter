package com.revolut.currency

import com.revolut.common_mvi.MviInteractor
import com.revolut.common_mvi.MviPresenter
import com.revolut.currency.rates.RatesIntent
import com.revolut.currency.rates.RatesPresenter
import com.revolut.currency.rates.RatesResult
import com.revolut.currency.rates.RatesState
import com.revolut.currency.rates.RatesInteractor
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.computation
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val RATES_PRESENTER = "rates_presenter"
const val RATES_INTERACTOR = "rates_interactor"

val currencyAppModule = module {
    factory<MviPresenter<RatesIntent, RatesState>>(named(RATES_PRESENTER)) {
        RatesPresenter(
            get(named(RATES_INTERACTOR))
        )
    }
    factory<MviInteractor<RatesIntent, RatesResult>>(named(RATES_INTERACTOR)) {
        RatesInteractor(get(), get(), get(), get(), get(), get(), Schedulers.io())
    }
}
