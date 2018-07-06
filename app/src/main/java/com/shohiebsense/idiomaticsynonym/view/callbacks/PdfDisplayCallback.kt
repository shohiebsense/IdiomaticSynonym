package com.shohiebsense.idiomaticsynonym.view.callbacks

/**
 * Created by Shohiebsense on 18/10/2017.
 */
interface PdfDisplayCallback {

    fun onErrorReadingFile()
    fun onLoadingPdf()
    fun onError()
    fun onFinishedLoadingPdf()
    fun onProcess()
    fun onFinishedFetchingPdfAsList(fetchedText: MutableList<String>, name: String)
    fun onEmitted(fetchedText: String, name: String)



}