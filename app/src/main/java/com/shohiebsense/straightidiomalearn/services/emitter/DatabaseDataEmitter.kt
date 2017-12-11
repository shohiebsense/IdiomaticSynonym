package com.shohiebsense.straightidiomalearn.services.emitter

import android.content.Context
import com.shohiebsense.straightidiomalearn.db.database
import com.shohiebsense.straightidiomalearn.model.TranslatedIdiom
import com.shohiebsense.straightidiomalearn.model.UntranslatedIdiom
import com.shohiebsense.straightidiomalearn.services.dbs.QueryService
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import com.shohiebsense.straightidiomalearn.view.fragment.callbacks.DatabaseCallback
import com.shohiebsense.straightidiomalearn.view.fragment.callbacks.FetchedTextCallback
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by Shohiebsense on 16/10/2017.
 */

class DatabaseDataEmitter(val context: Context, var databaseCallback: DatabaseCallback) {

    var FETCHING_TRANSLATED_IDIOM_MODE = 1
    var FETCHING_UNTRANSLATED_IDIOM_MODE = 2

    var queryService = QueryService(context.database.readableDatabase)
    //var translatedIdiomList : MutableList<TranslatedIdiom> = mutableListOf<TranslatedIdiom>()
    //var untranslatedIdiomList : MutableList<UntranslatedIdiom> = mutableListOf<UntranslatedIdiom>()

    companion object {

        var translatedIdiomList: MutableList<TranslatedIdiom> = mutableListOf<TranslatedIdiom>()
        var untranslatedIdiomList: MutableList<UntranslatedIdiom> = mutableListOf<UntranslatedIdiom>()



        fun isIdiomsEmpty() : Boolean{
            return translatedIdiomList.isEmpty() || untranslatedIdiomList.isEmpty()
        }
    }

     var translatedConsumer = object : Observer<TranslatedIdiom> {
        override fun onSubscribe(d: Disposable) {
            AppUtil.makeDebugLog("subscribe ss ") //first
            databaseCallback.onFetchingData(FETCHING_TRANSLATED_IDIOM_MODE
            )
        }

        override fun onError(e: Throwable) {
            AppUtil.makeDebugLog("errorr ss")
            databaseCallback.onErrorFetchingData()

        }

        override fun onComplete() {
            AppUtil.makeDebugLog("completeedd ss") //last
            databaseCallback.onFetchedTranslatedData()

        }

        override fun onNext(translatedIdiom: TranslatedIdiom) {
            translatedIdiomList.add(translatedIdiom)
          //  AppUtil.makeErrorLog("translatedidiom " + translatedIdiomList.size)

        }
    }

    var unTranslatedConsumer = object : Observer<UntranslatedIdiom> {
        override fun onSubscribe(d: Disposable) {
            AppUtil.makeDebugLog("subscribe ss ") //first
            databaseCallback.onFetchingData(FETCHING_UNTRANSLATED_IDIOM_MODE)
        }

        override fun onError(e: Throwable) {
            AppUtil.makeDebugLog("errorr ss")
            databaseCallback.onErrorFetchingData()


        }

        override fun onComplete() {
            AppUtil.makeDebugLog("completeedd untranslated") //last
            databaseCallback.onFetchedUntranslatedData()
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
            databaseCallback.onFetchingData(FETCHING_TRANSLATED_IDIOM_MODE)
        }

        override fun onError(e: Throwable) {
            AppUtil.makeDebugLog("errorr ss "+e.toString())
            databaseCallback.onErrorFetchingData()

        }

        override fun onComplete() {
            AppUtil.makeDebugLog("completeedd translatedidioms") //last
            databaseCallback.onFetchedTranslatedData()

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
            databaseCallback.onFetchingData(FETCHING_UNTRANSLATED_IDIOM_MODE)
        }

        override fun onError(e: Throwable) {
            AppUtil.makeDebugLog("errorr "+e.toString())
            databaseCallback.onErrorFetchingData()

        }

        override fun onComplete() {
            AppUtil.makeDebugLog("completeedd untranslated") //last
            AppUtil.makeErrorLog("size of untranslated "+ untranslatedIdiomList.size)
            databaseCallback.onFetchedUntranslatedData()
        }

        override fun onNext(t: MutableList<UntranslatedIdiom>) {
            AppUtil.makeErrorLog("gett in untranslatedidiomlist")
            untranslatedIdiomList = t
        }

    }

}