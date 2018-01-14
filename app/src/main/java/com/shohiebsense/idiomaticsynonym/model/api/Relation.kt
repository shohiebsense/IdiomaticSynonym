package com.shohiebsense.idiomaticsynonym.model.api

import com.google.gson.annotations.SerializedName

/**
 * Created by Shohiebsense on 27/09/2017.
 */

class Relation (
        @SerializedName("relation_direct")
        val relationDirect : Int,
        @SerializedName("relation_reverse")
        val relationReverse : Int,
        @SerializedName("s")
        var synonym : Synonym
)
{




}