package com.shohiebsense.idiomaticsynonym.services.dbs

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.shohiebsense.idiomaticsynonym.db.Bookmark
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.model.IndexedSentence
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.utils.StoryExample
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.db.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * Created by Shohiebsense on 13/01/2018.
 */
class BookmarkQueryService(val db : SQLiteDatabase) {

    fun insertPrerequisites() {
        var mCompositeDisposable = CompositeDisposable()
        mCompositeDisposable.add(Single.fromCallable {
            db.insert(Bookmark.TABLE_BOOKMARK_ENGLISH,
                    Bookmark.COLUMN_PDFFILENAME to "sample",
                    Bookmark.COLUMN_ENGLISH to StoryExample.getStory(),
                    Bookmark.COLUMN_INDONESIAN to StoryExample.getTranslation(),
                    Bookmark.COLUMN_IDIOM to "",
                    Bookmark.COLUMN_SENTENCE_INDEX to "",
                    Bookmark.COLUMN_UPLOAD_ID to ""
            )
        }.subscribeOn(Schedulers.io()).subscribe())
    }

    fun insertPhrasalVerbSample(){
        var mCompositeDisposable = CompositeDisposable()
        mCompositeDisposable.add(Single.fromCallable {
            db.insert(Bookmark.TABLE_BOOKMARK_ENGLISH,
                    Bookmark.COLUMN_PDFFILENAME to "phrasal verb",
                    Bookmark.COLUMN_ENGLISH to StoryExample.getPhrasalVerbIdiomSample(),
                    Bookmark.COLUMN_INDONESIAN to StoryExample.getPharasalVerbranslationSample(),
                    Bookmark.COLUMN_IDIOM to "",
                    Bookmark.COLUMN_SENTENCE_INDEX to "",
                    Bookmark.COLUMN_UPLOAD_ID to ""
            )
        }.subscribeOn(Schedulers.io()).subscribe())
    }

    fun insertExpressionSample(){
        var mCompositeDisposable = CompositeDisposable()
        mCompositeDisposable.add(Single.fromCallable {
            db.insert(Bookmark.TABLE_BOOKMARK_ENGLISH,
                    Bookmark.COLUMN_PDFFILENAME to "expression idiom",
                    Bookmark.COLUMN_ENGLISH to StoryExample.getExpressionIdiomSample(),
                    Bookmark.COLUMN_INDONESIAN to StoryExample.getExpressionIdiomTranslation(),
                    Bookmark.COLUMN_IDIOM to "",
                    Bookmark.COLUMN_SENTENCE_INDEX to "",
                    Bookmark.COLUMN_UPLOAD_ID to ""
            )
        }.subscribeOn(Schedulers.io()).subscribe())    }

    fun insertIntoBookmarkEnglish(fileName: String, wholeSentence: String, indonesian : String) : Int{
        var mCompositeDisposable = CompositeDisposable()
        var lastId = -1
        mCompositeDisposable.add(Single.fromCallable {
            db.insert(Bookmark.TABLE_BOOKMARK_ENGLISH,
                    Bookmark.COLUMN_PDFFILENAME to fileName,
                    Bookmark.COLUMN_ENGLISH to wholeSentence,
                    Bookmark.COLUMN_INDONESIAN to indonesian,
                    Bookmark.COLUMN_IDIOM to "",
                    Bookmark.COLUMN_SENTENCE_INDEX to "",
                    Bookmark.COLUMN_UPLOAD_ID to ""
            )
        }.subscribeOn(Schedulers.io()).subscribe())
        mCompositeDisposable.add(Single.fromCallable {
            lastId = selectLastInsertedId()
        }.subscribe())
        return lastId
    }

    fun updateIndonesianSentence(wholeSentence: String, sentenceIndex: StringBuilder){
        AppUtil.makeDebugLog("indonesian translation exists right ??? " + wholeSentence)
        db.update(Bookmark.TABLE_BOOKMARK_ENGLISH,
                Bookmark.COLUMN_INDONESIAN to wholeSentence,
                Bookmark.COLUMN_SENTENCE_INDEX to sentenceIndex.toString())
                .whereArgs(Bookmark.COLUMN_ID + " = " + selectLastInsertedId())
                .exec()
    }

