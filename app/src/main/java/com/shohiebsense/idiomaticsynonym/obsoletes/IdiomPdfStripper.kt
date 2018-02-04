package com.shohiebsense.idiomaticsynonym.obsoletes

import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.text.TextPosition

/**
 * Created by Shohiebsense on 24/01/2018.
 */
class IdiomPdfStripper : PDFTextStripper() {




    override fun writeString(text: String?, textPositions: MutableList<TextPosition>?) {
        super.writeString(text, textPositions)
    }
}