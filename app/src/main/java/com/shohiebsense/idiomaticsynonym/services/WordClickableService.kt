package com.shohiebsense.idiomaticsynonym.services

import android.content.Context
import android.graphics.Color
import android.support.design.widget.BottomSheetBehavior
import android.view.View
import com.klinker.android.link_builder.Link
import com.shohiebsense.idiomaticsynonym.model.TranslatedIdiom
import com.shohiebsense.idiomaticsynonym.services.emitter.TranslatedAndUntranslatedDataEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.callbacks.SingleEntityCallback
import com.shohiebsense.idiomaticsynonym.view.callbacks.WordClickableCallback
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.regex.Pattern


/**
 * Created by Shohiebsense on 25/05/2018
 */

class WordClickableService(var context : Context, var wordClickableCallback: WordClickableCallback) {
    lateinit var translateService : TranslateService
    lateinit var currentIdiom : String

    init {
        AppUtil.makeErrorLog("attempt to initializee")
        if(AppUtil.checkInternetConnection(context)){
            //NETWORK ON MAIN THREAD EXCEPTION
            Single.create<Unit> {
                translateService = TranslateService(context)
                AppUtil.makeDebugLog("initialized hehehehe ")
            }.observeOn(Schedulers.newThread()).subscribeOn(Schedulers.newThread()).subscribe()
        }
    }
    var singleEntityCallback : SingleEntityCallback = object : SingleEntityCallback {
        override fun onFetched(translatedIdiom: TranslatedIdiom?) {
            if (translatedIdiom == null)
            {
                AppUtil.makeErrorLog("halooo ")
            }
            else{
                AppUtil.makeErrorLog("i got it "+translatedIdiom)
                //Network mainn thread. use observable
                //ehh udah sihh


            }
        }

        override fun onError() {
            //service
            getSingleTranslate(currentIdiom,0, "")
        }



    }

    fun generateClickableSpan(texts: String, idioms: String, behaviour: BottomSheetBehavior<View>)  {
        AppUtil.makeErrorLog("started to do")
        var idioms =  AppUtil.getListOfIdioms(idioms)
        val singleCombinedIdiom = HashSet<String>()
        val links = arrayListOf<Link>()
        for(foundedIdiom in idioms){
            if(texts.contains(foundedIdiom) && singleCombinedIdiom.add(foundedIdiom)) {
                var index = texts.indexOf(foundedIdiom,0,true)
                var prevIndex : Char
                if(index == 0){
                    continue
                }
                prevIndex = texts[index-1].toLowerCase()
                val afterLastIndex = texts[index+foundedIdiom.length].toLowerCase()
                var bool = prevIndex.isLetterOrDigit()
                var boolFinal = afterLastIndex.isLetter()
                if(index >= 0 && (!bool && !boolFinal)){
                    val link = Link(Pattern.compile("[\\s]"+ foundedIdiom+"[^a-z]", Pattern.CASE_INSENSITIVE))
                            .setTextColor(Color.parseColor("#00BCD4"))
                            .setUnderlined(false)
                            .setOnClickListener {
                                //supposed to be select single query
                                behaviour.state = BottomSheetBehavior.STATE_HIDDEN
                                currentIdiom = foundedIdiom
                                AppUtil.makeErrorLog("at least until here")
                                val emitter = TranslatedAndUntranslatedDataEmitter(context,singleEntityCallback)
                                emitter.getSingleTranslatedIdiom(foundedIdiom)

                            }

                    links.add(link)
                }


            }
        }

        AppUtil.makeErrorLog("not lasts until hereee boi?")
        wordClickableCallback.onCompleted(links)
    }

    fun getMeaningFromIdiom(idiom : String){
        currentIdiom = idiom
        val emitter = TranslatedAndUntranslatedDataEmitter(context,singleEntityCallback)
        emitter.getSingleTranslatedIdiom(idiom)
    }

    fun getSingleTranslate(idiom: String, sentenceIndex: Int, sentence: String){

        val observer = object : Observer<String> {
            var combineStringMeaning = mutableListOf<String>()
            override fun onSubscribe(d: Disposable) {
                //process startup
            }

            override fun onComplete() {
                wordClickableCallback.onShowingOnlineTranslation(combineStringMeaning)
            }

            override fun onError(e: Throwable) {
                AppUtil.makeErrorLog("e singleTranslating "+ e.toString())
            }

            override fun onNext(t: String) {
                combineStringMeaning.add(t)
            }
        }
        val combineStrings = mutableListOf<String>()
        if(idiom.contains(",")){
            combineStrings.addAll(idiom.split("\\s*,\\s*") )
        }
        else{
            combineStrings.add(idiom)
        }
        translateService.singleTranslate(observer, combineStrings)
    }

}


