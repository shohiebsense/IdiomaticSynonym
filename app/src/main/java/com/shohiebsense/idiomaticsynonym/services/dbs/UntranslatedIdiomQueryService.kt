package com.shohiebsense.idiomaticsynonym.services.dbs

import com.shohiebsense.idiomaticsynonym.model.UntranslatedIdiom
import com.shohiebsense.idiomaticsynonym.services.interfaces.UntranslatedQueryIdiomInterface
import io.reactivex.Observable

/**
 * Created by Shohiebsense on 07/10/2017.
 */

class UntranslatedIdiomQueryService : UntranslatedQueryIdiomInterface {
    override fun getUntranslatedIdiomByWord(word: String): Observable<UntranslatedIdiom> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getExampleByWord(word: String): Observable<UntranslatedIdiom> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}