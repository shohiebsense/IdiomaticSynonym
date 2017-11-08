package com.shohiebsense.straightidiomalearn.services.interfaces

import io.reactivex.Observable
import io.reactivex.Single
import com.shohiebsense.straightidiomalearn.model.TranslatedIdiom

/**
 * Created by Shohiebsense on 07/10/2017.
 */

interface TranslatedIdiomQueryInterface{
    fun getAll() : Single<List<TranslatedIdiom>>
    fun getIdiomFromWord(word : String) : Observable<TranslatedIdiom>
    fun getEnglishExampleFromWord(word : String) : Observable<TranslatedIdiom>
    fun getIndonesianExampleFromWord(word : String) : Observable<TranslatedIdiom>

}