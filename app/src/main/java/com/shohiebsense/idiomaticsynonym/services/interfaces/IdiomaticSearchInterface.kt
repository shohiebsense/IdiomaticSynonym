package com.shohiebsense.idiomaticsynonym.services.interfaces

import com.shohiebsense.idiomaticsynonym.model.api.Synonym
import io.reactivex.Observable

/**
 * Created by Shohiebsense on 08/10/2017.
 */

interface IdiomaticSearchInterface {
    fun getSynonymFromIdiomword(word : String) : List<Observable<Synonym>>
    fun getRelatedWords(word : String)
}