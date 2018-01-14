package com.shohiebsense.idiomaticsynonym.model.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Shohiebsense on 27/09/2017.
 */

class Thesaurus(
        @SerializedName("kateglo")
        @Expose
        val kateglo : Kateglo
){


}
