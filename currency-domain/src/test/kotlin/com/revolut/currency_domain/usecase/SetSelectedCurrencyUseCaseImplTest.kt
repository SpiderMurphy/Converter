package com.revolut.currency_domain.usecase

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.revolut.currency_domain.models.Currency
import com.revolut.currency_domain.repository.BaseCurrencyRepository
import com.revolut.currency_domain.repository.RatesRepository
import com.revolut.currency_domain.repository.SelectedCurrencyRepository
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class SetSelectedCurrencyUseCaseImplTest {
    @Mock
    private lateinit var selectedCurrencyRepositoryMock: SelectedCurrencyRepository
    @Mock
    private lateinit var baseCurrencyRepositoryMock: BaseCurrencyRepository
    @Mock
    private lateinit var ratesRepositoryMock: RatesRepository
    private lateinit var setSelectedCurrencyUseCase: SetSelectedCurrencyUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        setSelectedCurrencyUseCase = SetSelectedCurrencyUseCaseImpl(
            selectedCurrencyRepositoryMock,
            baseCurrencyRepositoryMock,
            ratesRepositoryMock
        )
        whenever(baseCurrencyRepositoryMock.putBaseCurrency(any())).thenReturn(Completable.complete())
        whenever(selectedCurrencyRepositoryMock.putSelectedCurrency(any()))
            .thenReturn(Completable.complete())
    }

    @Test
    fun `given a base currency and the same currency code when execute should behave correctly`() {
        // given
        val currencyCode = "EUR"
        val baseCurrency = Currency(currencyCode, 1.0, 1.0)
        val amount = 1.0
        whenever(ratesRepositoryMock.getRate(currencyCode, currencyCode))
            .thenReturn(Maybe.empty())
        whenever(baseCurrencyRepositoryMock.observeBaseCurrency())
            .thenReturn(Observable.just(baseCurrency))

        // when
        val testObserver = setSelectedCurrencyUseCase.execute(currencyCode, amount).test()

        // then
        verify(selectedCurrencyRepositoryMock)
            .putSelectedCurrency(Currency(currencyCode, 1.0, DEFAULT_RATE_IF_EMPTY))
        verify(baseCurrencyRepositoryMock)
            .putBaseCurrency(baseCurrency)
        testObserver.assertResult()
    }

    @Test
    fun `given a base currency and a currency code when execute should behave correctly`() {
        // given
        val currencyCode = "AUD"
        val baseCurrency = Currency("EUR", 1.0, 1.0)
        val amount = 1.0
        whenever(ratesRepositoryMock.getRate(baseCurrency.currencyCode, currencyCode))
            .thenReturn(Maybe.just(2.0))
        whenever(baseCurrencyRepositoryMock.observeBaseCurrency())
            .thenReturn(Observable.just(baseCurrency))

        // when
        val testObserver = setSelectedCurrencyUseCase.execute(currencyCode, amount).test()

        // then
        verify(selectedCurrencyRepositoryMock)
            .putSelectedCurrency(Currency(currencyCode, 1.0, 2.0))
        verify(baseCurrencyRepositoryMock)
            .putBaseCurrency(baseCurrency.copy(amount = 0.5))
        testObserver.assertResult()
    }
}
