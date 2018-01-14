package com.shohiebsense.idiomaticsynonym.view.fragment.callbacks

import java.io.File

/**
 * Created by Shohiebsense on 18/10/2017.
 */
interface PdfDisplayCallback {

    fun onLoadingPdf()
    fun onErrorLoadingPdf()
    fun onFinishedLoadingPdf(file : File)
    fun onFetchingPdf()
    fun onFinishedFetchingPdf(fetchedText: MutableList<String>, name: String)




}