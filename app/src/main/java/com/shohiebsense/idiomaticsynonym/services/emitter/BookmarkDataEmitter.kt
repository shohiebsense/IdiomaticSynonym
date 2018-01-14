package com.shohiebsense.idiomaticsynonym.services.emitter

import android.content.Context
import com.shohiebsense.idiomaticsynonym.db.bookmarkDatabase
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.model.IndexedSentence
import com.shohiebsense.idiomaticsynonym.services.dbs.BookmarkQueryService
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by Shohiebsense on 03/01/2018.
 */
class BookmarkDataEmitter(val context: Context) {

    var queryService = BookmarkQueryService(context.bookmarkDatabase.writableDatabase)

    fun insertBookmarkEnglish(fileName: String, wholeText: CharSequence){
        queryService.insertIntoBookmarkEnglish(context, fileName, wholeText)
    }

    fun insertIndexedSentence(index: Int, sentence: String, idiom: String){
        queryService.insertSentenceAndItsSource(context, index, sentence, idiom)
    }

    fun insertIndexedSentences(indexedSentences: List<IndexedSentence>, listener: BookmarkQueryService.CompletedTransactionListener){
        queryService.insertSentenceAndItsSource(context, indexedSentences, listener)

    }

    fun getEnglisbBookmarkedBasedOnPdfFileName(name : String){
        val observer = object : Observer<BookmarkedEnglish>{
            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(t: BookmarkedEnglish) {
            }

            override fun onComplete() {
            }

            override fun onError(e: Throwable) {
            }

        }
        queryService.getEnglishBookmarkBaaedOnPdfFileName(name, observer)
    }

    fun getAllIndexedSentencesBasedOnBookmarkedEnglishId(id : Int){
        val observer = object : Observer<List<IndexedSentence>> {
            override fun onNext(t: List<IndexedSentence>) {
                //indexedSentenceCallback.onFetched(t)
                AppUtil.makeDebugLog("the size indexed Sentence is "+t.size)
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onError(e: Throwable) {
                AppUtil.makeDebugLog("dapat error "+e.toString())
            }

            override fun onComplete() {
                AppUtil.makeDebugLog("the size indexed Sentence is completedd")


            }
        }

        queryService.getIndexedSentencesBasedOnEnglishBookmarkedId(id,observer)
    }

    fun getAllIndexedSentenceBasedOnPdfFileName(name : String,indexedSentenceCallback: IndexedSentenceCallback)  {
        val observer = object : Observer<List<IndexedSentence>> {
            override fun onNext(t: List<IndexedSentence>) {
                indexedSentenceCallback.onFetched(t)
                AppUtil.makeDebugLog("the size indexed Sentence is "+t.size)
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onError(e: Throwable) {
                AppUtil.makeDebugLog("dapat error "+e.toString())
            }

            override fun onComplete() {
                AppUtil.makeDebugLog("the size indexed Sentence is completedd")


            }
        }

        queryService.selectIndexedSentenceBasedOnPdfFileName(name,observer)
    }

    interface IndexedSentenceCallback {
        fun onFetched(indexedSentences : List<IndexedSentence>)
    }
}