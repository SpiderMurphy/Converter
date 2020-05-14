package com.revolut.currency.rates

import com.revolut.common_mvi.MviInteractor
import com.revolut.currency_domain.models.Currency
import com.revolut.currency_domain.models.Rates
import com.revolut.currency_domain.usecase.*
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.functions.BiFunction

class RatesInteractor(
    private val observeRatesChangeUseCase: ObserveRatesChangeUseCase,
    private val observeSelectedCurrencyUseCase: ObserveSelectedCurrencyUseCase,
    private val observeBaseCurrencyUseCase: ObserveBaseCurrencyUseCase,
    private val setSelectedCurrencyUseCase: SetSelectedCurrencyUseCase,
    private val mapRatesItemUseCase: MapRatesItemUseCase,
    private val mapCurrencyToRateItemUseCase: MapCurrencyToRateItemUseCase,
    private val ioScheduler: Scheduler
) : MviInteractor<RatesIntent, RatesResult> {

    private val getRatesProcessor: ObservableTransformer<RatesIntent.GetRates, RatesResult.GetRates> =
        ObservableTransformer { upstream ->
            upstream
                .observeOn(ioScheduler)
                .switchMap { intent ->
                    observeBaseCurrencyUseCase
                        .execute()
                        .switchMap { baseCurrency ->
                            Observable.combineLatest(
                                observeRatesChangeUseCase.execute(baseCurrency.currencyCode),
                                observeSelectedCurrencyUseCase.execute(),
                                BiFunction { rates: Rates, currency: Currency ->
                                    Triple(
                                        rates,
                                        currency,
                                        baseCurrency
                                    )
                                })
                        }.switchMapSingle { (rates, selectedCurrency, baseCurrency) ->
                            mapCurrencyToRateItemUseCase
                                .execute(
                                    getBaseAndSelectedCurrencyArgs(
                                        baseCurrency,
                                        selectedCurrency
                                    ), intent.languageTag
                                )
                                .flatMap { items ->
                                    mapRatesItemUseCase
                                        .execute(rates, intent.languageTag, baseCurrency.amount)
                                        .map { rateItems ->
                                            items.plus(rateItems.filter {
                                                it.currencyCode != selectedCurrency.currencyCode
                                            })
                                        }
                                }
                        }.map { RatesResult.GetRates(it) }
                }
        }

    private val setAmountProcessor: ObservableTransformer<RatesIntent.SetCurrency, RatesResult> =
        ObservableTransformer { upstream ->
            upstream.switchMapCompletable {
                setSelectedCurrencyUseCase
                    .execute(it.currencyCode, it.amount)
            }.toObservable<RatesResult>()
        }

    override val intentProcessor: ObservableTransformer<RatesIntent, RatesResult> =
        ObservableTransformer { intents ->
            intents
                .publish { shared ->
                    Observable.mergeArray(
                        shared.ofType(RatesIntent.GetRates::class.java).compose(getRatesProcessor),
                        shared.ofType(RatesIntent.SetCurrency::class.java).compose(
                            setAmountProcessor
                        ),
                        shared.ofType(RatesIntent.SelectedCurrencyChanged::class.java).map {
                            RatesResult.SelectedCurrencyChanged(it.oldCurrencyCode != it.newCurrencyCode)
                        }
                    )
                }
        }

    private fun getBaseAndSelectedCurrencyArgs(baseCurrency: Currency, selectedCurrency: Currency) =
        mutableListOf<Currency>()
            .apply {
                if (baseCurrency.currencyCode != selectedCurrency.currencyCode) {
                    add(selectedCurrency)
                    add(baseCurrency)
                } else {
                    add(selectedCurrency)
                }
            }.toList()
}
