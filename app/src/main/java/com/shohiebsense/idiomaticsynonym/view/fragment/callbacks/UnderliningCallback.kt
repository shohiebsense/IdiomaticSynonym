package com.shohiebsense.idiomaticsynonym.view.fragment.callbacks

import com.shohiebsense.idiomaticsynonym.model.TranslatedIdiom
import com.shohiebsense.idiomaticsynonym.model.UntranslatedIdiom
import java.util.*

/**
 * Created by Shohiebsense on 21/10/2017.
 */
interface UnderliningCallback {
    fun onTranslatingText()
    fun onErrorTranslatingText()
    fun onFinishedTranslatingText()
    fun onFetchingDatabase()
    fun onFinishedFetchingTranslatedDatabase(translatedIdiomList: MutableList<TranslatedIdiom>)
    fun onFinishedFetchingUntranslatedDatabase(untranslatedIdiomList: MutableList<UntranslatedIdiom>)
    fun onErrorFetchingDatabase()
    fun onErrorUnderliningText()
    fun onFinishedUnderliningText(decoratedSpan: ArrayList<CharSequence>)
    fun onFindingTranslatedIdiom()
    fun onFinishedFindingTranslatedIdiom(decoratedSpan: ArrayList<CharSequence>)
    fun onFindingUntranslatedIdiom()
    fun onFinishedUntranslatedIdiom()
    fun onClickedIdiomText(idiomText: String, index: Int, sentence: String)
    fun onErrorClickedIdiomText()
    fun onTranslatingIdiomOneByOne()
    fun onFinishedTranslatingIdiomOneByOne(combineStringMeaning: MutableList<String>, sentenceIndex: Int)
    fun onErrorTranslatingIdiomOneByOne()
}