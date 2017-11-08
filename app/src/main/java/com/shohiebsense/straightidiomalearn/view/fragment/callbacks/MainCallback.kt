package com.shohiebsense.straightidiomalearn.view.fragment.callbacks

import com.shohiebsense.straightidiomalearn.model.TranslatedIdiom
import io.reactivex.disposables.Disposable

/**
 * Created by Shohiebsense on 16/10/2017.
 */
interface MainCallback {
    fun onFetchingSuccess(disposable : Disposable)
    fun onFetchProcess(translatedIdiom: TranslatedIdiom)
    fun onFetchingFailed(throwable : Throwable)
    fun pnFetchingCompleted()
}