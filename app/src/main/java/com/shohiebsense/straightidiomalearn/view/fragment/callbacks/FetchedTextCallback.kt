package com.shohiebsense.straightidiomalearn.view.fragment.callbacks

import android.text.SpannableString
import com.shohiebsense.straightidiomalearn.model.TranslatedIdiom
import com.shohiebsense.straightidiomalearn.model.UntranslatedIdiom

/**
 * Created by Shohiebsense on 21/10/2017.
 */
interface FetchedTextCallback {
    fun onTranslatingText()
    fun onErrorTranslatingText()
    fun onFinishedTranslatingText()
    fun onFetchingDatabase()
    fun onFinishedFetchingTranslatedDatabase(translatedIdiomList: MutableList<TranslatedIdiom>)
    fun onFinishedFetchingUntranslatedDatabase(untranslatedIdiomList: MutableList<UntranslatedIdiom>)
    fun onErrorFetchingDatabase()
    fun onErrorUnderliningText(decoratedSpan: SpannableString)
    fun onFinishedUnderliningText(decoratedSpan: SpannableString)
    fun onFindingTranslatedIdiom()
    fun onFinishedFindingTranslatedIdiom(anuu: String, decoratedSpan: SpannableString)
    fun onFindingUntranslatedIdiom()
    fun onFinishedUntranslatedIdiom()
    fun onClickedIdiomText(idiomText : String)
    fun onErrorClickedIdiomText()
    fun onTranslatingIdiomOneByOne()
    fun onFinishedTranslatingIdiomOneByOne(combineStringMeaning: MutableList<String>)
    fun onErrorTranslatingIdiomOneByOne()
}