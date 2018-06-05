package com.shohiebsense.idiomaticsynonym.view.callbacks

import com.klinker.android.link_builder.Link


/**
 * Created by Shohiebsense on 26/05/2018
 */

interface WordClickableCallback {
    fun onCompleted(links : ArrayList<Link>)
    fun onClickedIdiomText(idiom : String)
    fun onShowingOnlineTranslation(meanings: MutableList<String>)
}