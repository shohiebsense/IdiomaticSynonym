package com.shohiebsense.idiomaticsynonym.services

/*import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions*/
/*import com.google.cloud.translate.Translate.TranslateOption
import com.google.common.collect.ImmutableList*/
import android.content.Context
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.google.common.collect.ImmutableList
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

    lateinit var options : TranslateOptions
    lateinit var translateService : Translate
    lateinit var model : Translate.TranslateOption

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


     fun translate(observer : Observer<String>, mutableListArray : MutableList<String>) {
        //commented due to development, uncomment again.

         Observable.create<String> {
             observerr ->

             options = TranslateOptions.newBuilder()
                     .setApiKey(context.getString(R.string.API_TRANSLATE_KEY))
                     .build()
             translateService = options.service

             mutableListArray.forEach{
                 val language = translateService.detect(it).language.toLowerCase()

                 val detections = translateService.detect(ImmutableList.of(it))
                 AppUtil.makeDebugLog("Language(s) detected:")
                 for (detection in detections) {
                     AppUtil.makeDebugLog(detection.toString())
                 }
                 AppUtil.makeDebugLog("translatt: "+language)
                 if(language.equals("en")){
                     AppUtil.makeDebugLog("before translation "+it)
                     var translation  = translateService.translate(it,
                             Translate.TranslateOption.targetLanguage("id"),model)


                     //AppUtil.makeDebugLog("hasil translasi : \n" + translation.translatedText)
                     observerr.onNext(translation.translatedText)
                 }
                 else{
                     observerr.onError(Throwable("its not english"))
                 }
                 observerr.onComplete()
             }


         }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(observer)






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

                observer.onNext(translation.translatedTextList)
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





