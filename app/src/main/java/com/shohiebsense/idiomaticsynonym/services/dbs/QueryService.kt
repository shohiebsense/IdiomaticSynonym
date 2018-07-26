package com.shohiebsense.idiomaticsynonym.services.dbs

import android.database.sqlite.SQLiteDatabase
import com.shohiebsense.idiomaticsynonym.db.Idioms
import com.shohiebsense.idiomaticsynonym.model.CombinedIdiom
import com.shohiebsense.idiomaticsynonym.model.TranslatedIdiom
import com.shohiebsense.idiomaticsynonym.model.UntranslatedIdiom
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.db.*

/**
 * Created by Shohiebsense on 15/10/2017.
 */
class QueryService(val db : SQLiteDatabase)  {

    init {
        //AppUtil.makeDebugLog("mmkk sini")
        //query = (context.application as IdiomApplication).data
    }

    fun getAllIdiomsOnly(consumer: Observer<ArrayList<CombinedIdiom>>)  {
        val idioms = arrayListOf<CombinedIdiom>()
        Observable.create<ArrayList<CombinedIdiom>> {
            e->
            db.select(Idioms.TABLE_UNTRANSLATED_IDIOM).columns(Idioms.COLUMN_IDIOM).groupBy(Idioms.COLUMN_IDIOM)
                    .exec {
                        val parser = getCombinedIdiomParser()
                        asSequence().forEach {
                            row -> idioms.add(parser.parseRow(row))
                        }
                    }
            db.select(Idioms.TABLE_TRANSLATED_IDIOM).columns(Idioms.COLUMN_IDIOM,Idioms.COLUMN_MEANING)
                    .exec {
                        val parser = getCombinedIdiomTranslatedParser()
                        asSequence().forEach {
                            row->idioms.add(parser.parseRow(row))
                        }
                        if(!e.isDisposed){
                            e.onNext(idioms)
                        }
                        close()
                    }
            e.onComplete()
        }.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(consumer)
    }


    fun getAllSingleInstance(consumer: Observer<ArrayList<CombinedIdiom>>)  {
        val idioms = arrayListOf<CombinedIdiom>()
        Observable.create<ArrayList<CombinedIdiom>> {
            e->
            db.select(Idioms.TABLE_UNTRANSLATED_IDIOM).columns(Idioms.COLUMN_IDIOM)
                    .exec {
                        val parser = getCombinedIdiomParser()
                        asSequence().forEach {
                            row -> idioms.add(parser.parseRow(row))
                        }

                    }

            db.select(Idioms.TABLE_TRANSLATED_IDIOM).columns(Idioms.COLUMN_IDIOM,Idioms.COLUMN_MEANING)
                    .exec {
                        val parser = getCombinedIdiomParser()
                        asSequence().forEach {
                            row->idioms.add(parser.parseRow(row))
                        }
                        if(!e.isDisposed){
                            e.onNext(idioms)
                        }
                        close()
                    }
            e.onComplete()
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(consumer)
    }


    fun getAllTranslated(consumer : Observer<ArrayList<TranslatedIdiom>>): ArrayList<TranslatedIdiom> {
        AppUtil.makeDebugLog("translating query")
        var translatedList = arrayListOf<TranslatedIdiom>()
        var translatedIdiom  = arrayListOf<TranslatedIdiom>()
       // query.select(TranslatedIdiom::class).get().observable().observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(consumer)
      //  query.count(TranslatedIdiom::class).get().observable().subscribe(translatedConsumer)
        Observable.create<ArrayList<TranslatedIdiom>>{
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

    fun getCombinedIdiomParser() : RowParser<CombinedIdiom>{
        return rowParser{idiom : String ->
            return@rowParser CombinedIdiom(idiom)
        }
    }

    fun getCombinedIdiomTranslatedParser() : RowParser<CombinedIdiom>{
        return rowParser{idiom : String, meaning : String ->
            return@rowParser CombinedIdiom(idiom,meaning)
        }
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

    fun getTranslatedIdiomParser() : RowParser<TranslatedIdiom>{
        return rowParser{id : Int, idiom : String, meaning : String ->
            AppUtil.makeErrorLog("hallowww each "+idiom+meaning)
            return@rowParser TranslatedIdiom(id,idiom,meaning)
        }
    }



    fun getAllUntranslated(consumer : Observer<ArrayList<UntranslatedIdiom>>) {
      //  query.select(UntranslatedIdiom::class).get().observable().observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(consumer)
        var untranslatedList = arrayListOf<UntranslatedIdiom>()
        Observable.create<ArrayList<UntranslatedIdiom>>{
            e->
            AppUtil.makeDebugLog("masuk untranslated")
            db.select(Idioms.TABLE_UNTRANSLATED_IDIOM).columns(Idioms.COLUMN_ID,Idioms.COLUMN_IDIOM).groupBy(Idioms.COLUMN_IDIOM)
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


    fun getIdiom(idiom : String, consumer : Observer<TranslatedIdiom>) {
        AppUtil.makeErrorLog("harusnya masuk sini lahhh ")
        Observable.create<TranslatedIdiom>{
            e->
            db.select(Idioms.TABLE_TRANSLATED_IDIOM).whereArgs(Idioms.COLUMN_IDIOM +"=\'"+idiom+"\'")
                    .exec {
                        val parser = getTranslatedIdiomParser()
                        //asSequence().forEach { row -> AppUtil.makeDebugLog ("wuttt "+ parser.parseRow(row).idiom) }
                        e.onNext(parseOpt(parser)!!)
                        e.onComplete()
                        close()
                    }
        }
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(consumer)
    }






}