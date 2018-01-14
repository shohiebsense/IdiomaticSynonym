package com.shohiebsense.idiomaticsynonym.services.interfaces

import com.shohiebsense.idiomaticsynonym.model.TranslatedIdiom
import io.reactivex.Observer

/**
 * Created by Shohiebsense on 15/10/2017.
 */
interface QueryInterface {

    fun getAll(consumer : Observer<TranslatedIdiom>) : MutableList<TranslatedIdiom>
/*    fun getAllUntranslated() : Single<List<TranslatedIdiom>>
    fun getIdiomFromWord(sentence : String) : Observable<TranslatedIdiom>
    fun getEnglishExampleFromWord(sentence : String) : Observable<TranslatedIdiom>
    fun getIndonesianExampleFromWord(sentence : String) : Observable<TranslatedIdiom>
    fun getUntranslatedIdiomByWord(sentence : String) : Observable<UntranslatedIdiom>
    //soon
    fun getExampleByWord(sentence : String) : Observable<UntranslatedIdiom>*/

}