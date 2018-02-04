package com.shohiebsense.idiomaticsynonym.view.callbacks

/**
 * Created by Shohiebsense on 11/12/2017.
 */

interface TranslatedDisplayCallback {
    fun onSingleSynonymIdiomClicked(idiom: String, index: Int)
    fun onFinishExtractText(decoratedSpan: CharSequence)
}