
package com.shohiebsense.idiomaticsynonym.model

class TranslatedIdiom(
        var id : Int,
        var idiom : String,
        var meaning : String) {

    lateinit var exampleEn : String
    lateinit var exampleId : String

    constructor(id : Int, idiom : String, meaning : String, exampleEn : String, exampleId : String) : this(id, idiom, meaning){
        this.exampleEn = exampleEn
        this.exampleId = exampleId
    }




}
