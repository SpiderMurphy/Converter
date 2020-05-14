package com.revolut.currency.rates.adapter

import androidx.annotation.DrawableRes
import com.revolut.currency.R

enum class CurrencyFlag(val currencyCode: String, @DrawableRes val resourceId: Int) {
    AUD("AUD", R.drawable.ic_aud),
    BGN("BGN", R.drawable.ic_bgn),
    BRL("BRL", R.drawable.ic_brl),
    CAD("CAD", R.drawable.ic_cad),
    CHF("CHF", R.drawable.ic_chf),
    CNY("CNY", R.drawable.ic_cny),
    CZK("CZK", R.drawable.ic_czk),
    DKK("DKK", R.drawable.ic_dkk),
    EUR("EUR", R.drawable.ic_eur),
    GBP("GBP", R.drawable.ic_gbp),
    HKD("HKD", R.drawable.ic_hkd),
    HRK("HRK", R.drawable.ic_hrk),
    HUF("HUF", R.drawable.ic_huf),
    IDR("IDR", R.drawable.ic_idr),
    ILS("ILS", R.drawable.ic_ils),
    INR("INR", R.drawable.ic_inr),
    JPY("JPY", R.drawable.ic_jpy),
    KRW("KRW", R.drawable.ic_krw),
    MXN("MXN", R.drawable.ic_mxn),
    MYR("MYR", R.drawable.ic_myr),
    NOK("NOK", R.drawable.ic_nok),
    NZD("NZD", R.drawable.ic_nzd),
    PHP("PHP", R.drawable.ic_php),
    PLN("PLN", R.drawable.ic_pln),
    RON("RON", R.drawable.ic_ron),
    RUB("RUB", R.drawable.ic_rub),
    SEK("SEK", R.drawable.ic_sek),
    SGD("SGD", R.drawable.ic_sgd),
    THB("THB", R.drawable.ic_thb),
    USD("USD", R.drawable.ic_usd),
    ZAR("ZAR", R.drawable.ic_zar),
    NOT_FOUND("", R.drawable.ic_flag_not_found);

    companion object {
        fun fromCurrencyCode(currencyCode: String) =
            values()
                .find { it.currencyCode == currencyCode } ?: NOT_FOUND
    }
}