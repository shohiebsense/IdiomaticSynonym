package com.shohiebsense.idiomaticsynonym.view.callbacks

/**
 * Created by Shohiebsense on 18/10/2017.
 */
interface PdfDisplayCallback {

    fun onErrorReadingFile()
    fun onLoadingPdf()
    fun onErrorLoadingPdf()
    fun onFinishedLoadingPdf(file: String)
    fun onFetchingPdf()
    fun onFinishedFetchingPdfAsList(fetchedText: MutableList<String>, name: String)
    fun onFinishedFetchingPdf(fetchedText: String, name: String)



}