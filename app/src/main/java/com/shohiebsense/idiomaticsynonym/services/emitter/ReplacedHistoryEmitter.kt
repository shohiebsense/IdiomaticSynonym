package com.shohiebsense.idiomaticsynonym.services.emitter

import android.content.Context
import com.shohiebsense.idiomaticsynonym.db.bookmarkDatabase
import com.shohiebsense.idiomaticsynonym.model.ReplaceHistory
import com.shohiebsense.idiomaticsynonym.services.dbs.ReplacedHistoryQueryService
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import io.reactivex.Observer
import io.reactivex.disposables.Disposable


/**
 * Created by Shohiebsense on 12/07/2018
 */

class ReplacedHistoryEmitter(val context: Context, val callback : ReplacedHistoryEmitter.ReplacedHistoryCallback)  {
    var queryService = ReplacedHistoryQueryService(context.bookmarkDatabase.writableDatabase)


    fun isIdiomExists(bookmarkId: Int, idiom: String) {
        var observer = object : Observer<Boolean> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: Boolean) {
                if(t){
                    callback.onIdiomExists()
                }
                else{
                    callback.onIdiomNotExists()
                }
            }

            override fun onError(e: Throwable) {
                AppUtil.makeErrorLog("error getting isExissts "+e.toString())
            }
        }
        queryService.isIdiomExists(bookmarkId,idiom,observer)
    }

    fun getOriginalTranslation(bookmarkId: Int, idiom: String){
        var observer = object : Observer<ReplaceHistory> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: ReplaceHistory) {
                callback.onGettingOriginalTranslation(t.originalTranslation)
            }

            override fun onError(e: Throwable) {
                AppUtil.makeErrorLog("error getting original Transl "+e.toString())
            }
        }
        queryService.getOriginalTranslation(bookmarkId,idiom,observer)
    }

    fun getReplacedTranslation(bookmarkId: Int, idiom: String){
        var observer = object : Observer<ReplaceHistory> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: ReplaceHistory) {
                callback.onGettingReplacedTranslation(t.replacedTranslation)
            }

            override fun onError(e: Throwable) {
                AppUtil.makeErrorLog("error getting replacedTranslation Transl "+e.toString())
                //getTable()
            }
        }
        queryService.getReplacedTranslation(bookmarkId,idiom,observer)
    }

    fun setReplacedTranslation(bookmarkId: Int, idiom: String, translation: String){
        queryService.setReplacedTranslation(bookmarkId,idiom,translation)
    }

    fun setOriginalTranslation(bookmarkId : Int, idiom : String, translation : String){
        queryService.setOriginalTranslation(bookmarkId,idiom,translation)
    }

    fun getTable() {
        queryService.getReplacedStories()
    }

    interface ReplacedHistoryCallback{
        fun onIdiomNotExists()
        fun onIdiomExists()
        fun onGettingOriginalTranslation(translation: String)
        fun onGettingReplacedTranslation(translation: String)
    }
}