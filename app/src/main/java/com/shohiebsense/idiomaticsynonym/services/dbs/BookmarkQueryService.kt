package com.shohiebsense.idiomaticsynonym.services.dbs

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.shohiebsense.idiomaticsynonym.db.Bookmark
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.model.IndexedSentence
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.db.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * Created by Shohiebsense on 13/01/2018.
 */
class BookmarkQueryService(val db : SQLiteDatabase) {
    var lastInsertedId = 0

    fun insertIntoBookmarkEnglish(fileName: String, wholeSentence: String, indonesian : String){
        AppUtil.makeDebugLog("englishhh "+wholeSentence)
        AppUtil.makeDebugLog("indonesiann "+indonesian)
        var mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable.add(Single.fromCallable {

            db.insert(Bookmark.TABLE_BOOKMARK_ENGLISH,
                    Bookmark.COLUMN_PDFFILENAME to fileName,
                    Bookmark.COLUMN_ENGLISH to wholeSentence,
                    Bookmark.COLUMN_INDONESIAN to indonesian
            )

            lastInsertedId = selectLastInsertedId()
        }.subscribeOn(Schedulers.io()).subscribe())

    }

    fun updateIndonesianSentence(wholeSentence: String){
        AppUtil.makeDebugLog("indonesian exists right ??? " + wholeSentence)
        db.update(Bookmark.TABLE_BOOKMARK_ENGLISH,
                Bookmark.COLUMN_INDONESIAN to wholeSentence)
                .whereArgs(Bookmark.COLUMN_ID + " = " + lastInsertedId)
                .exec()
    }

    fun updateEnglishSentence(englishSentences: String) {
        db.update(Bookmark.TABLE_BOOKMARK_ENGLISH,
                Bookmark.COLUMN_ENGLISH to englishSentences)
                .whereArgs(Bookmark.COLUMN_ID + " = " + lastInsertedId)
                .exec()
    }


    fun selectLastInsertedId() : Int{
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
                        it.bookId = lastInsertedId
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
                            Bookmark.COLUMN_IDIOM to idiom,
                            Bookmark.COLUMN_BOOKMARK_ENGLISH_ID to lastInsertedId
                            )
                }
                .subscribeOn(Schedulers.io())
                .subscribe())
    }

    fun getEnglishBookmarkBaaedOnLastId(id: Int, observer: Observer<BookmarkedEnglish>){
        AppUtil.makeDebugLog("idddd nya berapa? "+id)
        Observable.create<BookmarkedEnglish> {
            e->
            db.select(Bookmark.TABLE_BOOKMARK_ENGLISH).whereArgs(Bookmark.COLUMN_ID +"="+ id).limit(1).exec {
                //val parser = getBookmarkedEnglishParser()
                val parser = rowParser { id: Int, fileName: String, english: String, indonesian : String ->
                    BookmarkedEnglish(id,fileName,english,indonesian)
                }
                // parser2 = classParser<BookmarkedEnglish>()
                AppUtil.makeDebugLog("hiiiifiaewfj ia ")
                e.onNext(parseSingle(parser))

                e.onComplete()
                close()
            }
        }.subscribe(observer)
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
        db.select(Bookmark.TABLE_BOOKMARK_INDEXED_SENTENCES).column(Bookmark.COLUMN_ID). exec {
            count = getCount()
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
        return rowParser { id: Int, fileName: String, english: String, indonesian : String ->
            return@rowParser BookmarkedEnglish(id, fileName, english, indonesian)
        }
    }

    interface CompletedTransactionListener{
        fun onCompleted()
    }



}