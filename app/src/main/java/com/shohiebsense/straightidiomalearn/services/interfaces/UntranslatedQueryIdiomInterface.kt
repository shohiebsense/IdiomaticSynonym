package com.shohiebsense.straightidiomalearn.services.interfaces

import io.reactivex.Observable
import com.shohiebsense.straightidiomalearn.model.UntranslatedIdiom

/**
 * Created by Shohiebsense on 07/10/2017.
 */

interface UntranslatedQueryIdiomInterface {
    fun getUntranslatedIdiomByWord(word : String) : Observable<UntranslatedIdiom>
    //soon
    fun getExampleByWord(word : String) : Observable<UntranslatedIdiom>

}