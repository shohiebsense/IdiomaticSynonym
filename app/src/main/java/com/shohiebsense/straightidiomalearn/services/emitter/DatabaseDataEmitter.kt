package com.shohiebsense.straightidiomalearn.services.emitter

import android.content.Context
import com.shohiebsense.straightidiomalearn.db.IdiomDbHelper
import com.shohiebsense.straightidiomalearn.db.database
import com.shohiebsense.straightidiomalearn.model.TranslatedIdiom
import com.shohiebsense.straightidiomalearn.model.UntranslatedIdiom
import com.shohiebsense.straightidiomalearn.services.dbs.QueryService
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import com.shohiebsense.straightidiomalearn.view.fragment.callbacks.FetchedTextCallback
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by Shohiebsense on 16/10/2017.
 */

class DatabaseDataEmitter(val context: Context, var fetchCallback: FetchedTextCallback) {

    var queryService = QueryService(context.database.readableDatabase)
    //var translatedIdiomList : MutableList<TranslatedIdiom> = mutableListOf<TranslatedIdiom>()
    //var untranslatedIdiomList : MutableList<UntranslatedIdiom> = mutableListOf<UntranslatedIdiom>()

    companion object {

        var translatedIdiomList: MutableList<TranslatedIdiom> = mutableListOf<TranslatedIdiom>()
        var untranslatedIdiomList: MutableList<UntranslatedIdiom> = mutableListOf<UntranslatedIdiom>()


    }

     var translatedConsumer = object : Observer<TranslatedIdiom> {
        override fun onSubscribe(d: Disposable) {
            AppUtil.makeDebugLog("subscribe ss ") //first
            fetchCallback.onFindingTranslatedIdiom()
        }

        override fun onError(e: Throwable) {
            AppUtil.makeDebugLog("errorr ss")
            fetchCallback.onErrorFetchingDatabase()

        }

        override fun onComplete() {
            AppUtil.makeDebugLog("completeedd ss") //last
            fetchCallback.onFinishedFetchingTranslatedDatabase(translatedIdiomList)

        }

        override fun onNext(translatedIdiom: TranslatedIdiom) {
            translatedIdiomList.add(translatedIdiom)
          //  AppUtil.makeErrorLog("translatedidiom " + translatedIdiomList.size)

        }
    }

    var unTranslatedConsumer = object : Observer<UntranslatedIdiom> {
        override fun onSubscribe(d: Disposable) {
            AppUtil.makeDebugLog("subscribe ss ") //first
            fetchCallback.onFindingUntranslatedIdiom()
        }

        override fun onError(e: Throwable) {
            AppUtil.makeDebugLog("errorr ss")
            fetchCallback.onErrorFetchingDatabase()


        }

        override fun onComplete() {
            AppUtil.makeDebugLog("completeedd untranslated") //last
            fetchCallback.onFinishedFetchingUntranslatedDatabase(untranslatedIdiomList)
        }

        override fun onNext(untranslatedIdiom: UntranslatedIdiom) {
            untranslatedIdiomList.add(untranslatedIdiom)
           // AppUtil.makeErrorLog("untranslated " + untranslatedIdiomList.size)
        }
    }
    fun getAll(){
        //queryService.getAllTranslated(translatedConsumer)
        queryService.getAllUntranslated(unTranslatedListConsumer)
        queryService.getAllTranslated(translatedListConsumer)

    }

    fun getAllUntranslated(){
        queryService.getAllUntranslated(unTranslatedListConsumer)
    }



    var translatedListConsumer = object : Observer<MutableList<TranslatedIdiom>> {
        override fun onSubscribe(d: Disposable) {
            AppUtil.makeDebugLog("subscribe translatedlist ") //first
            fetchCallback.onFindingTranslatedIdiom()
        }

        override fun onError(e: Throwable) {
            AppUtil.makeDebugLog("errorr ss "+e.toString())
            fetchCallback.onErrorFetchingDatabase()

        }

        override fun onComplete() {
            AppUtil.makeDebugLog("completeedd translatedidioms") //last
            fetchCallback.onFinishedFetchingTranslatedDatabase(translatedIdiomList)

        }

        override fun onNext(translatedIdiomListt: MutableList<TranslatedIdiom>) {
            AppUtil.makeErrorLog("gett in translatedidiomlist")
            translatedIdiomList = translatedIdiomListt
            //  AppUtil.makeErrorLog("translatedidiom " + translatedIdiomList.size)

        }
    }

    var unTranslatedListConsumer = object : Observer<MutableList<UntranslatedIdiom>> {


        override fun onSubscribe(d: Disposable) {
            AppUtil.makeDebugLog("subscribe untranslated list ") //first
            fetchCallback.onFindingUntranslatedIdiom()
        }

        override fun onError(e: Throwable) {
            AppUtil.makeDebugLog("errorr "+e.toString())
            fetchCallback.onErrorFetchingDatabase()


        }

        override fun onComplete() {
            AppUtil.makeDebugLog("completeedd untranslated") //last
            AppUtil.makeErrorLog("size of untranslated "+ untranslatedIdiomList.size)
            fetchCallback.onFinishedFetchingUntranslatedDatabase(untranslatedIdiomList)
        }

        override fun onNext(t: MutableList<UntranslatedIdiom>) {
            AppUtil.makeErrorLog("gett in untranslatedidiomlist")
            untranslatedIdiomList = t
        }

    }

}