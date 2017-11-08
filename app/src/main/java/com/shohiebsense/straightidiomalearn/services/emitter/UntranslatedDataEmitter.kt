package com.shohiebsense.straightidiomalearn.services.emitter

import android.app.Activity
import android.content.Context
import com.shohiebsense.straightidiomalearn.db.database
import com.shohiebsense.straightidiomalearn.model.UntranslatedIdiom
import com.shohiebsense.straightidiomalearn.services.dbs.QueryService
import com.shohiebsense.straightidiomalearn.view.fragment.callbacks.UntranslatedCallback
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by Shohiebsense on 16/10/2017.
 */

class UntranslatedDataEmitter(var activity: Context, var untranslatedCallback: UntranslatedCallback) {

    var queryService = QueryService(activity.database.readableDatabase)
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