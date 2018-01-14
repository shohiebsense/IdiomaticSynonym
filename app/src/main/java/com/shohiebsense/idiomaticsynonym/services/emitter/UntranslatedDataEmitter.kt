package com.shohiebsense.idiomaticsynonym.services.emitter

import android.content.Context
import com.shohiebsense.idiomaticsynonym.db.database
import com.shohiebsense.idiomaticsynonym.model.UntranslatedIdiom
import com.shohiebsense.idiomaticsynonym.services.dbs.QueryService
import com.shohiebsense.idiomaticsynonym.view.fragment.callbacks.UntranslatedCallback
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by Shohiebsense on 16/10/2017.
 */

class UntranslatedDataEmitter(var activity: Context, var untranslatedCallback: UntranslatedCallback) {

    var queryService = QueryService(activity.database.writableDatabase)
    var translatedConsumer = object : Observer<UntranslatedIdiom> {
        override fun onError(e: Throwable) {
            untranslatedCallback.onFetchingFailed(e)
        }

        override fun onSubscribe(d: Disposable) {
            untranslatedCallback.onFetchingSuccess(d)
        }

        override fun onComplete() {
            untranslatedCallback.pnFetchingCompleted()
        }

        override fun onNext(t: UntranslatedIdiom) {
            untranslatedCallback.onFetchProcess(t)
        }

    }


    fun getAll(){
        //queryService.getAllUntranslated
    }

}