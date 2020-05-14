package com.revolut.currency_domain.usecase

import com.nhaarman.mockito_kotlin.whenever
import com.revolut.currency_domain.repository.RatesIdRepository
import com.revolut.currency_domain.models.RateItem
import com.revolut.currency_domain.models.Rates
import com.revolut.currency_domain.repository.CurrenciesRepository
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MapRatesItemUseCaseImplTest {
    @Mock
    private lateinit var ratesIdRepositoryMock: RatesIdRepository
    @Mock
    private lateinit var currenciesRepositoryMock: CurrenciesRepository
    @Mock
    private lateinit var ratesMock: Rates
    private lateinit var mapRatesItemUseCase: MapRatesItemUseCase
    private val currencyAmount = 1.0

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mapRatesItemUseCase =
            MapRatesItemUseCaseImpl(
                currenciesRepositoryMock,
                ratesIdRepositoryMock
            )
    }

    @Test
    fun `given baseCurrency, rates, languageTag when execute should return the correct results`() {
        // given
        val baseCurrency = "EUR"
        val languageTag = "en"
        val currencyCode1 = "AUD"
        val currencyCode2 = "USD"
        val stableId1 = 1L
        val stableId2 = 2L
        val ratesMap = mapOf(currencyCode1 to 1.0, currencyCode2 to 1.0)
        whenever(ratesMock.rates).thenReturn(ratesMap)
        whenever(ratesMock.baseCurrency).thenReturn(baseCurrency)
        whenever(currenciesRepositoryMock.getCurrencyName(currencyCode1, languageTag))
            .thenReturn(Single.just(currencyCode1))
        whenever(currenciesRepositoryMock.getCurrencyName(currencyCode2, languageTag))
            .thenReturn(Single.just(currencyCode2))
        whenever(ratesIdRepositoryMock.getStableId(currencyCode1)).thenReturn(Single.just(stableId1))
        whenever(ratesIdRepositoryMock.getStableId(currencyCode2)).thenReturn(Single.just(stableId2))

        // when
        val testObserver =
            mapRatesItemUseCase.execute(ratesMock, languageTag, currencyAmount).test()

        // then
        val expectedResult = listOf(
            RateItem(
                stableId1,
                currencyCode1,
                currencyCode1,
                currencyAmount
            ),
            RateItem(
                stableId2,
                currencyCode2,
                currencyCode2,
                currencyAmount
            )
        )
        testObserver.assertResult(expectedResult)
    }


    @Test
    fun `given baseCurrency, rates, languageTag and exception retrieving name when execute should emit the return result `() {
        // given
        val baseCurrency = "EUR"
        val languageTag = "en"
        val currencyCode1 = "AUD"
        val stableId = 1L
        val ratesMap = mapOf(currencyCode1 to 1.0)
        whenever(ratesMock.rates).thenReturn(ratesMap)
        whenever(ratesMock.baseCurrency).thenReturn(baseCurrency)
        whenever(currenciesRepositoryMock.getCurrencyName(currencyCode1, languageTag))
            .thenReturn(Single.error(Throwable()))
        whenever(ratesIdRepositoryMock.getStableId(currencyCode1)).thenReturn(Single.just(stableId))

        // when
        val testObserver =
            mapRatesItemUseCase.execute(ratesMock, languageTag, currencyAmount).test()

        // then
        val expectResult = RateItem(
            stableId,
            currencyCode1,
            DEFAULT_NAME_ON_ERROR,
            currencyAmount
        )
        testObserver.assertResult(listOf(expectResult))
    }

    @Test
    fun `given baseCurrency, empty Rates, languageTag when execute should return the correct result`() {
        // given
        val baseCurrency = "EUR"
        val languageTag = "en"
        val ratesMap = emptyMap<String, Double>()
        whenever(ratesMock.baseCurrency).thenReturn(baseCurrency)
        whenever(ratesMock.rates).thenReturn(ratesMap)

        // when
        val tesObserver = mapRatesItemUseCase.execute(ratesMock, languageTag, currencyAmount).test()

        // then
        tesObserver
            .assertResult(emptyList())
    }
}
