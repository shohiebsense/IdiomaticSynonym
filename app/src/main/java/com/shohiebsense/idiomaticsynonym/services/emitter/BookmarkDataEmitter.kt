package com.shohiebsense.idiomaticsynonym.services.emitter

import android.content.Context
import android.text.SpannedString
import com.shohiebsense.idiomaticsynonym.db.bookmarkDatabase
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.model.IndexedSentence
import com.shohiebsense.idiomaticsynonym.services.dbs.BookmarkQueryService
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.fragment.translateddisplay.EnglishResultFragment
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by Shohiebsense on 03/01/2018.
 */
class BookmarkDataEmitter(val context: Context) {

    var queryService = BookmarkQueryService(context.bookmarkDatabase.writableDatabase)
    lateinit var bookmarkCallback : SingleBookmarkCallback

    fun insertBookmarkEnglish(fileName: String, wholeText: CharSequence, indonesianText : CharSequence) : Int{
        AppUtil.makeDebugLog("bookmark inserted ")
       return queryService.insertIntoBookmarkEnglish(fileName, AppUtil.toHtml(SpannedString(wholeText)),AppUtil.toHtml(SpannedString(indonesianText)))
    }

    fun getEnglishTextBasedOnId(id : Int) : CharSequence{
        return queryService.getEnglishBookmarkBaaedOnLastId(id)
    }

    fun getLastId() : Int{
        return queryService.selectLastInsertedId()
    }

    fun updateEnglishText(wholeText: CharSequence){
        queryService.updateEnglishSentence(AppUtil.toHtml(SpannedString(wholeText)))
    }


    fun updateIndonesianText(wholeText: CharSequence){
        queryService.updateIndonesianSentence(AppUtil.toHtml(SpannedString(wholeText)))
    }

    fun insertIndexedSentence(index: Int, sentence: String, idiom: String){
        queryService.insertSentenceAndItsSource(index, sentence, idiom)
    }

    fun insertIndexedSentences(indexedSentences: List<IndexedSentence>, listener: BookmarkQueryService.CompletedTransactionListener){
        queryService.insertSentenceAndItsSource(context, indexedSentences, listener)
    }



    fun getHowManyBookTranslated() : Int {
        return queryService.getBookmarkCounts()
    }

    fun getHowManyIdiomsFound() : Int {
        return queryService.getIdiomFoundedCount()
    }


    fun getEnglisbBookmarks(listener : BookmarksCallback){
        val bookmarks = arrayListOf<BookmarkedEnglish>()
        val observer = object : Observer<List<BookmarkedEnglish>>{
            override fun onNext(t: List<BookmarkedEnglish>) {
                AppUtil.makeDebugLog("the sizeee "+t.size)
                bookmarks.addAll(t)
            }
            override fun onSubscribe(d: Disposable) {
                AppUtil.makeDebugLog("subscribing ")
            }


            override fun onComplete() {
                listener.onFetched(bookmarks)
            }

            override fun onError(e: Throwable) {
                AppUtil.makeErrorLog(e.toString())
                listener.onError()
            }

        }
        queryService.selectAllBookmarks(observer)
    }

    lateinit var bookmark : BookmarkedEnglish

    fun getEnglisbBookmarkedBasedOnPdfFileName(name: Int){
        val observer = object : Observer<BookmarkedEnglish>{
            override fun onSubscribe(d: Disposable) {
                AppUtil.makeDebugLog("onSubscribeee bookmark")

            }

            override fun onNext(t: BookmarkedEnglish) {
                AppUtil.makeDebugLog("on NEXXTT "+t.fileName)
                bookmark = t
                queryService.selectIndexedSentenceBasedOnId(bookmark.id,getBookmarksAndSentencesObserver())
            }

            override fun onComplete() {
                AppUtil.makeDebugLog("COMPLETED FINDING NAME")
            }

            override fun onError(e: Throwable) {

                AppUtil.makeDebugLog("ada errorr "+e.toString())
            }

        }
        queryService.getEnglishBookmarkBaaedOnLastId(name, observer)
    }

    lateinit var bookmarkAndIndexedSentenceCallback: BookmarkAndIndexedSentenceCallback
    lateinit var indexedSentenceCallback: IndexedSentenceCallback
    fun getBookmarksAndSentencesObserver() : Observer<ArrayList<IndexedSentence>> {
        return object : Observer<ArrayList<IndexedSentence>> {
            override fun onNext(t: ArrayList<IndexedSentence>) {
                AppUtil.makeDebugLog("indexedsentencessss s "+t.size)
                bookmarkAndIndexedSentenceCallback.onFetched(bookmark,t)
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
    }

    fun getAllIndexedSentencesBasedOnBookmarkedEnglishId(id : Int){
        val observer = object : Observer<List<IndexedSentence>> {
            override fun onNext(t: List<IndexedSentence>) {
                //bookmarkAndIndexedSentenceCallback.onFetched(t)
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

    fun getAllIndexedSentenceBasedOnLastId(id: Int, bookmarkAndIndexedSentenceCallback: BookmarkAndIndexedSentenceCallback)  {
        this.bookmarkAndIndexedSentenceCallback = bookmarkAndIndexedSentenceCallback
        getEnglisbBookmarkedBasedOnPdfFileName(id)
    }

    fun getIndexedSentencesObserver() : Observer<ArrayList<IndexedSentence>> {
        return object : Observer<ArrayList<IndexedSentence>> {
            override fun onNext(t: ArrayList<IndexedSentence>) {
                AppUtil.makeDebugLog("indexedsentencessss s "+t.size)
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
    }

    fun getAllIndexedSentenceBasedOnLastId(id : Int, indexedSentenceCallback: IndexedSentenceCallback){
        this.indexedSentenceCallback = indexedSentenceCallback
        queryService.selectIndexedSentenceBasedOnId(id,getIndexedSentencesObserver())
    }

    fun getEnglishBookmark(lastId: Int, bookmarkCallback: SingleBookmarkCallback) {
        val observer = object : Observer<BookmarkedEnglish>{
            override fun onSubscribe(d: Disposable) {
                AppUtil.makeDebugLog("onSubscribeee bookmark")

            }

            override fun onNext(t: BookmarkedEnglish) {
                bookmarkCallback.onFetched(t)
            }

            override fun onComplete() {
                AppUtil.makeDebugLog("COMPLETED FINDING NAME")
            }

            override fun onError(e: Throwable) {
                AppUtil.makeDebugLog("ada errorr "+e.toString())
            }

        }
        queryService.getEnglishBookmarkBaaedOnLastId(lastId, observer)
    }

    interface BookmarkAndIndexedSentenceCallback {
        fun onFetched(bookmark: BookmarkedEnglish, indexedSentences: ArrayList<IndexedSentence>)
    }

    interface IndexedSentenceCallback {
        fun onFetched(indexedSentences: ArrayList<IndexedSentence>)
    }

    interface SingleBookmarkCallback {
        fun onFetched(bookmark : BookmarkedEnglish)
    }


    interface BookmarksCallback {
        fun onFetched(bookmarks : ArrayList<BookmarkedEnglish>)
        fun onError()
    }


}