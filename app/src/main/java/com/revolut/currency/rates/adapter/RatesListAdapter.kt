package com.revolut.currency.rates.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.revolut.currency.R
import com.revolut.currency.rates.RatesAdapterData
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class RatesListAdapter : ListAdapter<RatesAdapterData, RateViewHolder>(RatesDiff()) {
    private val itemEvents: Subject<RateItemEvent> = PublishSubject.create()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder =
        RateViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.rate_item_view,
                parent,
                false
            )
        ).apply {
            observeItemEdited()
                .filter { it.isEdited }
                .map { it.toSelectRate() }
                .subscribe(itemEvents)
        }


    override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
        holder.bindItem(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).stableId
    }

    override fun onCurrentListChanged(
        previousList: MutableList<RatesAdapterData>,
        currentList: MutableList<RatesAdapterData>
    ) {
        itemEvents.onNext(
            RateItemEvent.SelectedCurrencyChanged(
                previousList.firstOrNull()?.currencyCode ?: "",
                currentList.firstOrNull()?.currencyCode ?: ""
            )
        )
    }

    fun observeEvents(): Observable<RateItemEvent> = itemEvents.hide()

    private fun RateViewHolder.EditedItem.toSelectRate(): RateItemEvent.SelectRate = RateItemEvent
        .SelectRate(getItem(position).currencyCode, value.toDoubleOrNull() ?: 0.0)
}

internal class RatesDiff : DiffUtil.ItemCallback<RatesAdapterData>() {
    override fun areItemsTheSame(oldItem: RatesAdapterData, newItem: RatesAdapterData): Boolean =
        oldItem.currencyCode == newItem.currencyCode

    override fun areContentsTheSame(oldItem: RatesAdapterData, newItem: RatesAdapterData): Boolean =
        (oldItem.currencyName == newItem.currencyName)
                && (newItem.currencyAmount == oldItem.currencyAmount)
                && (newItem.isSelected == oldItem.isSelected)
}

sealed class RateItemEvent {
    data class SelectRate(val currencyCode: String, val amount: Double) : RateItemEvent()
    data class SelectedCurrencyChanged(
        val oldCurrencyCode: String,
        val newCurrencyCode: String
    ) : RateItemEvent()
}
