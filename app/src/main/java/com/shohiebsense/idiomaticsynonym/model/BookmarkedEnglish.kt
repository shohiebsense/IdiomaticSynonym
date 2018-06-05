package com.shohiebsense.idiomaticsynonym.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Shohiebsense on 14/01/2018.
 */
class BookmarkedEnglish(var id : Int,
                        var fileName : String,
                        var english: CharSequence,
                        var indonesian : CharSequence?,
                        var idioms : String,
                        var indexedSentences : String,
                        var uploadId : String
                        ) : Parcelable {



    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
            ) {

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(fileName)
        parcel.writeString(english.toString())
        parcel.writeString(indonesian?.toString())
        parcel.writeString(idioms)
        parcel.writeString(indexedSentences)
        parcel.writeString(uploadId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookmarkedEnglish> {
        override fun createFromParcel(parcel: Parcel): BookmarkedEnglish {
            return BookmarkedEnglish(parcel)
        }

        override fun newArray(size: Int): Array<BookmarkedEnglish?> {
            return arrayOfNulls(size)
        }
    }


}