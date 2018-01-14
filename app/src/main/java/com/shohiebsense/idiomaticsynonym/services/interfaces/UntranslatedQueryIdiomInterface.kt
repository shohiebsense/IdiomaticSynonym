package com.shohiebsense.idiomaticsynonym.services.interfaces

import com.shohiebsense.idiomaticsynonym.model.UntranslatedIdiom
import io.reactivex.Observable

/**
 * Created by Shohiebsense on 07/10/2017.
 */

interface UntranslatedQueryIdiomInterface {
    fun getUntranslatedIdiomByWord(word : String) : Observable<UntranslatedIdiom>
    //soon
    fun getExampleByWord(word : String) : Observable<UntranslatedIdiom>

}