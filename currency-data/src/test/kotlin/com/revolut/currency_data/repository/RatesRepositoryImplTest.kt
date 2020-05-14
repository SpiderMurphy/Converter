package com.revolut.currency_data.repository

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.revolut.currency_data.api.RatesApi
import com.revolut.currency_data.models.BackEndCurrencies
import com.revolut.currency_data.repository.cache.RatesCache
import com.revolut.currency_domain.models.Rates
import com.revolut.currency_domain.repository.RatesRepository
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class RatesRepositoryImplTest {
    @Mock
    private lateinit var ratesApiMock: RatesApi
    @Mock
    private lateinit var ratesCacheMock: RatesCache

    private lateinit var ratesRepository: RatesRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ratesRepository = RatesRepositoryImpl(ratesApiMock, ratesCacheMock)
        whenever(ratesCacheMock.add(any(), any())).thenReturn(Completable.complete())
    }

    @Test
    fun `given a base currency when fetchRates should return the correct result`() {
        // given
        val baseCurrency = "EUR"
        val returnedCurrency = "USD" to 1.2
        val rates = mock<Rates>()
        whenever(ratesApiMock.getRates(baseCurrency)).thenReturn(
            Single.just(BackEndCurrencies(baseCurrency, mapOf(returnedCurrency)))
        )
        whenever(ratesCacheMock.getCache()).thenReturn(Single.just(mapOf(baseCurrency to rates)))

        // when
        val testObserver = ratesRepository.getRates(baseCurrency).test()

        // then
        val expectedResult = BackEndCurrencies(baseCurrency, mapOf(returnedCurrency))
        testObserver.assertResult(expectedResult)
    }

    @Test
    fun `given a base currency cache and api throwing error when fetchRates should return the correct result`() {
        // given
        val baseCurrency = "EUR"
        val cachedCurrency = "USD" to 1.2
        whenever(ratesApiMock.getRates(baseCurrency)).thenReturn(Single.error(Throwable()))
        whenever(ratesCacheMock.getCache()).thenReturn(
            Single.just(
                mapOf(
                    baseCurrency to BackEndCurrencies(
                        baseCurrency,
                        mapOf(cachedCurrency)
                    )
                )
            )
        )

        // when
        val testObserver = ratesRepository.getRates(baseCurrency).test()

        // then
        val expectedResult = BackEndCurrencies(baseCurrency, mapOf(cachedCurrency))
        testObserver.assertResult(expectedResult)
    }

    @Test
    fun `given an empty cache and api throwing error when fetchRates should return the correct result`() {
        // given
        val baseCurrency = "EUR"
        whenever(ratesApiMock.getRates(baseCurrency)).thenReturn(Single.error(Throwable()))
        whenever(ratesCacheMock.getCache()).thenReturn(Single.just(emptyMap()))

        // when
        val testObserver = ratesRepository.getRates(baseCurrency).test()

        // then
        testObserver.assertResult()
    }
}
