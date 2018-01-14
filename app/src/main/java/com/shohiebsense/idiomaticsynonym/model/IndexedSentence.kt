package com.shohiebsense.idiomaticsynonym.model

import android.os.Parcel
import android.os.Parcelable


/**
 * Created by Shohiebsense on 27/12/2017.
 */

data class IndexedSentence(val sentence: String) : Parcelable {

    var id : Int = 0
    var index : Int? = null
    var pdfFileName : String? = null
    var bookId : Int = 0

    constructor(parcel: Parcel) : this(parcel.readString()) {
        id = parcel.readInt()
        index = parcel.readValue(Int::class.java.classLoader) as? Int
        pdfFileName = parcel.readString()
        bookId = parcel.readInt()
    }


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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sentence)
        parcel.writeInt(id)
        parcel.writeValue(index)
        parcel.writeString(pdfFileName)
        parcel.writeInt(bookId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<IndexedSentence> {
        override fun createFromParcel(parcel: Parcel): IndexedSentence {
            return IndexedSentence(parcel)
        }

        override fun newArray(size: Int): Array<IndexedSentence?> {
            return arrayOfNulls(size)
        }
    }



}