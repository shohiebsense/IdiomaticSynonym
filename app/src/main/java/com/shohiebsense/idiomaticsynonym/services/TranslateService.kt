package com.shohiebsense.idiomaticsynonym.services

/*import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions*/
/*import com.google.cloud.translate.Translate.TranslateOption
import com.google.common.collect.ImmutableList*/
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.google.common.collect.ImmutableList
import com.klinker.android.link_builder.Link
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers


/**
 * Created by Shohiebsense on 17/09/2017.
 */

class TranslateService(val context : Context) {

    lateinit var model : Translate.TranslateOption

    var options = TranslateOptions.newBuilder()
    .setApiKey(context.getString(R.string.API_TRANSLATE_KEY))
    .build()
    var  translateService = options.service


    init {

        init()
    }

    fun anu() {
        translateService
    }
    //has not been initialized
    fun init(){
        Observable.create<TranslateOptions> {
            options = TranslateOptions.newBuilder()
                    .setApiKey(context.getString(R.string.API_TRANSLATE_KEY))
                    .build()
            translateService = options.service
            model = Translate.TranslateOption.model("nmt")
        }.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe()
        AppUtil.makeDebugLog("translateService initialized")
    }


     /*fun translate(observer : Observer<SpannableStringBuilder>, mutableListArray: List<String>, indices : ArrayList<Int>)  {
         //commented due to development, uncomment again.

         Observable.create<SpannableStringBuilder> { observerr ->

             options = TranslateOptions.newBuilder()
                     .setApiKey(context.getString(R.string.API_TRANSLATE_KEY))
                     .build()
             translateService = options.service

             mutableListArray.forEachIndexed { index, it ->

                 val language = translateService.detect(it).language.toLowerCase()

                 val detections = translateService.detect(ImmutableList.of(it))
                 AppUtil.makeDebugLog("Language(s) detected:")
                 for (detection in detections) {
                     AppUtil.makeDebugLog(detection.toString())
                 }
                 AppUtil.makeDebugLog("translatt: " + language)
                 if (language.equals("en")) {
                     AppUtil.makeDebugLog("before translation " + it)
                     var translation = translateService.translate(it,
                             Translate.TranslateOption.targetLanguage("id"), model)

                     if (translation != null) {
                         spannableStringBuilder = SpannableStringBuilder(translation.translatedText)

                         if (flagged) {
                             spannableStringBuilder.setSpan(StyleSpan(Typeface.BOLD), 0, translation.translatedText.lastIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                         }

                         return spannableStringBuilder

                     }
                 }
             }
                     //AppUtil.makeDebugLog("hasil translasi : \n" + translation.translatedText)
         }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(observer)
     }*/

    fun translate(it: Link, flagged: Boolean) : SpannableStringBuilder? {
        //commented due to development, uncomment again.

        val language = translateService.detect(it.toString()).language.toLowerCase()
        val detections = translateService.detect(ImmutableList.of(it.toString()))
        AppUtil.makeDebugLog("Language(s) detected:")
        for (detection in detections) {
            AppUtil.makeDebugLog(detection.toString())
        }
        AppUtil.makeDebugLog("translatt: " + language)
        var spannableStringBuilder = SpannableStringBuilder()
        if (language.equals("en")) {
            AppUtil.makeDebugLog("before translation " + it)
            var translation = translateService.translate(it.toString(),
                    Translate.TranslateOption.targetLanguage("id"), model)

            if(translation != null){
                spannableStringBuilder = SpannableStringBuilder(translation.translatedText)

                if(flagged){
                    spannableStringBuilder.setSpan(StyleSpan(Typeface.BOLD), 0, translation.translatedText.lastIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                }
                return spannableStringBuilder

            }
            //AppUtil.makeDebugLog("hasil translasi : \n" + translation.translatedText)
        }
        return spannableStringBuilder
    }

    fun singleTranslate(observer: Observer<String>, text: MutableList<String>){
        AppUtil.makeDebugLog("BEGINNING THE SINGLE SENTENCE TRANSLATION")
        init()
        text.toObservable().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe{
            val language = translateService.detect(it).language.toLowerCase()

            val detections = translateService.detect(ImmutableList.of(it))
            AppUtil.makeDebugLog("Language(s) detected:")
            for (detection in detections) {
                AppUtil.makeDebugLog(detection.toString())
            }
            AppUtil.makeDebugLog("translatt: "+language)
            if(language.equals("en")){
                AppUtil.makeDebugLog("before single translation "+it)
                var translation  = translateService.translate(it,
                        Translate.TranslateOption.targetLanguage("id"),model)


                AppUtil.makeDebugLog("hasil single translasi : \n" + translation.translatedText)

                observer.onNext(translation.translatedText)
            }
            else{
                observer.onError(Throwable("its not english"))
            }
            observer.onComplete()
        }

    }
}





