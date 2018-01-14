package com.shohiebsense.idiomaticsynonym.db

/**
 * Created by Shohiebsense on 13/01/2018.
 */
object Bookmark {
    const val NAME : String = "Bookmark.db"
    const val VERSION : Int = 1


    const val COLUMN_ID = "ID"


    const val TABLE_BOOKMARK_ENGLISH = "BookEng"
    //bookmark english : id, english, pdfFileName
    const val COLUMN_PDFFILENAME = "pdfFileName"
    const val COLUMN_ENGLISH = "english"

    const val TABLE_BOOKMARK_INDEXED_SENTENCES = "BookSenIndex"
    //bookmark indexed sentence : id, sentence index : INT, bookmark english id, SENTENCE
    const val COLUMN_BOOKMARK_ENGLISH_ID = "book_id"
    const val COLUMN_SENTENCE_INDEX = "sentenceIndex"
    const val COLUMN_SENTENCE = "sentence"


}