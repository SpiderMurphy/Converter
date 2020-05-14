package com.revolut.currency_domain

import com.revolut.currency_domain.usecase.*
import com.revolut.currency_domain.usecase.MapRatesItemUseCaseImpl
import com.revolut.currency_domain.usecase.ObserveRatesChangeUseCaseImpl
import org.koin.dsl.module

val currencyDomainModule = module {
    factory<ObserveRatesChangeUseCase> { ObserveRatesChangeUseCaseImpl(get()) }
    factory<MapRatesItemUseCase> {
        MapRatesItemUseCaseImpl(
            get(),
            get()
        )
    }
    factory<MapCurrencyToRateItemUseCase> { MapCurrencyToRateItemUseCaseImpl(get(), get()) }
    factory<SetSelectedCurrencyUseCase> { SetSelectedCurrencyUseCaseImpl(get(), get(), get()) }
    factory<ObserveSelectedCurrencyUseCase> { ObserveSelectedCurrencyUseCaseImpl(get()) }
    factory<ObserveBaseCurrencyUseCase> { ObserveBaseCurrencyUseCaseImpl(get()) }
}
