package com.shohiebsense.idiomaticsynonym.services.dbs

import android.database.sqlite.SQLiteDatabase
import com.shohiebsense.idiomaticsynonym.db.IdiomsDbConstants
import com.shohiebsense.idiomaticsynonym.model.CombinedIdiom
import com.shohiebsense.idiomaticsynonym.model.Idiom
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.db.*

/**
 * Created by Shohiebsense on 15/10/2017.
 */
class IdiomTableQueryService(val db : SQLiteDatabase) {

    init {
        //AppUtil.makeDebugLog("mmkk sini")
        //query = (context.application as IdiomApplication).data
    }

    fun getAllIdioms() : Maybe<ArrayList<Idiom>> {
        val idioms = arrayListOf<Idiom>()
        return Maybe.create<ArrayList<Idiom>> {
            db.select(IdiomsDbConstants.TABLE_IDIOMS)
                    .exec {
                        val parser = idiomParser()
                        asSequence().forEach { row ->
                            idioms.add(parser.parseRow(row))
                        }
                        it.onSuccess(idioms)
                    }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun idiomParser(): RowParser<Idiom> {
        return rowParser { id: Int, idiomText: String, translation: String, similar: String? ->
            return@rowParser Idiom(id, idiomText, translation, similar)
        }
    }

}
