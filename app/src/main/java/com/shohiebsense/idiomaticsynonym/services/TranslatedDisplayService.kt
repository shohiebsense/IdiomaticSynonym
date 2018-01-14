package com.shohiebsense.idiomaticsynonym.services

import android.content.Context
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import com.eaio.stringsearch.BNDMCI
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.fragment.callbacks.TranslatedDisplayCallback
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable

/**
 * Created by Shohiebsense on 11/12/2017.
 */
//class TranslatedDisplayService(val context: Context, val translatedTextList: ArrayList<String>, val idiomList: HashMap<Int, String>, val callback: TranslatedDisplayCallback)  {
class TranslatedDisplayService(val context: Context, val translatedTextList: ArrayList<String>, val idiomList: MutableList<String>,
                               val indices : ArrayList<Int>,val callback: TranslatedDisplayCallback)  {




    fun extract(){

        var bndmci = BNDMCI()
        var spannableStrings : CharSequence = ""
        AppUtil.makeDebugLog("STILL THE SIZEE IS "+idiomList)

        var observer = object : Observer<SpannableString> {
            override fun onComplete() {
                AppUtil.makeDebugLog("shohiebdoang "+spannableStrings)
                callback.onFinishExtractText(spannableStrings)
            }


            override fun onNext(t: SpannableString) {
                spannableStrings = TextUtils.concat(spannableStrings, t)
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onError(e: Throwable) {

            }

        }

        var index = 0
       translatedTextList.toObservable().subscribeOn(AndroidSchedulers.mainThread()).subscribe{
           sentence ->

           val decoratedSpan = SpannableString(sentence.replace("&#39;".toRegex(),"\"")
                   .replace("&amp;".toRegex(),"&").replace("&lt;".toRegex(),"<")
                   .replace("&gt;".toRegex(),">").replace("&quot;","\"")
                   .replace("&apos;","\'"))



           if(indices.contains(index)){
                decoratedSpan.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.secondaryDarkColor)), 0, sentence.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
           }

           for(idioms in idiomList){
               AppUtil.makeDebugLog("spannablestrings spans  "+idioms)

               if(sentence.contains(idioms,true)){
                   var index = bndmci.searchString(sentence, idioms)
                   var spaceIndex = BNDMCI().searchString(sentence, " ")
                   if(index != -1 && spaceIndex != -1){
                        makeStyleSpan(sentence, decoratedSpan, idioms, index)

                   }

               }

           }
           //finish
           AppUtil.makeDebugLog("insidee "+decoratedSpan)
           observer.onNext(decoratedSpan)
           observer.onComplete()
       }


    }


    fun makeStyleSpan(sentence : String, decoratedSpan : SpannableString, idiom : String, index : Int){
        if(index+idiom.length > sentence.length){
            return
        }

        AppUtil.makeDebugLog("stringgss " + sentence.substring(index, index+idiom.length))
        //pecah komanya jadi dua.

        if(idiom.contains(",")){
            val regex = ","
            var idiomList = idiom.split(regex).toMutableList()

            for(meanings in idiomList){
                decoratedSpan.setSpan(StyleSpan(Typeface.BOLD),index, index+idiom.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                var clickableSpan = object : ClickableSpan()
                {
                    override fun onClick(widget: View?) {

                        callback.onSingleSynonymIdiomClicked(idiom,index)
                        //
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                    }

                }
                decoratedSpan.setSpan(clickableSpan, index, index + idiom.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            }
            // selectedIdiomList!!.put(index,idiomList.first())
        }
        else{
            AppUtil.makeDebugLog("clicked, the idiom is  "+idiom)
            decoratedSpan.setSpan(StyleSpan(Typeface.BOLD),index, index+idiom.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            var clickableSpan = object : ClickableSpan()
            {
                override fun onClick(widget: View?) {

                    callback.onSingleSynonymIdiomClicked(idiom,index)
                    //
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }

            }
            decoratedSpan.setSpan(clickableSpan, index, index + idiom.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        }


    }



}