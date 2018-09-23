package com.shohiebsense.idiomaticsynonym.db


/**
 * Created by Shohiebsense on 12/10/2017.
 */

object IdiomsDbConstants {
    const val NAME : String = "idioms.db"
    const val VERSION : Int = 1


    const val TABLE_TRANSLATED_IDIOM = "TranslatedIdiom"
    const val TABLE_UNTRANSLATED_IDIOM = "UntranslatedIdiom"
    const val TABLE_INDEXED_SENTENCE = "IndexedSentence"
    const val TABLE_IDIOMS = "idioms"



    const val COLUMN_ID = "ID"
    const val COLUMN_IDIOM = "idiom"
    const val COLUMN_MEANING = "meaning"
    const val COLUMN_EXAMPLE_EN = "exampleEn"
    const val COLUMN_EXAMPLE_ID = "exampleId"

    const val COLUMN_TB_IDIOM_IDIOM = COLUMN_IDIOM
    const val COLUMN_TRANSLATION = "translation"
    const val COLUMN_SIMILAR = "similar"


}