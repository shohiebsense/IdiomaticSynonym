package com.shohiebsense.straightidiomalearn.services.interfaces

import com.shohiebsense.straightidiomalearn.model.TranslatedIdiom
import io.reactivex.Observer

/**
 * Created by Shohiebsense on 15/10/2017.
 */
interface QueryInterface {

    fun getAll(consumer : Observer<TranslatedIdiom>) : MutableList<TranslatedIdiom>
/*    fun getAllUntranslated() : Single<List<TranslatedIdiom>>
    fun getIdiomFromWord(word : String) : Observable<TranslatedIdiom>
    fun getEnglishExampleFromWord(word : String) : Observable<TranslatedIdiom>
    fun getIndonesianExampleFromWord(word : String) : Observable<TranslatedIdiom>
    fun getUntranslatedIdiomByWord(word : String) : Observable<UntranslatedIdiom>
    //soon
    fun getExampleByWord(word : String) : Observable<UntranslatedIdiom>*/

}