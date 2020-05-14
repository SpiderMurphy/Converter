package com.revolut.common_mvi

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observables.ConnectableObservable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

interface MviPresenter<Intent, State> {
    fun bind(intents: Observable<Intent>)
    fun subscribe(render: (state: State) -> Unit, scheduler: Scheduler)
    fun unsubscribe()
    fun destroy()
}

abstract class MviPresenterBase<Intent, Result, State>(
    interactor: MviInteractor<Intent, Result>
) : MviPresenter<Intent, State> {
    private val intentSubject: Subject<Intent> = PublishSubject.create()
    private val stateObservable: ConnectableObservable<State>
    private val stateDisposable = CompositeDisposable()
    private val renderDisposable = CompositeDisposable()
    protected abstract val initialState: State

    init {
        stateObservable = intentSubject
            .compose(interactor.intentProcessor)
            .scan(initialState, ::reduce)
            .publish()
    }

    override fun bind(intents: Observable<Intent>) {
        intents.subscribe(intentSubject)
    }

    override fun subscribe(render: (state: State) -> Unit, scheduler: Scheduler) {
        stateDisposable.add(stateObservable.connect())
        renderDisposable.add(stateObservable.observeOn(scheduler).subscribe(render))
    }

    override fun unsubscribe() {
        renderDisposable.clear()
    }

    override fun destroy() {
        if (renderDisposable.isDisposed.not()) {
            renderDisposable.clear()
        }
        stateDisposable.clear()
    }

    protected abstract fun reduce(previousState: State, result: Result): State
}
