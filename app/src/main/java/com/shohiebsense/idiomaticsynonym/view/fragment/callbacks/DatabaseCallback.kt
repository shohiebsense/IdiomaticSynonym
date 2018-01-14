package com.shohiebsense.idiomaticsynonym.view.fragment.callbacks

/**
 * Created by Shohiebsense on 01/12/2017.
 */
interface DatabaseCallback {

    fun onFetchingData(idiomMode : Int)
    fun onErrorFetchingData()
    fun onFetchedTranslatedData()
    fun onFetchedUntranslatedData()
    fun onFetchedBoth()
}