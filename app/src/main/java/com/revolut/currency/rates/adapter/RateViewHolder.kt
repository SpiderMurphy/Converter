package com.revolut.currency.rates.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.revolut.currency.R
import com.revolut.currency.rates.RatesAdapterData
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class RateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val currencyFlag: ImageView by lazy { itemView.findViewById<ImageView>(R.id.currencyFlag) }
    private val currencyCode: TextView by lazy { itemView.findViewById<TextView>(R.id.currencyCode) }
    private val currencyName: TextView by lazy { itemView.findViewById<TextView>(R.id.currencyName) }
    private val rateValue: EditText by lazy { itemView.findViewById<EditText>(R.id.rateValue) }
    private val eventsSubject: Subject<EditedItem> = PublishSubject.create()

    init {
        itemView.setOnClickListener {
            eventsSubject.onNext(
                EditedItem(
                    adapterPosition,
                    rateValue.text.toString(),
                    true
                )
            )
        }
        rateValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                eventsSubject.onNext(
                    EditedItem(adapterPosition, rateValue.text.toString(), rateValue.isFocused)
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    fun bindItem(rateItem: RatesAdapterData) {
        currencyCode.text = rateItem.currencyCode
        currencyName.text = rateItem.currencyName
        currencyFlag.setImageResource(CurrencyFlag.fromCurrencyCode(rateItem.currencyCode).resourceId)
        rateValue.isEnabled = rateItem.isSelected
        rateValue.takeIf { !it.isFocused }?.setText(rateItem.currencyAmount)
        rateValue.takeIf { rateItem.isSelected && !it.isFocused }?.requestFocus()
        rateValue.imeOptions = EditorInfo.IME_ACTION_DONE
    }

    fun observeItemEdited(): Observable<EditedItem> = eventsSubject.hide()

    data class EditedItem(
        val position: Int,
        val value: String,
        val isEdited: Boolean
    )
}
