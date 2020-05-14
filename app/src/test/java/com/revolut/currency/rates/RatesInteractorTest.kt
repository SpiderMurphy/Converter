package com.revolut.currency.rates

import com.nhaarman.mockito_kotlin.whenever
import com.revolut.common_mvi.MviInteractor
import com.revolut.currency_domain.models.Currency
import com.revolut.currency_domain.models.RateItem
import com.revolut.currency_domain.models.Rates
import com.revolut.currency_domain.usecase.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class RatesInteractorTest {
    @Mock
    private lateinit var observeRatesChangeUseCaseMock: ObserveRatesChangeUseCase
    @Mock
    private lateinit var observeSelectedCurrencyUseCaseMock: ObserveSelectedCurrencyUseCase
    @Mock
    private lateinit var observeBaseCurrencyUseCaseMock: ObserveBaseCurrencyUseCase
    @Mock
    private lateinit var setSelectedCurrencyUseCaseMock: SetSelectedCurrencyUseCase
    @Mock
    private lateinit var mapRatesItemUseCaseMock: MapRatesItemUseCase
    @Mock
    private lateinit var mapCurrencyToRateItemUseCaseMock: MapCurrencyToRateItemUseCase
    @Mock
    private lateinit var ratesMock: Rates

    private val scheduler = Schedulers.trampoline()

    private lateinit var ratesInteractor: MviInteractor<RatesIntent, RatesResult>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ratesInteractor = RatesInteractor(
            observeRatesChangeUseCaseMock,
            observeSelectedCurrencyUseCaseMock,
            observeBaseCurrencyUseCaseMock,
            setSelectedCurrencyUseCaseMock,
            mapRatesItemUseCaseMock,
            mapCurrencyToRateItemUseCaseMock,
            scheduler
        )
    }

    @Test
    fun `given a base currency, selected and rate changes when processed should emit the correct result`() {
        // given
        val baseCurrency = Currency("EUR", 0.5, 1.0)
        val selectedCurrency = Currency("USD", 1.0, 2.0)
        val languageTag = "en"
        val rateItemSelected = RateItem(
            2, selectedCurrency.currencyCode, "US Dollar",
            selectedCurrency.rate
        )
        val rateItemBase = RateItem(
            1,
            baseCurrency.currencyCode,
            "Euro",
            baseCurrency.rate
        )
        whenever(ratesMock.baseCurrency).thenReturn(baseCurrency.currencyCode)
        whenever(ratesMock.rates).thenReturn(mapOf(selectedCurrency.currencyCode to selectedCurrency.rate))
        whenever(observeBaseCurrencyUseCaseMock.execute()).thenReturn(
            Observable.just(baseCurrency)
        )
        whenever(observeSelectedCurrencyUseCaseMock.execute()).thenReturn(
            Observable.just(selectedCurrency)
        )
        whenever(observeRatesChangeUseCaseMock.execute(baseCurrency.currencyCode)).thenReturn(
            Observable.just(ratesMock)
        )
        whenever(
            mapCurrencyToRateItemUseCaseMock.execute(
                listOf(selectedCurrency, baseCurrency),
                languageTag
            )
        ).thenReturn(Single.just(listOf(rateItemSelected, rateItemBase)))
        whenever(mapRatesItemUseCaseMock.execute(ratesMock, languageTag, baseCurrency.amount))
            .thenReturn(Single.just(listOf(rateItemSelected)))

        // when
        val testObserver = Observable
            .just(RatesIntent.GetRates(baseCurrency.currencyCode, languageTag))
            .compose(ratesInteractor.intentProcessor)
            .test()

        // then
        val expectedResult = RatesResult.GetRates(listOf(rateItemSelected, rateItemBase))
        testObserver.assertResult(expectedResult)
    }

    @Test
    fun `given a base currency, selected same as base and rate changes when processed should emit the correct result`() {
        // given
        val baseCurrency = Currency("EUR", 1.0, 1.0)
        val selectedCurrency = Currency("EUR", 1.0, 1.0)
        val languageTag = "en"
        val rateItemBase = RateItem(
            1,
            baseCurrency.currencyCode,
            "Euro",
            baseCurrency.rate
        )
        val rateItemAdditional = RateItem(
            2, "USD", "US Dollar",
            0.5
        )
        whenever(ratesMock.baseCurrency).thenReturn(baseCurrency.currencyCode)
        whenever(ratesMock.rates).thenReturn(mapOf(rateItemAdditional.currencyCode to 2.0))
        whenever(observeBaseCurrencyUseCaseMock.execute()).thenReturn(Observable.just(baseCurrency))
        whenever(observeSelectedCurrencyUseCaseMock.execute()).thenReturn(
            Observable.just(selectedCurrency)
        )
        whenever(observeRatesChangeUseCaseMock.execute(baseCurrency.currencyCode)).thenReturn(
            Observable.just(ratesMock)
        )
        whenever(mapCurrencyToRateItemUseCaseMock.execute(listOf(baseCurrency), languageTag))
            .thenReturn(Single.just(listOf(rateItemBase)))
        whenever(mapRatesItemUseCaseMock.execute(ratesMock, languageTag, baseCurrency.amount))
            .thenReturn(Single.just(listOf(rateItemAdditional)))

        // when
        val testObserver = Observable
            .just(RatesIntent.GetRates(baseCurrency.currencyCode, languageTag))
            .compose(ratesInteractor.intentProcessor)
            .test()

        // then
        val expectedResult = RatesResult.GetRates(listOf(rateItemBase, rateItemAdditional))
        testObserver.assertResult(expectedResult)
    }
}
