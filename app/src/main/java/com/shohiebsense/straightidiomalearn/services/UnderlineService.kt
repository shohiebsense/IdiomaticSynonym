package com.shohiebsense.straightidiomalearn.services

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
import com.shohiebsense.straightidiomalearn.R
import com.shohiebsense.straightidiomalearn.model.TranslatedIdiom
import com.shohiebsense.straightidiomalearn.model.UntranslatedIdiom
import com.shohiebsense.straightidiomalearn.utils.AppUtil
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


}