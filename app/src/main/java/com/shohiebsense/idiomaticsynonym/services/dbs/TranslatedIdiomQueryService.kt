package com.shohiebsense.idiomaticsynonym.services.dbs


import com.shohiebsense.idiomaticsynonym.model.TranslatedIdiom
import com.shohiebsense.idiomaticsynonym.services.interfaces.TranslatedIdiomQueryInterface
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by Shohiebsense on 07/10/2017.
 */

object TranslatedIdiomQueryService : TranslatedIdiomQueryInterface {

    override fun getAll(): Single<List<TranslatedIdiom>> {
       /* return  Select().from(TranslatedIdiom::class.java)
                .where()
                .rx()
                .flowQueryList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/



        /*Select().from(TranslatedIdiom::class.java)
                .rx()
                .flowQueryList()*/

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    override fun getIdiomFromWord(word: String): Observable<TranslatedIdiom> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getEnglishExampleFromWord(word: String): Observable<TranslatedIdiom> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getIndonesianExampleFromWord(word: String): Observable<TranslatedIdiom> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}