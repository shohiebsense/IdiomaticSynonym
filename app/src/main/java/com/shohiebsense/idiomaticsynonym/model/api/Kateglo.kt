package com.shohiebsense.idiomaticsynonym.model.api

import com.google.gson.annotations.SerializedName

/**
 * Created by Shohiebsense on 25/09/2017.
 */

data class Kateglo(
        @SerializedName("id")
        val id : String,
        @SerializedName("phrase")
        val phrase : String,
        @SerializedName("phrase_type")
        val phraseType : String,
        @SerializedName("lex_class")
        val lexClass : String,
        @SerializedName("roget_class")
        val rogetClass : Any,
        @SerializedName("pronounciation")
        val pronounciation : Any,
        @SerializedName("translations")
        val translations : List<Translation>,
        @SerializedName("relation")
        val relation : Relation, // penting
        @SerializedName("lex_class_name")
        val lexClassName : String){

}


