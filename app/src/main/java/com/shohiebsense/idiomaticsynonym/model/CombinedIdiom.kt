package com.shohiebsense.idiomaticsynonym.model

/**
 * Created by Shohiebsense on 07/01/2018.
 */
class CombinedIdiom(val idiom : String) {
    var meaning : String = ""

    constructor(idiom :String, meaning : String) : this(idiom){
        this.meaning = meaning
    }
}