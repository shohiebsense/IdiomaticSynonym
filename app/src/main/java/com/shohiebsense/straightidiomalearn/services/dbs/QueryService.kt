package com.shohiebsense.straightidiomalearn.services.dbs

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.shohiebsense.straightidiomalearn.IdiomApplication
import com.shohiebsense.straightidiomalearn.db.Idioms
import com.shohiebsense.straightidiomalearn.model.TranslatedIdiom
import com.shohiebsense.straightidiomalearn.model.UntranslatedIdiom
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.db.*
import com.shohiebsense.straightidiomalearn.db.database

/**
 * Created by Shohiebsense on 15/10/2017.
 */
class QueryService(val db : SQLiteDatabase)  {

    init {
        //AppUtil.makeDebugLog("mmkk sini")

        //query = (context.application as IdiomApplication).data
    }


    fun getAllTranslated(consumer : Observer<MutableList<TranslatedIdiom>>): MutableList<TranslatedIdiom> {
        AppUtil.makeDebugLog("translating query")
        var translatedList = mutableListOf<TranslatedIdiom>()
        var translatedIdiom  = arrayListOf<TranslatedIdiom>()
       // query.select(TranslatedIdiom::class).get().observable().observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(consumer)
      //  query.count(TranslatedIdiom::class).get().observable().subscribe(translatedConsumer)
        Observable.create<MutableList<TranslatedIdiom>>{
            e->
            AppUtil.makeDebugLog("mmasukk sini")
            db.select(Idioms.TABLE_TRANSLATED_IDIOM).columns(Idioms.COLUMN_ID,Idioms.COLUMN_IDIOM, Idioms.COLUMN_MEANING)
                    .exec {
                        val parser = getTranslatedIidomParser()
                      /*  parseList(object : MapRowParser<List<TranslatedIdiom>> {
                            override fun parseRow(columns: Map<String, Any?>): List<TranslatedIdiom> {
                                val id = columns.getValue(Idioms.COLUMN_ID)
                                val idiom = columns.getValue(Idioms.COLUMN_IDIOM)
                                val meaning = columns.getValue(Idioms.COLUMN_MEANING)
                                translatedList.add(TranslatedIdiom(id as Int,idiom as String,meaning as String))
                                return translatedList
                            }

                        })*/
                        asSequence().forEach { row -> translatedList.add(parser.parseRow(row)) }
                        e.onNext(translatedList)
                        e.onComplete()
                        close()
                    }

        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(consumer)
        return translatedList;
    }

    fun getTranslatedIidomParser() : RowParser<TranslatedIdiom>{
        return rowParser{id : Int, idiom : String, meaning : String->
            return@rowParser TranslatedIdiom(id, idiom, meaning)
        }
    }

    fun getUntranslatedIdiomParser() : RowParser<UntranslatedIdiom>{
        return rowParser { id: Int, idiom: String ->
            return@rowParser UntranslatedIdiom(id, idiom)
        }
    }


    fun getAllUntranslated(consumer : Observer<MutableList<UntranslatedIdiom>>) {
      //  query.select(UntranslatedIdiom::class).get().observable().observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(consumer)
        var untranslatedList = mutableListOf<UntranslatedIdiom>()
        Observable.create<MutableList<UntranslatedIdiom>>{
            e->
            AppUtil.makeDebugLog("masuk untranslated")
            db.select(Idioms.TABLE_UNTRANSLATED_IDIOM).columns(Idioms.COLUMN_ID,Idioms.COLUMN_IDIOM)
                    .exec {
                        val parser = getUntranslatedIdiomParser()
                        /*  parseList(object : MapRowParser<List<TranslatedIdiom>> {
                              override fun parseRow(columns: Map<String, Any?>): List<TranslatedIdiom> {
                                  val id = columns.getValue(Idioms.COLUMN_ID)
                                  val idiom = columns.getValue(Idioms.COLUMN_IDIOM)
                                  val meaning = columns.getValue(Idioms.COLUMN_MEANING)
                                  translatedList.add(TranslatedIdiom(id as Int,idiom as String,meaning as String))
                                  return translatedList
                              }

                          })*/
                        asSequence().forEach { row -> untranslatedList.add(parser.parseRow(row))
                        }
                        e.onNext(untranslatedList)
                        e.onComplete()
                        close()
                    }
        }
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(consumer)

        AppUtil.makeDebugLog("untranslatedd querying ")
    }

    /*
      override fun getIdiomFromWord(word: String): Observable<TranslatedIdiom> {
       }

       override fun getEnglishExampleFromWord(word: String): Observable<TranslatedIdiom> {
       }

       override fun getIndonesianExampleFromWord(word: String): Observable<TranslatedIdiom> {
       }

       override fun getUntranslatedIdiomByWord(word: String): Observable<UntranslatedIdiom> {
       }

       override fun getExampleByWord(word: String): Observable<UntranslatedIdiom> {
       }*/
}