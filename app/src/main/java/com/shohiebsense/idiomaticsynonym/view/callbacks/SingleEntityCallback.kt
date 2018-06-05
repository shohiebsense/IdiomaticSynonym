package com.shohiebsense.idiomaticsynonym.view.callbacks

import com.shohiebsense.idiomaticsynonym.model.TranslatedIdiom


/**
 * Created by Shohiebsense on 26/05/2018
 */

interface SingleEntityCallback {
    fun onFetched(translatedIdiom: TranslatedIdiom?)
    fun onError()
}