package com.shohiebsense.straightidiomalearn.model.api

import com.google.gson.annotations.SerializedName

/**
 * Created by Shohiebsense on 27/09/2017.
 */
 class Synonym(
        @SerializedName("name")
        val name : String,
        val synonymwords : List<SynonymWord>

) {





}