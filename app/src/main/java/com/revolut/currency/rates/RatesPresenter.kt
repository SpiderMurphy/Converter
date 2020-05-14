package com.revolut.currency.rates

import com.revolut.common_mvi.MviInteractor
import com.revolut.common_mvi.MviPresenterBase
import com.revolut.currency_domain.models.RateItem
import java.math.BigDecimal
import java.math.RoundingMode

private const val SCALE_PLACES = 2

class RatesPresenter(
    interactor: MviInteractor<RatesIntent, RatesResult>
) : MviPresenterBase<RatesIntent, RatesResult, RatesState>(interactor) {
    override val initialState: RatesState
        get() = RatesState(rates = emptyList(), scrollToTop = false)

    override fun reduce(previousState: RatesState, result: RatesResult): RatesState =
        when (result) {
            is RatesResult.GetRates -> previousState.copy(
                scrollToTop = false,
                rates = result.rates.toRatesData()
            )
            is RatesResult.SelectedCurrencyChanged -> previousState.copy(
                scrollToTop = result.scrollToTop
            )
        }

}

private fun List<RateItem>.toRatesData(): List<RatesAdapterData> = mapIndexed { index, rateItem ->
    RatesAdapterData(
        stableId = rateItem.stableId,
        isSelected = index == 0,
        currencyCode = rateItem.currencyCode,
        currencyName = rateItem.currencyName,
        currencyAmount = rateItem.rateValue.scaleTo(SCALE_PLACES).toString()
    )
}

fun Double.scaleTo(places: Int): BigDecimal = BigDecimal(this.toString())
    .setScale(places, RoundingMode.HALF_UP)
