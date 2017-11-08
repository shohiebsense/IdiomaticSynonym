package com.shohiebsense.straightidiomalearn.model.api

import com.google.gson.annotations.SerializedName

/**
 * Created by Shohiebsense on 27/09/2017.
 */

class SynonymWord (
    @SerializedName("root_phrase")
    val rootPhrase : String,
    @SerializedName("related_phrase")
    val relatedPhrase : String,
    @SerializedName("rel_type")
    val relType : String,
    @SerializedName("rel_type_name")
    val relTypeName : String,
    @SerializedName("lex_class")
    val lexClass : String
)
{

}