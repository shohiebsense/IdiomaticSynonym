package com.shohiebsense.idiomaticsynonym.model

import android.os.Parcel
import android.os.Parcelable


/**
 * Created by Shohiebsense on 27/12/2017.
 */

data class IndexedSentence(val sentence: String)  {

    var id : Int = 0
    var index : Int? = null
    var pdfFileName : String? = null
    var bookId : Int = 0
    var idiom : String? = null



    constructor(sentence: String, index : Int) : this(sentence){
        this.index = index
    }

    constructor(sentence: String, index: Int, pdfFileName: String) : this(sentence){
        this.index = index
        this.pdfFileName = pdfFileName
    }


    constructor(sentence: String, index: Int, pdfFileName : String, bookId : Int) : this(sentence){
        this.index = index
        this.pdfFileName = pdfFileName
        this.bookId = bookId
    }




}