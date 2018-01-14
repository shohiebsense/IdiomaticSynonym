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

/**
 * Created by Shohiebsense on 13/01/2018.
 */
class BookmarkQueryService(val db : SQLiteDatabase) {
    var lastInsertedId = 0

    fun insertIntoBookmarkEnglish(context: Context, fileName: String, wholeSentence: CharSequence){
        var mCompositeDisposable = CompositeDisposable()
        mCompositeDisposable.add(Single.fromCallable {
            db.insert(Bookmark.TABLE_BOOKMARK_ENGLISH,
                    Bookmark.COLUMN_PDFFILENAME to fileName,
                    Bookmark.COLUMN_ENGLISH to wholeSentence
            )

            db.select(Bookmark.TABLE_BOOKMARK_ENGLISH).column(Bookmark.COLUMN_ID).limit(1).orderBy(Bookmark.COLUMN_ID, SqlOrderDirection.DESC).exec {
                lastInsertedId = parseSingle(IntParser)
            }


        }.subscribeOn(Schedulers.io()).subscribe())

    }

    fun insertSentenceAndItsSource(context: Context, indexedSentences: List<IndexedSentence>, completedTransactionListener: CompletedTransactionListener) {
        var mCompositeDisposable = CompositeDisposable()
        mCompositeDisposable.add(Single
                .fromCallable {
                    indexedSentences.forEach {
                        it.bookId = lastInsertedId
                        db.insert(Bookmark.TABLE_BOOKMARK_INDEXED_SENTENCES,
                                Bookmark.COLUMN_SENTENCE to it.sentence,
                                Bookmark.COLUMN_SENTENCE_INDEX to it.index,
                                Bookmark.COLUMN_BOOKMARK_ENGLISH_ID to it,
                                Bookmark.COLUMN_BOOKMARK_ENGLISH_ID to it.bookId
                        )
                        completedTransactionListener.onCompleted()
                    }



                }
                .subscribeOn(Schedulers.io())
                .subscribe())
    }

    fun insertSentenceAndItsSource(context: Context, index: Int, sentence: String, idiom : String){

        var mCompositeDisposable = CompositeDisposable()
        var indexedSentences = mutableListOf<IndexedSentence>()



        mCompositeDisposable.add(Single
                .fromCallable {
                    db.insert(Bookmark.TABLE_BOOKMARK_INDEXED_SENTENCES,
                            Bookmark.COLUMN_SENTENCE to sentence,
                            Bookmark.COLUMN_SENTENCE_INDEX to index,
                            Bookmark.COLUMN_BOOKMARK_ENGLISH_ID to lastInsertedId
                            )
                }
                .subscribeOn(Schedulers.io())
                .subscribe())
    }

    fun getEnglishBookmarkBaaedOnPdfFileName(name : String, observer : Observer<BookmarkedEnglish>){

        Observable.create<BookmarkedEnglish> {
            e->
            db.select(Bookmark.TABLE_BOOKMARK_ENGLISH).whereArgs("(pdfFileName = $name)").limit(1).exec {
                val parser = getBookmarkedEnglishParser()

                e.onNext(parseSingle(parser))
                e.onComplete()
                close()
            }
        }
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
        }
    }


    fun selectIndexedSentenceBasedOnPdfFileName(name : String, observer : Observer<List<IndexedSentence>>){
        var indexedSentences = mutableListOf<IndexedSentence>()

        Observable.create<List<IndexedSentence>>{
            e->
            AppUtil.makeDebugLog("masuk indexed sentence")
            db.select(Bookmark.TABLE_BOOKMARK_INDEXED_SENTENCES).columns(Bookmark.COLUMN_SENTENCE).exec {
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


    fun getIndexedSentenceIdiomParser() : RowParser<IndexedSentence> {
        return rowParser{indexedSentence : String ->
            return@rowParser IndexedSentence(indexedSentence)
        }
    }

    fun getBookmarkedEnglishParser() : RowParser<BookmarkedEnglish>{
        return rowParser { id: Int, fileName: String, texts: String ->
            return@rowParser BookmarkedEnglish(id, fileName, texts)
        }
    }

    interface CompletedTransactionListener{
        fun onCompleted()
    }



}