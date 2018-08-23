package com.shohiebsense.idiomaticsynonym.services

import android.content.Context
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.ContextCompat
import android.view.View
import com.klinker.android.link_builder.Link
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.model.TranslatedIdiom
import com.shohiebsense.idiomaticsynonym.services.emitter.TranslatedAndUntranslatedDataEmitter
import com.shohiebsense.idiomaticsynonym.services.yandex.YandexTranslationService
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.callbacks.SingleEntityCallback
import com.shohiebsense.idiomaticsynonym.view.callbacks.WordClickableCallback
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.regex.Pattern


/**
 * Created by Shohiebsense on 25/05/2018
 */

class WordClickableService(var context : Context, var wordClickableCallback: WordClickableCallback) : YandexTranslationService.YandexListener {
    override fun onGetTranslation(text: String) {

    }



    var translateService : YandexTranslationService? = YandexTranslationService(context,this)
    lateinit var currentIdiom : String
    var isReady = true


    var singleEntityCallback : SingleEntityCallback = object : SingleEntityCallback {
        override fun onFetched(translatedIdiom: TranslatedIdiom?) {
            if (translatedIdiom == null)
            {
                AppUtil.makeErrorLog("halooo ")
            }
            else{
                val combineStrings = mutableListOf<String>()
                if(translatedIdiom.meaning.contains(",")){
                    combineStrings.addAll(translatedIdiom.meaning.split(","))
                }
                else{
                    combineStrings.add(translatedIdiom.meaning)
                }
                AppUtil.makeDebugLog("hoii "+combineStrings.size)
                wordClickableCallback.onShowingTranslation(combineStrings)

            }
        }

        override fun onError() {
            //service
            getSingleTranslate()
        }



    }

    fun generateClickableSpan(texts: String, idioms: String, behaviour: BottomSheetBehavior<View>)  {
        AppUtil.makeErrorLog("started to do")
        var idiomsList =  AppUtil.getListOfIdioms(idioms)
        val singleCombinedIdiom = HashSet<String>()
        val links = arrayListOf<Link>()
        for(foundedIdiom in idiomsList){
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
                            .setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                            .setTextColorOfHighlightedLink(ContextCompat.getColor(context, R.color.colorPrimary))
                            .setUnderlined(false)
                            .setOnClickListener {
                                //supposed to be select single query
                                wordClickableCallback.onClickedIdiomText(foundedIdiom)
                                behaviour.state = BottomSheetBehavior.STATE_HIDDEN
                                currentIdiom = foundedIdiom
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

    fun getSingleTranslate(){
        val observer = object : Observer<String> {
            var combineStringMeaning = mutableListOf<String>()
            override fun onSubscribe(d: Disposable) {
                //process startup
            }

            override fun onComplete() {
                AppUtil.makeErrorLog("combine finished "+combineStringMeaning[0])
                wordClickableCallback.onShowingTranslation(combineStringMeaning)
            }

            override fun onError(e: Throwable) {
                AppUtil.makeErrorLog("e singleTranslating "+ e.toString())
            }

            override fun onNext(t: String) {
                AppUtil.makeErrorLog("hoi")
                combineStringMeaning.add(t)
                wordClickableCallback.onShowingTranslation(combineStringMeaning)
            }
        }
        val combineStrings = mutableListOf<String>()
        if(currentIdiom.contains(",")){
            combineStrings.addAll(currentIdiom.split("\\s*,\\s*") )
        }
        else{
            combineStrings.add(currentIdiom)
        }
        if(translateService == null) {
            //
            return
        }
        if(isReady)
        translateService?.getSingleTranslate(observer, combineStrings)
        else{
            wordClickableCallback.onErrorShowing()
        }
    }

    fun getSingleTranslate(idiom : String){
        val observer = object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                //process startup
            }

            override fun onComplete() {
            }

            override fun onError(e: Throwable) {
                AppUtil.makeErrorLog("e singleTranslating "+ e.toString())
            }

            override fun onNext(t: String) {
                AppUtil.makeErrorLog("hoi")
                wordClickableCallback.onShowingIdiomOnlineTranslation(t)
            }
        }
        if(isReady)
            translateService?.getIdiomTranslate(observer, idiom)
        else{
            wordClickableCallback.onErrorShowing()
        }
    }

    fun getIdiomTranslate(idiom: String){
        val observer = object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                //process startup
            }

            override fun onComplete() {
            }

            override fun onError(e: Throwable) {
                AppUtil.makeErrorLog("e singleTranslating "+ e.toString())
            }

            override fun onNext(t: String) {
                wordClickableCallback.onShowingIdiomOnlineTranslation(t)
            }
        }
        if(isReady)
            translateService?.getIdiomTranslate(observer, idiom)
        else{
            wordClickableCallback.onErrorShowing()
        }
    }

    override fun onErrorConnection() {
        wordClickableCallback.onErrorShowing()
    }

}


