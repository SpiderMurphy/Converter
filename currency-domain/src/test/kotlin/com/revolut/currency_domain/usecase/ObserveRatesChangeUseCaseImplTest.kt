package com.revolut.currency_domain.usecase

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.revolut.currency_domain.models.Rates
import com.revolut.currency_domain.repository.RatesRepository
import io.reactivex.Maybe
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test

import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.TimeUnit

class ObserveRatesChangeUseCaseImplTest {
    @Mock
    private lateinit var ratesRepositoryMock: RatesRepository
    private lateinit var observeRatesChangeUseCase: ObserveRatesChangeUseCase
    private val testScheduler = TestScheduler()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        observeRatesChangeUseCase =
            ObserveRatesChangeUseCaseImpl(
                ratesRepositoryMock,
                testScheduler
            )
    }

    @Test
    fun `given a delayed emission when execute should emit the correct results`() {
        // given
        val baseCurrency = "EUR"
        val emission1 = mock<Rates>()
        val emission2 = mock<Rates>()
        whenever(ratesRepositoryMock.getRates(baseCurrency)).thenReturn(
            Maybe.just(emission1).delay(
                INTERVAL_DELAY_MS,
                TimeUnit.MILLISECONDS,
                testScheduler
            ),
            Maybe.just(emission2)
        )

        // when
        val testObserver = observeRatesChangeUseCase.execute(baseCurrency).test()

        // then
        testScheduler.advanceTimeBy(INTERVAL_DELAY_MS, TimeUnit.MILLISECONDS)
        testObserver.assertValues(emission1, emission2)
    }

    @Test
    fun `given an error in between 2 changes when execute should emit the correct results`() {
        // given
        val baseCurrency = "EUR"
        val emission1 = mock<Rates>()
        val emission2 = mock<Rates>()
        whenever(ratesRepositoryMock.getRates(baseCurrency)).thenReturn(
            Maybe.just(emission1),
            Maybe.error(Throwable()),
            Maybe.just(emission2)
        )

        // when
        val testObserver = observeRatesChangeUseCase.execute(baseCurrency).test()

        // then
        testScheduler.advanceTimeBy(INTERVAL_DELAY_MS * 2, TimeUnit.MILLISECONDS)
        testObserver.assertValues(emission1, emission2)
    }
}
