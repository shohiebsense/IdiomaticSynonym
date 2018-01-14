package com.shohiebsense.idiomaticsynonym.services.emitter

import android.content.Context
import com.shohiebsense.idiomaticsynonym.db.database
import com.shohiebsense.idiomaticsynonym.model.CombinedIdiom
import com.shohiebsense.idiomaticsynonym.model.TranslatedIdiom
import com.shohiebsense.idiomaticsynonym.model.UntranslatedIdiom
import com.shohiebsense.idiomaticsynonym.services.dbs.QueryService
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.fragment.callbacks.DatabaseCallback
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by Shohiebsense on 16/10/2017.
 */

class TranslatedAndUntranslatedDataEmitter(val context: Context, var databaseCallback: DatabaseCallback) {

    var FETCHING_TRANSLATED_IDIOM_MODE = 1
    var FETCHING_UNTRANSLATED_IDIOM_MODE = 2

    var queryService = QueryService(context.database.writableDatabase)
    //var translatedIdiomList : MutableList<TranslatedIdiom> = mutableListOf<TranslatedIdiom>()
    //var untranslatedIdiomList : MutableList<UntranslatedIdiom> = mutableListOf<UntranslatedIdiom>()

    companion object {

        val translatedIdiomList: ArrayList<TranslatedIdiom> = arrayListOf<TranslatedIdiom>()
        val untranslatedIdiomList: ArrayList<UntranslatedIdiom> = arrayListOf<UntranslatedIdiom>()
        val idiomsList = arrayListOf<CombinedIdiom>()



        fun isIdiomsEmpty() : Boolean{
            return translatedIdiomList.isEmpty() || untranslatedIdiomList.isEmpty()
        }
    }



    fun getAll(){
        //queryService.getAllTranslated(translatedConsumer)
        //queryService.getAllUntranslated(unTranslatedListConsumer)
        //queryService.getAllTranslated(translatedListConsumer)

        queryService.getAllIdiomsOnly(getIdiomStringObserver())

    }

    fun getIdiomStringObserver(): Observer<ArrayList<CombinedIdiom>> {
        return object : Observer<ArrayList<CombinedIdiom>>{
            override fun onSubscribe(d: Disposable) {
                AppUtil.makeDebugLog("mulai????")

                databaseCallback.onFetchingData(FETCHING_TRANSLATED_IDIOM_MODE)
            }

            override fun onNext(t: ArrayList<CombinedIdiom>) {
                AppUtil.makeDebugLog("can beres????")
                idiomsList.addAll(t)
            }

            override fun onComplete() {
                databaseCallback.onFetchedBoth()
            }

            override fun onError(e: Throwable) {
                AppUtil.makeDebugLog("error  "+e.toString())
                databaseCallback.onErrorFetchingData()

            }

        }
    }

    fun getAllIdiomsOnly(){
        queryService.getAllIdiomsOnly(getIdiomStringObserver())
    }

    fun getAllUntranslated(){
        queryService.getAllUntranslated(unTranslatedListConsumer)
    }



    var translatedListConsumer = object : Observer<ArrayList<TranslatedIdiom>> {
        override fun onSubscribe(d: Disposable) {
            AppUtil.makeDebugLog("subscribe translatedlist ") //first
            databaseCallback.onFetchingData(FETCHING_TRANSLATED_IDIOM_MODE)
        }

        override fun onError(e: Throwable) {
            AppUtil.makeDebugLog("errorr ss "+e.toString())
            databaseCallback.onErrorFetchingData()

        }

        override fun onComplete() {
            databaseCallback.onFetchedTranslatedData()

        }

        override fun onNext(translatedIdiomListt: ArrayList<TranslatedIdiom>) {
            translatedIdiomList.addAll(translatedIdiomListt)

        }
    }

    var unTranslatedListConsumer = object : Observer<ArrayList<UntranslatedIdiom>> {


        override fun onSubscribe(d: Disposable) {
            databaseCallback.onFetchingData(FETCHING_UNTRANSLATED_IDIOM_MODE)
        }

        override fun onError(e: Throwable) {
            AppUtil.makeDebugLog("errorr "+e.toString())
            databaseCallback.onErrorFetchingData()

        }

        override fun onComplete() {
            databaseCallback.onFetchedUntranslatedData()
        }

        override fun onNext(t: ArrayList<UntranslatedIdiom>) {
            untranslatedIdiomList.addAll(t)
        }

    }



}