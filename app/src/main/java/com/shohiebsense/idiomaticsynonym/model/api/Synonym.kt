package com.shohiebsense.idiomaticsynonym.model.api

import com.google.gson.annotations.SerializedName

/**
 * Created by Shohiebsense on 27/09/2017.
 */
 class Synonym(
        val synonymwords : MutableList<SynonymWord>,
        @SerializedName("name")
        val name : String


) {





}