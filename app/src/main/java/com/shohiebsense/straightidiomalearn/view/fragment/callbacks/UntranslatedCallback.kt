package com.shohiebsense.straightidiomalearn.view.fragment.callbacks

import com.shohiebsense.straightidiomalearn.model.UntranslatedIdiom
import io.reactivex.disposables.Disposable

/**
 * Created by Shohiebsense on 16/10/2017.
 */
interface UntranslatedCallback {

    fun onFetchingSuccess(disposable : Disposable)
    fun onFetchProcess(untranslatedIdiom: UntranslatedIdiom)
    fun onFetchingFailed(throwable : Throwable)
    fun pnFetchingCompleted()
}