    fun updateIndonesianSentence(observer : SingleObserver<Unit>, wholeSentence: String, id: String){
        Single.fromCallable<Unit> {
            db.update(Bookmark.TABLE_BOOKMARK_ENGLISH,
                    Bookmark.COLUMN_INDONESIAN to wholeSentence)
                    .whereArgs(Bookmark.COLUMN_ID + " = " + id)
                    .exec()
        }
                .subscribeOn(Schedulers.io())
                .subscribe(observer)
    }



    fun updateEnglishSentence(englishSentences: String) {
        db.update(Bookmark.TABLE_BOOKMARK_ENGLISH,
                Bookmark.COLUMN_ENGLISH to englishSentences)
                .whereArgs(Bookmark.COLUMN_ID + " = " + selectLastInsertedId())
                .exec()
    }

    fun updateIdioms(idioms: String,id : Int) {
        Log.e("shohiebsensenseee ","inserted "+idioms + "  "+id)

        Single.fromCallable {
            db.update(Bookmark.TABLE_BOOKMARK_ENGLISH,
                    Bookmark.COLUMN_IDIOM to idioms
            ) .whereArgs(Bookmark.COLUMN_ID + " = " + id)
                    .exec()
        }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    fun updateUploadId(id: String, uploadId: String){
        Single.fromCallable {
            db.update(Bookmark.TABLE_BOOKMARK_ENGLISH,
                    Bookmark.COLUMN_UPLOAD_ID to uploadId
            ) .whereArgs(Bookmark.COLUMN_ID + " = " + id)
                    .exec()
        }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    fun selectLastInsertedId() : Int{
        var lastInsertedId = -1
        db.select(Bookmark.TABLE_BOOKMARK_ENGLISH).column(Bookmark.COLUMN_ID).orderBy(Bookmark.COLUMN_ID, SqlOrderDirection.DESC).limit(1).exec {
            lastInsertedId = parseSingle(IntParser)
        }
        return lastInsertedId
    }


    fun countBookmarkEnglishTable() : Int{
        var count = 0
        db.select(Bookmark.TABLE_BOOKMARK_ENGLISH).exec {
            count = columnCount
        }
        AppUtil.makeDebugLog("size english table"+count)
        return count
    }

    fun countIndexedSentenceBasedOnFileName(name : Int) : Int{
        var count = 0
        db.select(Bookmark.TABLE_BOOKMARK_INDEXED_SENTENCES).whereArgs("id = $name").exec {
            count = columnCount
        }
        AppUtil.makeDebugLog("ebrapaa "+count)
        return count
    }

    fun countBookmarksBasedOnFileName(name : String) : Int{
        var count = 0
        db.select(Bookmark.TABLE_BOOKMARK_ENGLISH).whereArgs("(pdfFileName = $name)").exec {
            count = columnCount
        }
        AppUtil.makeDebugLog("ebrapaa "+count)
        return count
    }


    fun insertSentenceAndItsSource(context: Context, indexedSentences: List<IndexedSentence>, completedTransactionListener: CompletedTransactionListener) {
        var mCompositeDisposable = CompositeDisposable()
        mCompositeDisposable.add(Single
                .fromCallable {
                    indexedSentences.forEach {
                        it.bookId = selectLastInsertedId()
                        db.insert(Bookmark.TABLE_BOOKMARK_INDEXED_SENTENCES,
                                Bookmark.COLUMN_ID to TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis()),
                                Bookmark.COLUMN_SENTENCE to it.sentence,
                                Bookmark.COLUMN_SENTENCE_INDEX to it.index,
                                Bookmark.COLUMN_BOOKMARK_ENGLISH_ID to it.bookId
                        )
                        completedTransactionListener.onCompleted()
                    }



                }
                .subscribeOn(Schedulers.io())
                .subscribe())
    }

    fun insertSentenceAndItsSource(index: Int, sentence: String, idiom : String){

        var mCompositeDisposable = CompositeDisposable()
        var indexedSentences = mutableListOf<IndexedSentence>()

        mCompositeDisposable.add(Single
                .fromCallable {
                    db.insert(Bookmark.TABLE_BOOKMARK_INDEXED_SENTENCES,
                            Bookmark.COLUMN_SENTENCE to sentence,
                            Bookmark.COLUMN_SENTENCE_INDEX to index,
                            Bookmark.COLUMN_BOOKMARK_ENGLISH_ID to selectLastInsertedId()
                    )
                }
                .subscribeOn(Schedulers.io())
                .subscribe())
    }


    fun getEnglishBookmarkBaaedOnLastId(id: Int) : CharSequence{
        var englishText : CharSequence  = ""
        AppUtil.makeDebugLog("idddd  berapa? "+id)
        db.select(Bookmark.TABLE_BOOKMARK_ENGLISH).whereArgs(Bookmark.COLUMN_ID +"="+ id).limit(1).exec {
            //val parser = getBookmarkedEnglishParser()
            val parser = rowParser { id: Int, fileName: String, english: String, indonesian: String , idioms : String, indexedSentences : String, uploadId : String ->
                AppUtil.makeDebugLog("the englishhh is  "+english )
                //englishText = english
                BookmarkedEnglish(id,fileName,english,indonesian,idioms,indexedSentences,uploadId)
            }
            // parser2 = classParser<BookmarkedEnglish>()
            AppUtil.makeDebugLog("afawaefweaf ia ")
            englishText = parseSingle(parser).english
            close()
        }
        return englishText
    }

    fun getEnglishBookmarkBaaedOnLastId(id: Int, observer: Observer<BookmarkedEnglish>){
        AppUtil.makeDebugLog("english bookmark id nya berapa? "+id)
        Observable.create<BookmarkedEnglish> {
            e->
            db.select(Bookmark.TABLE_BOOKMARK_ENGLISH).whereArgs(Bookmark.COLUMN_ID +"="+ id).limit(1).exec {
                //val parser = getBookmarkedEnglishParser()
                val parser = rowParser { id: Int, fileName: String, english: String, indonesian : String, idioms : String, indexedSentences : String, uploadId : String ->
                    AppUtil.makeErrorLog("dapet nih "+id+"  "+fileName+ "   "+idioms)
                    BookmarkedEnglish(id,fileName,english,indonesian,idioms,indexedSentences,uploadId)
                }
                // parser2 = classParser<BookmarkedEnglish>()
                AppUtil.makeDebugLog("hiiiifiaewfj ia ")
                e.onNext(parseSingle(parser))
                AppUtil.makeErrorLog("not getting into this")
                e.onComplete()
                close()
                AppUtil.makeDebugLog("finishhz")
            }
        } .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(observer)
    }

    fun getBookmarkCounts() : Int{
        var count = 0
        db.select(Bookmark.TABLE_BOOKMARK_ENGLISH).column(Bookmark.COLUMN_ID).exec {
            count = getCount()
        }
        return count
    }

    fun getIdiomFoundedCount() : Int {
        var count = 0
        var idioms = ""
        db.select(Bookmark.TABLE_BOOKMARK_ENGLISH).column(Bookmark.COLUMN_IDIOM). exec {
            var parser = StringParser
            asSequence().forEach {
                row ->
                idioms += StringParser.parseRow(row)
            }
            if(idioms.isNotBlank()){
                count = AppUtil.getListOfIdioms(idioms).size
            }
        }
        return count
    }

    fun getIndexedSentencesFoundedCount() : Int {
        var count = 0
        var idioms = ""
        db.select(Bookmark.TABLE_BOOKMARK_ENGLISH).column(Bookmark.COLUMN_SENTENCE_INDEX). exec {
            var parser = StringParser
            asSequence().forEach {
                row ->
                idioms += StringParser.parseRow(row)
            }
            if(idioms.isNotBlank()){
                count = AppUtil.getListOfIndexedSentences(idioms).size
            }
        }
        return count
    }

    fun selectAllBookmarks() : List<BookmarkedEnglish>{
        val bookmarkedEnglishes = arrayListOf<BookmarkedEnglish>()
        db.select(Bookmark.TABLE_BOOKMARK_ENGLISH).exec {
            AppUtil.makeDebugLog(" jumlahnya "+columnCount)

            val parser = getBookmarkedEnglishParser()
            asSequence().forEach {
                row ->
                AppUtil.makeDebugLog("h"+ row.size)

                bookmarkedEnglishes.add(parser.parseRow(row))
            }
            close()
        }
        return bookmarkedEnglishes
    }


    fun selectAllBookmarks(observer : Observer<List<BookmarkedEnglish>>) {
        val bookmarkedEnglishes = arrayListOf<BookmarkedEnglish>()
        Observable.create<List<BookmarkedEnglish>> { e ->
            db.select(Bookmark.TABLE_BOOKMARK_ENGLISH).exec {
                val parser = getBookmarkedEnglishParser()
                asSequence().forEach {
                    row ->
                    Log.e("shohiebsenseee ",""+ parser.parseRow(row).id + " " +parser.parseRow(row).fileName + " " + parser.parseRow(row).idioms )
                    bookmarkedEnglishes.add(parser.parseRow(row))
                }
                if(!e.isDisposed){
                    e.onNext(bookmarkedEnglishes)
                }
                e.onComplete()
                close()
            }
        }.subscribe(observer)
    }

    fun getIndexedSentencesBasedOnEnglishBookmarkedId(id : Int, observer : Observer<List<IndexedSentence>>){
        val indexedSentences = mutableListOf<IndexedSentence>()

        Observable.create<List<IndexedSentence>> {
            e->
            db.select(Bookmark.TABLE_BOOKMARK_INDEXED_SENTENCES).columns(Bookmark.COLUMN_SENTENCE).whereArgs("("+Bookmark.COLUMN_BOOKMARK_ENGLISH_ID+"= $id)").exec {
                val parser = getIndexedSentenceIdiomParser()
                /*  parseList(object : MapRowParser<List<TranslatedIdiom>> {
                      override fun parseRow(columns: Map<String, Any?>): List<TranslatedIdiom> {
                          val id = columns.getValue(Idioms.COLUMN_ID)
                          val idiom = columns.getValue(Idioms.COLUMN_IDIOM)
                          val meaning = columns.getValue(Idioms.COLUMN_MEANING)
                          translatedList.add(TranslatedIdiom(id as Int,idiom as String,meaning as String))
                          return translatedList
                      }

                  })*/
                asSequence().forEach { row -> indexedSentences.add(parser.parseRow(row))
                }
                e.onNext(indexedSentences)
                e.onComplete()
                close()
            }
        }.subscribe(observer)
    }



    fun selectIndexedSentenceBasedOnId(id: Int, observer: Observer<ArrayList<IndexedSentence>>){
        var indexedSentences = arrayListOf<IndexedSentence>()

        Observable.create<ArrayList<IndexedSentence>>{
            e->
            AppUtil.makeDebugLog("masuk indexed sentence")
            db.select(Bookmark.TABLE_BOOKMARK_INDEXED_SENTENCES).columns(Bookmark.COLUMN_SENTENCE).whereArgs("("+Bookmark.COLUMN_BOOKMARK_ENGLISH_ID+"= $id)") .exec {
                val parser = getIndexedSentenceIdiomParser()
                /*  parseList(object : MapRowParser<List<TranslatedIdiom>> {
                      override fun parseRow(columns: Map<String, Any?>): List<TranslatedIdiom> {
                          val id = columns.getValue(Idioms.COLUMN_ID)
                          val idiom = columns.getValue(Idioms.COLUMN_IDIOM)
                          val meaning = columns.getValue(Idioms.COLUMN_MEANING)
                          translatedList.add(TranslatedIdiom(id as Int,idiom as String,meaning as String))
                          return translatedList
                      }

                  })*/
                asSequence().forEach { row -> indexedSentences.add(parser.parseRow(row))
                }
                e.onNext(indexedSentences)
                e.onComplete()
                close()
            }
        }
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(observer)
    }

    fun getGetIndexedSentencesBasedOnBookmarkId(id : Int) {

    }




    fun getIndexedSentenceIdiomParser() : RowParser<IndexedSentence> {
        return rowParser{indexedSentence : String ->
            return@rowParser IndexedSentence(indexedSentence)
        }
    }



    fun getBookmarkedEnglishParser() : RowParser<BookmarkedEnglish>{
        return rowParser { id: Int, fileName: String, english: String, indonesian : String, idioms : String, indexedSentences : String, uploadId : String ->
            return@rowParser BookmarkedEnglish(id, fileName, english, indonesian,idioms,indexedSentences,uploadId)
        }
    }



    interface CompletedTransactionListener{
        fun onCompleted()
    }



}