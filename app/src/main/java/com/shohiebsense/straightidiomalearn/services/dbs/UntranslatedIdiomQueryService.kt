package com.shohiebsense.straightidiomalearn.services.dbs

import com.shohiebsense.straightidiomalearn.services.interfaces.UntranslatedQueryIdiomInterface
import io.reactivex.Observable
import com.shohiebsense.straightidiomalearn.model.UntranslatedIdiom

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