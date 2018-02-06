package com.shohiebsense.idiomaticsynonym.obsoletes

import android.content.Context
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.model.TranslatedIdiom
import com.shohiebsense.idiomaticsynonym.model.UntranslatedIdiom
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Shohiebsense on 19/10/2017.
 *
 *
 * DISCLAIMER BELUM DIBUAT THREADING
 */
class UnderlineService(var fetchedText: String, var translatedIdiom: MutableList<TranslatedIdiom>, var untranslatedIdiom: MutableList<UntranslatedIdiom>) {


    fun findText(context : Context, myObserver : Observer<SpannableString>){
        var dolor ="dolor"
        var startIndex = fetchedText.toLowerCase().indexOf(dolor)

        AppUtil.makeDebugLog("indexx ke "+startIndex)
        var endIndex = startIndex + dolor.length
        var decoratedSpan = SpannableString(fetchedText)
        var clickableSpan = object : ClickableSpan(){
            override fun onClick(p0: View?) {
                AppUtil.makeDebugLog("kok ga keluarr")
                Toast.makeText(context, "anuu "+dolor, Toast.LENGTH_LONG).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText =false
            }
        }
        val myObservable = Observable.create(ObservableOnSubscribe<SpannableString> { e->

            while(startIndex >=0 ){
                AppUtil.makeDebugLog("aaiawei "+startIndex)
                decoratedSpan.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.secondaryDarkColor)), 0, decoratedSpan.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                decoratedSpan.setSpan(StyleSpan(Typeface.BOLD),startIndex,startIndex+dolor.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                decoratedSpan.setSpan(object: ClickableSpan(){
                    override fun onClick(p0: View?) {
                        AppUtil.makeDebugLog("kok ga keluarr")
                        Toast.makeText(context, "anuu "+dolor, Toast.LENGTH_LONG).show()
                    }

                    override fun getUnderlying(): CharacterStyle {
                        return super.getUnderlying()
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                    }
                }, startIndex, startIndex+dolor.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                decoratedSpan.setSpan(clickableSpan, startIndex, startIndex + dolor.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                startIndex = fetchedText.toLowerCase().indexOf(dolor, startIndex + 1)
            }
            e.onNext(decoratedSpan)
            e.onComplete()
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())



        myObservable.subscribe(myObserver)
    }

    fun underlineByTranslatedIdiom(){

    }

    fun underlineTranslatedIdiom(){

    }


    /**
     *
     * used in underliningservice
     */

 /*   fun getSentencesObservable() : Observable<MutableList<String>> {

        return Observable.create( ObservableOnSubscribe<MutableList<String>>{
            e->
            var sentences = mutableListOf<String>()
            extractedPdfTexts.forEach { singleString ->
                sentences.add(AppUtil.newSpaceBetweenSentences(singleString))
            }
            e.onNext(sentences)
            e.onComplete()
        })
    }*/


   /* //cara1
    @Throws(Exception::class)
    fun testPDFBoxExtractImages() {
        val document = PDDocument.load(File("D:/Temp/Test.pdf"))
        val list = document.pages
        for (page in list) {
            val pdResources = page.resources
            for (c in pdResources.xObjectNames) {
                val o = pdResources.getXObject(c)
                if (o is PDImageXObject) {
                    val file = File("D:/Temp/" + System.nanoTime() + ".png")
                    //create file
                    //ImageIO.write(((PDImageXObject)o).getImage(), "png", file);
                }
            }
        }
        document.close()
    }*/



 /*   //method2 cara 2
    @Throws(IOException::class)
    fun getImagesFromPDF(document: PDDocument): List<Bitmap> {
        val images = ArrayList<Bitmap>()
        for (page in document.pages) {
            images.addAll(getImagesFromResources(page.resources))
        }

        return images
    }*/


    /* fun getTranslatedIdiomOneByOne(){
        if(fetchedPdfText.size == 0){
            AppUtil.makeDebugLog("THE FETCHED DAT CONTAINT IDIOM IS NULL")
            return
        }


        var observer = object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                //process startup
                underliningCallback.onTranslatingIdiomOneByOne()
            }

            override fun onComplete() {
                translatedIdiomText
                underliningCallback.onFinishedTranslatingIdiomOneByOne(combineStringMeaning)
            }

            override fun onError(e: Throwable) {
                AppUtil.makeErrorLog(e.toString())
                underliningCallback.onErrorTranslatingIdiomOneByOne()
            }

            override fun onNext(t: String) {
                AppUtil.makeDebugLog(t)
                if(t.contains(",")){
                    fetchedIdiomsMeaning.addAll(t.split("\\s*,\\s*") )
                }

                else
                    fetchedIdiomsMeaning.add(t)

                translatedIdiomText = t
            }

        }
        TranslateService().singleTranslate(context, observer, fetchedPdfText)
    }*/


    /*fun getUnderlineTheFetchedWithUntranslatedText(fetchedText: String, decoratedSpan: SpannableString, clickableSpan: ClickableSpan) {
        var untranslatedObserver = object : Consumer<UntranslatedIdiom> {
            override fun accept(t: UntranslatedIdiom?) {
                if (t != null) {
                    var index = BNDMCI().searchString(fetchedText, t?.idiom)
                    if(index != -1){
                        underliningIdiomProcess(index, decoratedSpan, t)
                        AppUtil.makeErrorLog( "nomorr "+index)
                    }
                }
            }

            *//* override fun onSubscribe(d: Disposable) {
                     underliningCallback.onFindingUntranslatedIdiom()

             }

             override fun onComplete() {
                 AppUtil.makeDebugLog("im finishedd untranslated")
                 underliningCallback.onFinishedUnderliningText(decoratedSpan)

             }

             override fun onError(e: Throwable) {
                 underliningCallback.onErrorUnderliningText(decoratedSpan)
             }

             override fun onNext(untranslatedIdiom: UntranslatedIdiom) {
                 var index = BNDMCI().searchString(extractedPdfTexts, untranslatedIdiom.idiom)
                 underliningIdiomProcess(index, decoratedSpan, untranslatedIdiom, clickableSpan)

                 if(index != -1){
                     AppUtil.makeErrorLog( "nomorr "+index)
                 }ww
             }*//*
        }
        TranslatedAndUntranslatedDataEmitter.untranslatedIdiomList.toObservable().subscribeOn(Schedulers.io()).filter{aduh -> BNDMCI().searchString(fetchedText, aduh.idiom)  != -1 }.observeOn(Schedulers.io()).subscribe(untranslatedObserver)

        //TranslatedAndUntranslatedDataEmitter.untranslatedIdiomList.toSingle().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(untranslatedObserver)

    }*/


    /*  fun getUnderlineFetchedTextUsingUntranslatedData(anusingle: String, decoratedSpan: SpannableStringBuilder, sentenceIndex: Int) {
          AppUtil.makeDebugLog("== UNTRANSLATEDFETCHED ==")
          val untranslatedObserver = object : Observer<UntranslatedIdiom> {
              override fun onSubscribe(d: Disposable) {
                  AppUtil.makeDebugLog("ONSTARTEDTRANSLATEDIDIOM  ")

              }

              override fun onError(e: Throwable) {
                  AppUtil.makeDebugLog("ONUNTRANSLATED GET ERROR  "+e.toString())

              }

              override fun onComplete() {
                  underliningCallback.onFinishedUntranslatedIdiom()
                  AppUtil.makeDebugLog("ONfinishedUntranslatedIdiom  ")
              }

              override fun onNext(t: UntranslatedIdiom) {
                  val index = BNDMCI().searchString(anusingle,t.idiom)
                  fetchedUntranslatedIdiomTexts.add(t.idiom)
                  AppUtil.makeErrorLog( "nomorr untranslated "+index)
                  underliningIdiomProcess(index, decoratedSpan, t,sentenceIndex, sent)
                  indexedSentences.add(IndexedSentence(t.idiom, sentenceIndex))
              }


          }
          TranslatedAndUntranslatedDataEmitter.untranslatedIdiomList.toObservable().subscribeOn(Schedulers.io()).filter{ aduh -> BNDMCI().searchString(anusingle, aduh.idiom)  != -1 }.observeOn(Schedulers.io()).subscribe(untranslatedObserver)

      }*/



    //beginning zip option

    //COBA LANGSUNG DI ONNEXT



/*private fun getObserver2(): Observer<String> {

    var sentenceIndex = 0
    var decoratedSpans : ArrayList<CharSequence> = arrayListOf(SpannableStringBuilder())

    return object : Observer<String> {
        override fun onSubscribe(d: Disposable) {
            AppUtil.makeDebugLog("SUBSCRIBEEEE MY GAY")
        }


        override fun onNext(sentence : String) {
            TranslatedAndUntranslatedDataEmitter.idiomsList.forEach{index ->
                AppUtil.makeDebugLog("iterate no. $sentenceIndex, and the idiom is $index")
                if( sentence.contains(index.idiom)){
                    decoratedSpans.add(underliningCombinedIdiomProcess(sentence.indexOf(index.idiom),SpannableStringBuilder(sentence),index,sentenceIndex,sentence  ))
                }
            }
            sentenceIndex++
        }

        override fun onError(e: Throwable) {
            AppUtil.makeDebugLog("error underlining :  "+e.toString())

        }

        override fun onComplete() {
            AppUtil.makeDebugLog("completed !!! "+decoratedSpans.size)
            underliningCallback.onFinishedUnderliningText(decoratedSpans)
        }
    }
}*/



/*fun findingTranslatedIdiom(translatedIdiomList: List<TranslatedIdiom>, sentence: String, decoratedSpan: SpannableStringBuilder, sentenceIndex: Int) : CharSequence{
    var idiomSpannableBuilder =  decoratedSpan
    for(i in 0 .. translatedIdiomList.size - 1){

        val translatedIdiom = translatedIdiomList[i]
        val index = BNDMCI().searchString(sentence, translatedIdiom.idiom)

        val spaceIndex = BNDMCI().searchString(sentence, " ")
        if(index != -1 && spaceIndex != -1){
            //fetchedPdfText.get(index)
            fetchedTranslatedIdiomTexts.add(translatedIdiom.meaning)
            idiomSpannableBuilder = underliningIdiomProcess(index, decoratedSpan, translatedIdiom,sentenceIndex,sentence)
            indexedSentences.add(IndexedSentence(sentence, sentenceIndex))
        }
    }
    return idiomSpannableBuilder
}

fun findingUntranslatedIdiom(untranslatedIdiomList : List<UntranslatedIdiom>, sentence : String, decoratedSpan: SpannableStringBuilder, sentenceIndex: Int) : CharSequence{
    var idiomSpannableBuilder  =  decoratedSpan
    for(i in 0 .. untranslatedIdiomList.size -1){
        val untranslatedIdiom = untranslatedIdiomList[i]
        val index = BNDMCI().searchString(sentence,untranslatedIdiom.idiom)
        val spaceIndex = BNDMCI().searchString(sentence, " ")

        if(index != -1 && spaceIndex != -1){
            AppUtil.makeDebugLog( "nomorr untranslated "+index)

            fetchedUntranslatedIdiomTexts.add(untranslatedIdiom.idiom)
            idiomSpannableBuilder = underliningIdiomProcess(index, decoratedSpan, untranslatedIdiom,sentenceIndex,sentence)
            indexedSentences.add(IndexedSentence(untranslatedIdiom.idiom, sentenceIndex))
        }

    }
    return idiomSpannableBuilder
}*/


    //end


/*fun getUnderlineZippingV2(){
    if(TranslatedAndUntranslatedDataEmitter.idiomsList.isEmpty() ){
        AppUtil.makeErrorLog("error size list 0")
        underliningCallback.onErrorUnderliningText()
        return
    }
    var sentenceIndex = 0
    var decoratedSpans : ArrayList<CharSequence> = arrayListOf(SpannableStringBuilder())


    Completable.create{
        subscriber ->
        englishSentences.forEachIndexed{index, sentence ->
            TranslatedAndUntranslatedDataEmitter.idiomsList.forEachIndexed { idiomIndex, idioms ->
                var decoratedSpans : ArrayList<CharSequence> = arrayListOf(SpannableStringBuilder())

                if(sentence.contains(idioms,true)){
                    val index = sentence.indexOf(idioms)
                    val singleSpan = underliningIdiomProcess(index, decoratedSpan, translatedIdiom,sentenceIndex,sentence)


                }
                val index = BNDMCI().searchString(value, s)

                val spaceIndex = BNDMCI().searchString(sentence, " ")
                if(index != -1 && spaceIndex != -1){
                    //fetchedPdfText.get(index)
                    fetchedTranslatedIdiomTexts.add(translatedIdiom.meaning)
                    indexedSentences.add(IndexedSentence(sentence, sentenceIndex))
                }
            }
        }
    }




}
*/

/*fun getUnderLineZipping(){
    AppUtil.makeDebugLog("extracted pdf size " +extractedPdfTexts.size )
    idiomList = HashMap()
    if(TranslatedAndUntranslatedDataEmitter.untranslatedIdiomList.isEmpty() || TranslatedAndUntranslatedDataEmitter.translatedIdiomList.isEmpty() ){
        AppUtil.makeErrorLog("error size list 0")
        underliningCallback.onErrorUnderliningText()
    }


    var sentenceIndex = 0
    var decoratedSpans : ArrayList<CharSequence> = arrayListOf(SpannableStringBuilder())

    Observable.zip(getTranslatedIdiomListObservable(),
            getUntranslatedIdiomListObservable(), BiFunction<ArrayList<TranslatedIdiom>, ArrayList<UntranslatedIdiom>, ArrayList<CharSequence>> {
        translatedIdiomList, untranslatedIdiomList ->

        englishSentences.forEach {
            sentence ->
            AppUtil.makeDebugLog("hiii "+sentenceIndex)
            val sentenceStringBuilder = SpannableStringBuilder("")
            val sentence = AppUtil.newSpaceBetweenSentences(sentence)
            val decoratedSpan =  SpannableStringBuilder(TextUtils.concat(sentenceStringBuilder, findingTranslatedIdiom(translatedIdiomList, sentence, SpannableStringBuilder(sentence) ,sentenceIndex)))
            val decoratedSpan2 = findingUntranslatedIdiom(untranslatedIdiomList, sentence, decoratedSpan, sentenceIndex)
            decoratedSpans.add(decoratedSpan2)
            sentenceIndex++
        }



        *//* englishSentences.forEach { singleString ->
                 val sentence = AppUtil.newSpaceBetweenSentences(singleString)
                 var decoratedSpan =  SpannableStringBuilder(TextUtils.concat(decoratedSpans, findingTranslatedIdiom(translatedIdiomList, sentence, SpannableStringBuilder(sentence) ,sentenceIndex)))
                 decoratedSpans = findingUntranslatedIdiom(untranslatedIdiomList, sentence, decoratedSpan, sentenceIndex)
                 sentenceIndex++
             }*//*

            sentenceIndex++
            decoratedSpans
            //FIND
        }) *//* .filter { it.isNotEmpty() }*//*.subscribeOn(Schedulers.computation()) // Be notified on the main thread
                .observeOn(Schedulers.io())
                .subscribe(getObserver())

    }*/


    //test lagi dan harus pakai index sentence
    /*END_OF_SENTENCE.split(extractedPdfString).forEachIndexed {  sentenceIndex, sentence -> //bisapakai maxent, trigger

        for(combinedIdiom in list){
        val lcword = combinedIdiom.idiom.toLowerCase()
            var index = sentence.toLowerCase().indexOf(lcword)
            if (index >= 0) {

                spannableStringBuilder.setSpan(StyleSpan(Typeface.BOLD), index, index+lcword.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                spannableStringBuilder.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.secondaryDarkColor)), index, index+lcword.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View?) {
                        if (combinedIdiom.meaning.isBlank()) {
                            getSingleTranslate(combinedIdiom.idiom, sentenceIndex, sentence)
                            return
                        }
                        underliningCallback.onClickedIdiomText(combinedIdiom.meaning, sentenceIndex, sentence)
                        //
                    }
                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                    }
                }
                spannableStringBuilder.setSpan(clickableSpan, index, index+lcword.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                //bookmarkDataEmitter.insertIndexedSentence(sentenceIndex, sentence, combinedIdiom.idiom)
                flagged = true
            }
        }
    }*/
}