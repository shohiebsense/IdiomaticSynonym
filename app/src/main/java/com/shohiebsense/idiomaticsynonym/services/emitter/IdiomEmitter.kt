package com.shohiebsense.idiomaticsynonym.services.emitter

import android.content.Context
import com.shohiebsense.idiomaticsynonym.db.database
import com.shohiebsense.idiomaticsynonym.model.Idiom
import com.shohiebsense.idiomaticsynonym.services.dbs.IdiomTableQueryService
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import io.reactivex.MaybeObserver
import io.reactivex.Observer
import io.reactivex.disposables.Disposable


/**
 * Created by Shohiebsense on 21/09/2018
 */

class IdiomEmitter(val context: Context, val callback : IdiomCallback) {
    var queryService = IdiomTableQueryService(context.database.writableDatabase)

    fun getAllIdioms(){
        var observer = object : MaybeObserver<ArrayList<Idiom>>{
            override fun onSuccess(t: ArrayList<Idiom>) {
                callback.onFetched(t)
            }

            override fun onComplete() {
                AppUtil.makeErrorLog("completedd")
            }

            override fun onSubscribe(d: Disposable) {
                AppUtil.makeErrorLog("subscribee")
            }


            override fun onError(e: Throwable) {
                AppUtil.makeErrorLog("error fetching Idiom "+e.message)
            }

        }
        queryService.getAllIdioms().subscribe(observer)
    }


    interface IdiomCallback {
        fun onFetched(idioms : ArrayList<Idiom>)
    }
}