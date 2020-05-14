package com.revolut.currency_domain.usecase

import com.nhaarman.mockito_kotlin.whenever
import com.revolut.currency_domain.models.Currency
import com.revolut.currency_domain.models.RateItem
import com.revolut.currency_domain.repository.CurrenciesRepository
import com.revolut.currency_domain.repository.RatesIdRepository
import io.reactivex.Single
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MapCurrencyToRateItemUseCaseImplTest {
    @Mock
    private lateinit var rateIdRepositoryMock: RatesIdRepository
    @Mock
    private lateinit var currenciesRepositoryMock: CurrenciesRepository
    private lateinit var mapCurrencyToReteItemUseCase: MapCurrencyToRateItemUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mapCurrencyToReteItemUseCase = MapCurrencyToRateItemUseCaseImpl(
            rateIdRepositoryMock,
            currenciesRepositoryMock
        )
    }

    @Test
    fun `given multiple currencies when execute should emit the correct result`() {
        // given
        val currencies = listOf(
            Currency("EUR", 1.0, 1.0),
            Currency("USD", 1.89, 0.89)
        )
        whenever(rateIdRepositoryMock.getStableId("EUR")).thenReturn(Single.just(1))
        whenever(rateIdRepositoryMock.getStableId("USD")).thenReturn(Single.just(2))
        whenever(currenciesRepositoryMock.getCurrencyName("EUR", "en"))
            .thenReturn(Single.just("Euro"))
        whenever(currenciesRepositoryMock.getCurrencyName("USD", "en"))
            .thenReturn(Single.just("Usd Dollar"))

        // when
        val testObserver = mapCurrencyToReteItemUseCase.execute(currencies, "en").test()

        // then
        val expectedResult = listOf(
            RateItem(1, "EUR", "Euro", 1.0),
            RateItem(2, "USD", "Usd Dollar", 1.89)
        )
        testObserver.assertResult(expectedResult)
    }

    @Test
    fun `given an error on name translation when execute should emit the correct result`() {
        // given
        val currencies = listOf(
            Currency("EUR", 1.0, 1.0),
            Currency("USD", 1.89, 0.89)
        )
        whenever(rateIdRepositoryMock.getStableId("EUR")).thenReturn(Single.just(1))
        whenever(rateIdRepositoryMock.getStableId("USD")).thenReturn(Single.just(2))
        whenever(currenciesRepositoryMock.getCurrencyName("EUR", "en"))
            .thenReturn(Single.error(Throwable()))
        whenever(currenciesRepositoryMock.getCurrencyName("USD", "en"))
            .thenReturn(Single.just("Usd Dollar"))

        // when
        val testObserver = mapCurrencyToReteItemUseCase.execute(currencies, "en").test()

        // then
        val expectedResult = listOf(
            RateItem(1, "EUR", DEFAULT_NAME_IF_ERROR, 1.0),
            RateItem(2, "USD", "Usd Dollar", 1.89)
        )
        testObserver.assertResult(expectedResult)
    }

    @Test
    fun `given an error on id resolution when execute should emit the correct result`() {
        // given
        val currencies = listOf(Currency("EUR", 1.0, 1.0))
        whenever(rateIdRepositoryMock.getStableId("EUR")).thenReturn(
            Single.error(Throwable("error"))
        )
        whenever(currenciesRepositoryMock.getCurrencyName("EUR", "en"))
            .thenReturn(Single.error(Throwable()))

        // when
        val testObserver = mapCurrencyToReteItemUseCase.execute(currencies, "en").test()

        // then
        testObserver.assertErrorMessage("error")
    }
}
