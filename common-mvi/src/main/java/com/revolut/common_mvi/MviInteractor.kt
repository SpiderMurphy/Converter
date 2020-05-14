package com.revolut.common_mvi

import io.reactivex.ObservableTransformer

interface MviInteractor<Intent, Result> {
    val intentProcessor: ObservableTransformer<Intent, Result>
}
