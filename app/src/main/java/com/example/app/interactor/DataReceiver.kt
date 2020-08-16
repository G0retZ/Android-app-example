package com.example.app.interactor

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

interface DataReceiver<D> {
    /**
     * @return {@link Observable<D>} to get shared data. In first call returns last known value if any
     */
    fun get(): Observable<D>
}


/**
 * To share data of type [D] between use cases until process memory is available.
 *
 * @param <D> - data type
</D> */
abstract class MemoryDataSharer<D> : DataReceiver<D>, Observer<D> {
    internal var subject: BehaviorSubject<D> = BehaviorSubject.create()

    override fun get(): Observable<D> = subject

    override fun onSubscribe(d: Disposable) = subject.onSubscribe(d)

    override fun onNext(data: D) = subject.onNext(data)

    override fun onError(e: Throwable) = with(subject) {
        subject = BehaviorSubject.create()
        onError(e)
    }

    override fun onComplete() = with(subject) {
        subject = BehaviorSubject.create()
        onComplete()
    }
}
