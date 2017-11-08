package com.shohiebsense.straightidiomalearn.services

import android.content.Context

import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.shohiebsense.straightidiomalearn.R
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.google.cloud.translate.Translate.TranslateOption
import com.google.common.collect.ImmutableList
import io.reactivex.rxkotlin.toObservable


/**
 * Created by Shohiebsense on 17/09/2017.
 */

open class TranslateService {


    internal fun init(context: Context, observer : Observer<String>, text : String) {


        //dah jadi
        var mutableListArray = AppUtil.splitSentencesToWords(text)

        //commented due to development, uncomment again.
       /* mutableListArray.toObservable().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe{
            val options = TranslateOptions.newBuilder()
                    .setApiKey(context.getString(R.string.API_TRANSLATE_KEY))
                    .build()
            // Use translate `model` parameter with `base` and `nmt` options.
            val model = TranslateOption.model("nmt")

            val translate = options.service
            val language = translate.detect(it).language.toLowerCase()

            val detections = translate.detect(ImmutableList.of(it))
            AppUtil.makeDebugLog("Language(s) detected:")
            for (detection in detections) {
                AppUtil.makeDebugLog(detection.toString())
            }
            AppUtil.makeDebugLog("translatt: "+language)
            if(language.equals("en")){
                var translation  = translate.translate(it,
                        Translate.TranslateOption.targetLanguage("id"),model)

                observer.onNext(translation.translatedText)
                observer.onComplete()
            }
            else{
                observer.onError(Throwable("its not english"))
            }

        }*/




        //COMMENTED DUE TO HANDLING EXCEED TRANSLATION RATE ERROR.
        /*Observable.create<String> {

            val options = TranslateOptions.newBuilder()
                    .setApiKey(context.getString(R.string.API_TRANSLATE_KEY))
                    .build()
            // Use translate `model` parameter with `base` and `nmt` options.
            val model = TranslateOption.model("nmt")

            val translate = options.service
            val language = translate.detect(text).language.toLowerCase()

            val detections = translate.detect(ImmutableList.of(text))
            AppUtil.makeDebugLog("Language(s) detected:")
            for (detection in detections) {
                AppUtil.makeDebugLog(detection.toString())
            }
            AppUtil.makeDebugLog("translatt: "+language)
            if(language.equals("en")){
                var translation  = translate.translate(text,
                        Translate.TranslateOption.targetLanguage("id"),model)

                observer.onNext(translation.translatedText)
                observer.onComplete()
            }
            else{
                observer.onError(Throwable("its not english"))
            }

            // Translate translate = TranslateOptions.getDefaultInstance(). getService();

        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (observer)*/



    }
}





