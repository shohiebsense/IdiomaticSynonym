/*
package com.shohiebsense.straightidiomalearn.presenter

import com.shohiebsense.straightidiomalearn.services.dbs.TranslatedIdiomQueryService
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

*/
/**
 * Created by Shohiebsense on 13/10/2017.
 *//*


class TranslatedPresenter {


    private lateinit var translatedPhraseList : Single<List<TranslatedIdiom>>
    private lateinit var translatedPhraseObserver : SingleObserver<List<TranslatedIdiom>>



    init {
        translatedPhraseObserver = object : SingleObserver<List<TranslatedIdiom>>{
            override fun onSuccess(t: List<TranslatedIdiom>) {
                AppUtil.makeDebugLog("suksess "+t.size)
            }

            override fun onSubscribe(d: Disposable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onError(e: Throwable) {
                AppUtil.makeErrorLog("ambil list error "+e.toString())
            }

        }
    }


    fun getTranslatedPhrase(){
        translatedPhraseList = TranslatedIdiomQueryService.getAllTranslated()
        translatedPhraseList.subscribe(translatedPhraseObserver)
    }
}*/
