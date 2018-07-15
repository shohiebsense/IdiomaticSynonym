package com.shohiebsense.idiomaticsynonym.services

import android.content.pm.ApplicationInfo
import com.shohiebsense.idiomaticsynonym.TranslatedDisplayActivity
import com.shohiebsense.idiomaticsynonym.model.ReplacedSentence
import com.shohiebsense.idiomaticsynonym.services.emitter.ReplacedHistoryEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil


/**
 * Created by Shohiebsense on 11/07/2018
 */

class ReplaceService(val activity: TranslatedDisplayActivity, val listener : ReplaceService.ReplaceListener) : ReplacedHistoryEmitter.ReplacedHistoryCallback {


    lateinit var newIdiom: String
    lateinit var existingIdiom: String
    lateinit var originIdiom : String
    var replacedHistoryEmitter = ReplacedHistoryEmitter(activity,this)


    fun isIdiomTranslationExist(indonesianTranslation : String) {
        val replacedSentences = getSentence(indonesianTranslation,existingIdiom)
        if(replacedSentences.isNotEmpty()){
            listener.onReplacedSentencesExist(getSentence(indonesianTranslation,existingIdiom))
        }
        else{
            listener.onEmpty()
        }
    }

    fun replaceIdiomInSentence(indonesianTranslation: String, replacedSentence: ReplacedSentence, newIdiom: String){
        var oldIdiom = replacedSentence.sentence.substring(replacedSentence.index,replacedSentence.endIndex)
        replacedHistoryEmitter.isIdiomExists(activity.bookmark.id,oldIdiom)
        AppUtil.makeErrorLog("what is it +"+ replacedSentence.sentence.substring(replacedSentence.index,replacedSentence.endIndex))
        var newSentence = replacedSentence.sentence.replace(oldIdiom,newIdiom)
        var newTranslation = indonesianTranslation.replace(replacedSentence.sentence,newSentence)
        listener.onGettingNewSentence(newSentence)
        listener.onAttemptToReplace(newTranslation)
    }

    fun getSentenceIndexInTranslation(transltion: String, sentence : String) : Int{
        return transltion.indexOf(sentence)
    }

    fun getIndexIdiomInSentence(sentence: String, idiom : String) : Int{
        return sentence.indexOf(idiom)
    }


    fun getSentence(text: String, word: String):  ArrayList<ReplacedSentence> {
        var index: Int
        var endIndex : Int
        var foundedSentences = arrayListOf<ReplacedSentence>()
        val lcword = word.toLowerCase()
        for (sentence in AppUtil.END_OF_SENTENCE.split(text)) {
            if (sentence.toLowerCase().contains(lcword)) {
                //get index
                index = sentence.toLowerCase().indexOf(lcword.toLowerCase())
                endIndex = index + lcword.length
                AppUtil.makeErrorLog("indexx "+index+"  endindex"+endIndex+" length "+sentence.length)
                var sentenceIndex = text.indexOf(sentence)
                var sentenceEndIndex = sentenceIndex + text.length
                foundedSentences.add(ReplacedSentence(sentence,index,endIndex,sentenceIndex,sentenceEndIndex))
            }
        }
        return foundedSentences
    }


    //ReplacedTranslation
    override fun onIdiomNotExists() {
        //insert
        AppUtil.makeErrorLog("set originial translation  "+originIdiom+ "   "+existingIdiom+ "   "+newIdiom)
        replacedHistoryEmitter.setOriginalTranslation(activity.bookmark.id,originIdiom,existingIdiom)
        replacedHistoryEmitter.setReplacedTranslation(activity.bookmark.id,originIdiom,newIdiom)
    }

    override fun onIdiomExists() {
        replacedHistoryEmitter.setReplacedTranslation(activity.bookmark.id,originIdiom,newIdiom)
    }

    override fun onGettingOriginalTranslation(translation: String) {
        listener.onGettingOriginalTranslation(translation)
    }

    override fun onGettingReplacedTranslation(translation: String) {
        listener.onGettingReplacedTranslation(translation)
    }
    //end section

    interface ReplaceListener{
        fun onReplacedSentencesExist(foundedSentences: ArrayList<ReplacedSentence>)
        fun onAttemptToReplace(translation : String)
        fun onGettingNewSentence(newSentence : String)
        fun onGettingOriginalTranslation(translation: String)
        fun onGettingReplacedTranslation(translation: String)
        fun onEmpty()
    }
}