package com.shohiebsense.idiomaticsynonym.services

import com.shohiebsense.idiomaticsynonym.view.activity.detail.DetailActivity
import com.shohiebsense.idiomaticsynonym.model.ReplaceHistory
import com.shohiebsense.idiomaticsynonym.model.ReplacedSentence
import com.shohiebsense.idiomaticsynonym.services.emitter.ReplacedHistoryEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil


/**
 * Created by Shohiebsense on 11/07/2018
 */

class ReplaceService(val activity: DetailActivity, val listener : ReplaceService.ReplaceListener) : ReplacedHistoryEmitter.ReplacedHistoryCallback {


    lateinit var newIdiom: String
    lateinit var existingIdiom: String
    lateinit var originIdiom : String
    var idiomIndex  = -1
    var idiomEndIndex = -1
    var sentenceOrderInText = -1
    var replacedHistoryEmitter = ReplacedHistoryEmitter(activity,this)


    fun isIdiomTranslationExist(indonesianTranslation : String) {
        val replacedSentences = getSentence(indonesianTranslation,existingIdiom)
        if(replacedSentences.isNotEmpty()){
            listener.onReplacedSentencesExist(replacedSentences)
        }
        else{
            listener.onReplacedSentenceEmpty()
        }
    }

    fun replaceIdiomInSentence(indonesianTranslation: String, replacedSentence: ReplacedSentence, newIdiom: String){
        sentenceOrderInText = replacedSentence.sentenceOrderInText
        var oldIdiom = replacedSentence.sentence.substring(replacedSentence.index,replacedSentence.endIndex)
        var newSentence = replacedSentence.sentence.replace(oldIdiom,newIdiom)
        var newTranslation = indonesianTranslation.replace(replacedSentence.sentence,newSentence)
        idiomIndex = newSentence.indexOf(newIdiom)
        idiomEndIndex = idiomIndex + newIdiom.length
        replacedHistoryEmitter.isIdiomExists(activity.bookmark.id,originIdiom)
        listener.onGettingNewSentence(newSentence)
        listener.onAttemptToReplace(newTranslation)
    }

    fun getSentenceIndexInTranslation(transltion: String, sentence : String) : Int{
        return transltion.indexOf(sentence)
    }

    fun getIndexIdiomInSentence(sentence: String, idiom : String) : Int{
        return sentence.indexOf(idiom)
    }


    fun getSentence(text: String, translationIdiomWord: String):  ArrayList<ReplacedSentence> {
        var index: Int
        var endIndex : Int
        var foundedSentences = arrayListOf<ReplacedSentence>()
        val lcword = translationIdiomWord.toLowerCase()
        var sentences = AppUtil.END_OF_SENTENCE.split(text)
        for (i in 0 .. sentences.lastIndex) {
            if (sentences[i].toLowerCase().contains(lcword)) {
                //get index
                index = sentences[i].toLowerCase().indexOf(lcword.toLowerCase())
                endIndex = index + lcword.length
                var sentenceIndex = text.indexOf(sentences[i])
                var sentenceEndIndex = sentenceIndex + text.length
                foundedSentences.add(ReplacedSentence(sentences[i],index,endIndex,sentenceIndex,sentenceEndIndex,i))
            }
        }
        return foundedSentences
    }

    fun getTranslatedBasedInOrder(text : String, order : Int) : String {
        return AppUtil.END_OF_SENTENCE.split(text)[order]
    }


    //ReplacedTranslation
    override fun onIdiomNotExists() {
        //insert
        AppUtil.makeErrorLog("onIdiomNotExists"+originIdiom+ "   "+existingIdiom+ "   "+newIdiom)
        replacedHistoryEmitter.insertOriginalTranslation(activity.bookmark.id,originIdiom,existingIdiom)
        replacedHistoryEmitter.setReplacedTranslation(activity.bookmark.id,originIdiom,newIdiom)
        replacedHistoryEmitter.setIndexes(activity.bookmark.id,originIdiom,idiomIndex,idiomEndIndex)
        replacedHistoryEmitter.setSentenceOrder(activity.bookmark.id,originIdiom,sentenceOrderInText)
    }

    override fun onIdiomExists() {
        replacedHistoryEmitter.setReplacedTranslation(activity.bookmark.id,originIdiom,newIdiom)
        replacedHistoryEmitter.setOriginalTranslation(activity.bookmark.id,originIdiom,existingIdiom)
        replacedHistoryEmitter.setIndexes(activity.bookmark.id,originIdiom,idiomIndex,idiomEndIndex)
        replacedHistoryEmitter.setSentenceOrder(activity.bookmark.id,originIdiom,sentenceOrderInText)
    }

    override fun onGettingOriginalTranslation(translation: ReplaceHistory) {
        listener.onGettingOriginalTranslation(translation)
    }

    override fun onGettingReplacedTranslation(translation: ReplaceHistory) {
        listener.onGettingReplacedTranslation(translation)
    }

    override fun onNotFoundInTheSentence() {
        listener.onNotFoundInSentence()
    }

    //end section

    interface ReplaceListener{
        fun onReplacedSentencesExist(foundedSentences: ArrayList<ReplacedSentence>)
        fun onAttemptToReplace(translation : String)
        fun onGettingNewSentence(newSentence : String)
        fun onGettingOriginalTranslation(translation: ReplaceHistory)
        fun onGettingReplacedTranslation(translation: ReplaceHistory)
        fun onReplacedSentenceEmpty()
        fun onNotFoundInSentence()
    }
}