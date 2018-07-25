package com.shohiebsense.idiomaticsynonym.services.dbs

import android.database.sqlite.SQLiteDatabase
import com.shohiebsense.idiomaticsynonym.db.ReplacedHistoryConstant
import com.shohiebsense.idiomaticsynonym.model.ReplaceHistory
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.db.*


/**
 * Created by Shohiebsense on 12/07/2018
 */

class ReplacedHistoryQueryService(val db : SQLiteDatabase) {


    fun getReplacedStories(){
        Observable.create<ReplaceHistory> { e ->
            db.select(ReplacedHistoryConstant.TABLE_REPLACED_HISTORY).exec {
                val parser = rowParser { id: Int, bookmarkId: Int, english: String, indonesian : String, idioms : String, sentenceOrder : Int, startIndex : Int, endIndex : Int ->
                    ReplaceHistory(id,bookmarkId,english,indonesian,idioms,sentenceOrder,startIndex,endIndex)
                }
                asSequence().forEach {
                    row-> AppUtil.makeDebugLog("yoo "+parser.parseRow(row).originalTranslation+"  "+parser.parseRow(row).idiom+"   ")
                }
                // parser2 = classParser<BookmarkedEnglish>()
                e.onNext(parseSingle(parser))
                e.onComplete()
                close()
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun insertOriginalTranslation(bookmarkId : Int, idiom : String, translation : String) {
        var mCompositeDisposable = CompositeDisposable()
        AppUtil.makeErrorLog("translation inserted event ")
        mCompositeDisposable.add(Single.fromCallable {
            db.insert(ReplacedHistoryConstant.TABLE_REPLACED_HISTORY,
                    ReplacedHistoryConstant.COLUMN_BOOKMARK_ID to bookmarkId,
                    ReplacedHistoryConstant.COLUMN_IDIOM to idiom,
                    ReplacedHistoryConstant.COLUMN_ORIGINAL_TRANSLATION to translation
            )
        }.subscribeOn(Schedulers.io()).subscribe())
    }


    fun setOriginalTranslation(bookmarkId: Int, idiom : String, translation: String){
        Completable.create {
            db.update(ReplacedHistoryConstant.TABLE_REPLACED_HISTORY,
                    ReplacedHistoryConstant.COLUMN_ORIGINAL_TRANSLATION to translation)
                    .whereArgs("${ReplacedHistoryConstant.COLUMN_BOOKMARK_ID} = {bookmarkid} and ${ReplacedHistoryConstant.COLUMN_IDIOM} = {idiom} ",
                            "bookmarkid" to bookmarkId,
                            "idiom" to idiom)
                    .exec()
        }.subscribeOn(Schedulers.io()).subscribe()
    }

    fun setReplacedTranslation(bookmarkId: Int, idiom : String, translation: String){
        Completable.create {
            db.update(ReplacedHistoryConstant.TABLE_REPLACED_HISTORY,
                    ReplacedHistoryConstant.COLUMN_REPLACED_TRANSLATION to translation)
                    .whereArgs("${ReplacedHistoryConstant.COLUMN_BOOKMARK_ID} = {bookmarkid} and ${ReplacedHistoryConstant.COLUMN_IDIOM} = {idiom} ",
                            "bookmarkid" to bookmarkId,
                            "idiom" to idiom)
                    .exec()
        }.subscribeOn(Schedulers.io()).subscribe()
    }

    fun isIdiomExists(bookmarkId: Int, idiom : String, observer : Observer<Boolean>) {
        AppUtil.makeErrorLog("idiom di exists apa dulus "+idiom)
        Observable.create<Boolean> {
            db.select(ReplacedHistoryConstant.TABLE_REPLACED_HISTORY,ReplacedHistoryConstant.COLUMN_IDIOM).whereArgs("${ReplacedHistoryConstant.COLUMN_BOOKMARK_ID} = {bookmarkid} and ${ReplacedHistoryConstant.COLUMN_IDIOM} = {idiom} ",
                    "bookmarkid" to bookmarkId,
                    "idiom" to idiom).exec {

                AppUtil.makeErrorLog("the size is exists is "+count)
                it.onNext(count > 0)
                it.onComplete()
                close()
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer)
    }


    fun setSentenceOrder(bookmarkId: Int, idiom: String, order: Int) {
        AppUtil.makeErrorLog("updatee sentence order "+order)
        Completable.create {
            db.update(ReplacedHistoryConstant.TABLE_REPLACED_HISTORY,
                    ReplacedHistoryConstant.COLUMN_SENTENCE_ORDER to order)
                    .whereArgs("${ReplacedHistoryConstant.COLUMN_BOOKMARK_ID} = {bookmarkid} and ${ReplacedHistoryConstant.COLUMN_IDIOM} = {idiom} ",
                            "bookmarkid" to bookmarkId,
                            "idiom" to idiom)
                    .exec()
        }.subscribeOn(Schedulers.io()).subscribe()
    }

    fun setIndexes(bookmarkId: Int, idiom : String, idiomIndex: Int, idiomEndIndex: Int) {
        Completable.create {
            db.update(ReplacedHistoryConstant.TABLE_REPLACED_HISTORY,
                    ReplacedHistoryConstant.COLUMN_IDIOM_INDEX to idiomIndex,
                    ReplacedHistoryConstant.COLUMN_IDIOM_ENDINDEX to idiomEndIndex
                    )
                    .whereArgs("${ReplacedHistoryConstant.COLUMN_BOOKMARK_ID} = {bookmarkid} and ${ReplacedHistoryConstant.COLUMN_IDIOM} = {idiom} ",
                            "bookmarkid" to bookmarkId,
                            "idiom" to idiom)
                    .exec()
        }.subscribeOn(Schedulers.io()).subscribe()
    }

    fun getOriginalTranslation(bookmarkId: Int, idiom : String, observer : Observer<ReplaceHistory>) {
        Observable.create<ReplaceHistory> {
            db.select(ReplacedHistoryConstant.TABLE_REPLACED_HISTORY).whereArgs("${ReplacedHistoryConstant.COLUMN_BOOKMARK_ID} = {bookmarkid} and ${ReplacedHistoryConstant.COLUMN_IDIOM} = {idiom} ",
                    "bookmarkid" to bookmarkId,
                    "idiom" to idiom).exec {
                val parser = rowParser { id: Int, bookmarkId: Int, english: String, indonesian : String, idioms : String, sentenceOrder : Int, idiomStartIndex : Int, idiomEndIndex : Int ->
                    ReplaceHistory(id,bookmarkId,english,indonesian,idioms,sentenceOrder,idiomStartIndex,idiomEndIndex)
                }
                asSequence().forEach {
                    row-> AppUtil.makeDebugLog("getting origtrans "+parser.parseRow(row).originalTranslation+"  "+parser.parseRow(row).idiom+"   ")
                }
                // parser2 = classParser<BookmarkedEnglish>()
                it.onNext(parseSingle(parser))
                it.onComplete()
                close()
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer)
    }

    fun getReplacedTranslation(bookmarkId: Int, idiom : String, observer : Observer<ReplaceHistory>) {
        Observable.create<ReplaceHistory> {
            db.select(ReplacedHistoryConstant.TABLE_REPLACED_HISTORY).whereArgs("${ReplacedHistoryConstant.COLUMN_BOOKMARK_ID} = {bookmarkid} and ${ReplacedHistoryConstant.COLUMN_IDIOM} = {idiom} ",
                    "bookmarkid" to bookmarkId,
                    "idiom" to idiom)
                    .exec {
                val parser = rowParser { id: Int, bookmarkId: Int, idiom : String, originalTranslation: String, replacedTranslation : String, sentenceOrder : Int, startIndex : Int, endIndex : Int ->
                    ReplaceHistory(id,bookmarkId,idiom,originalTranslation,replacedTranslation,sentenceOrder,startIndex,endIndex)
                }
                asSequence().forEach {
                    row-> AppUtil.makeDebugLog("getting replaceTrans "+parser.parseRow(row).originalTranslation+"  "+parser.parseRow(row).idiom+"   ")
                }
                // parser2 = classParser<BookmarkedEnglish>()
                it.onNext(parseSingle(parser))
                it.onComplete()
                close()
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer)
    }




}