package com.shohiebsense.idiomaticsynonym.services.dbs

import android.database.sqlite.SQLiteDatabase
import com.shohiebsense.idiomaticsynonym.db.Bookmark
import com.shohiebsense.idiomaticsynonym.db.ReplacedHistory
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.model.ReplaceHistory
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.utils.StoryExample
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
            db.select(ReplacedHistory.TABLE_REPLACED_HISTORY).exec {
                val parser = rowParser { id: Int, bookmarkId: Int, english: String, indonesian : String, idioms : String ->
                    ReplaceHistory(id,bookmarkId,english,indonesian,idioms)
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

    fun setOriginalTranslation(bookmarkId : Int, idiom : String, translation : String) {
        var mCompositeDisposable = CompositeDisposable()
        mCompositeDisposable.add(Single.fromCallable {
            db.insert(ReplacedHistory.TABLE_REPLACED_HISTORY,
                    ReplacedHistory.COLUMN_BOOKMARK_ID to bookmarkId,
                    ReplacedHistory.COLUMN_IDIOM to idiom,
                    ReplacedHistory.COLUMN_COLUMN_ORIGINAL_TRANSLATION to translation
            )
        }.subscribeOn(Schedulers.io()).subscribe())
    }

    fun setReplacedTranslation(bookmarkId: Int, idiom : String, translation: String){
        Completable.create {
            db.update(ReplacedHistory.TABLE_REPLACED_HISTORY,
                    ReplacedHistory.COLUMN_COLUMN_REPLACED_TRANSLATION to translation)
                    .whereArgs("${ReplacedHistory.COLUMN_BOOKMARK_ID} = {bookmarkid} and ${ReplacedHistory.COLUMN_IDIOM} = {idiom} ",
                            "bookmarkid" to bookmarkId,
                            "idiom" to idiom)
                    .exec()
        }.subscribeOn(Schedulers.io()).subscribe()
    }

    fun isIdiomExists(bookmarkId: Int, idiom : String, observer : Observer<Boolean>) {
        AppUtil.makeErrorLog("idiom di exists apa dulus "+idiom)
        Observable.create<Boolean> {
            db.select(ReplacedHistory.TABLE_REPLACED_HISTORY,ReplacedHistory.COLUMN_IDIOM).whereArgs("${ReplacedHistory.COLUMN_BOOKMARK_ID} = {bookmarkid} and ${ReplacedHistory.COLUMN_IDIOM} = {idiom} ",
                    "bookmarkid" to bookmarkId,
                    "idiom" to idiom).exec {
                it.onNext(count > 0)
                it.onComplete()
                close()
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer)
    }


    fun getOriginalTranslation(bookmarkId: Int, idiom : String, observer : Observer<ReplaceHistory>) {
        Observable.create<ReplaceHistory> {
            db.select(ReplacedHistory.TABLE_REPLACED_HISTORY).whereArgs("${ReplacedHistory.COLUMN_BOOKMARK_ID} = {bookmarkid} and ${ReplacedHistory.COLUMN_IDIOM} = {idiom} ",
                    "bookmarkid" to bookmarkId,
                    "idiom" to idiom).exec {
                val parser = rowParser { id: Int, bookmarkId: Int, english: String, indonesian : String, idioms : String ->
                    ReplaceHistory(id,bookmarkId,english,indonesian,idioms)
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
        AppUtil.makeErrorLog("idiomnya apa dulu ?? "+idiom)
        Observable.create<ReplaceHistory> {
            db.select(ReplacedHistory.TABLE_REPLACED_HISTORY).whereArgs("("+ReplacedHistory.COLUMN_BOOKMARK_ID+"="+bookmarkId+" and "+ReplacedHistory.COLUMN_IDIOM+"='$idiom')").exec {
                val parser = rowParser { id: Int, bookmarkId: Int, english: String, indonesian : String, idioms : String ->
                    ReplaceHistory(id,bookmarkId,english,indonesian,idioms)
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