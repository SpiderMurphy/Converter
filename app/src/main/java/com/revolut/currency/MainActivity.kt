package com.revolut.currency

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.revolut.common_mvi.MviPresenter
import com.revolut.currency.rates.RatesIntent
import com.revolut.currency.rates.RatesState
import com.revolut.currency.rates.adapter.RateItemEvent
import com.revolut.currency.rates.adapter.RatesListAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named

class MainActivity : AppCompatActivity(), KoinComponent {
    private val ratesPresenter: MviPresenter<RatesIntent, RatesState> by inject(
        named(RATES_PRESENTER)
    )
    private val ratesAdapter: RatesListAdapter by lazy { RatesListAdapter() }
    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val ratesList: RecyclerView by lazy { findViewById<RecyclerView>(R.id.ratesList) }
    private val intentSubject: Subject<RatesIntent> = PublishSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        (ratesList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        ratesList.adapter = ratesAdapter
    }

    override fun onStart() {
        super.onStart()
        ratesPresenter.bind(
            buildIntents()
        )
    }

    override fun onResume() {
        super.onResume()
        ratesPresenter.subscribe(::render, AndroidSchedulers.mainThread())
        intentSubject.onNext(RatesIntent.GetRates("EUR", "en"))
    }

    override fun onPause() {
        super.onPause()
        ratesPresenter.unsubscribe()
    }

    override fun onStop() {
        super.onStop()
        ratesPresenter.destroy()
    }

    private fun render(viewState: RatesState) {
        when (viewState.scrollToTop) {
            true -> ratesList.scrollToPosition(0)
            false -> ratesAdapter.submitList(viewState.rates)
        }
    }

    private fun buildIntents(): Observable<RatesIntent> = Observable.mergeArray(
        intentSubject.hide(),
        ratesAdapter.observeEvents().map { event ->
            when (event) {
                is RateItemEvent.SelectRate -> RatesIntent.SetCurrency(
                    event.currencyCode,
                    event.amount
                )
                is RateItemEvent.SelectedCurrencyChanged -> RatesIntent.SelectedCurrencyChanged(
                    event.oldCurrencyCode,
                    event.newCurrencyCode
                )
            }
        }
    )
}
