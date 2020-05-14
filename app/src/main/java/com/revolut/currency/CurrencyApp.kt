package com.revolut.currency

import android.app.Application
import com.revolut.currency_data.currencyDataModule
import com.revolut.currency_domain.currencyDomainModule
import org.koin.core.context.startKoin

class CurrencyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(
                listOf(
                    currencyAppModule,
                    currencyDomainModule,
                    currencyDataModule
                )
            )
        }
    }
}