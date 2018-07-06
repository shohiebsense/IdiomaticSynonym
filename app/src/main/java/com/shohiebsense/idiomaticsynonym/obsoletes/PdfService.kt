package com.shohiebsense.idiomaticsynonym.obsoletes


/**
 * Created by Shohiebsense on 24/06/2018
 */

class PdfService {

  /*  fun getTextFromPdf(myObserver: Observer<String>, destinationFile: File, from: Int, to : Int) {
        Observable.create<String> { subscriber ->
            var pdfStripper = PDFTextStripper()
            AppUtil.makeErrorLog("jalanssz "+loadedDocument.numberOfPages)
            pdfStripper.startPage = from
            pdfStripper.endPage = to
            var parsedText = pdfStripper.getText(loadedDocument)
            subscriber.onNext(parsedText)

            //second way
            *//* val parser = PDFParser(destinationFile)
             parser.parse()
             val cosdoc = parser.document
             val pdfStripper2= PDFTextStripper()
             val pddoc = PDDocument(cosdoc)
             pdfStripper2.startPage = 1
             pdfStripper2.endPage =2
             var text = pdfStripper2.getText(pddoc)
             subscriber.onNext(text)*//*
            document.close()
            subscriber.onComplete()
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(myObserver)
    }*/
}