package com.mwhive.maptesttask.utilsandextensions


import com.mwhive.maptesttask.App
import com.mwhive.maptesttask.data.error.NoInternetConnectionException
import com.mwhive.maptesttask.utilsandextensions.extensions.hasNetworkConnection
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable


object NetworkConnectivity {

    fun getStateFlowable(): Flowable<Boolean> = Flowable.create({ emitter ->
        if (App.applicationContext().hasNetworkConnection()) {
            emitter.onNext(true)
        } else {
            emitter.onError(NoInternetConnectionException())
        }
        emitter.onComplete()
    }, BackpressureStrategy.LATEST)
}