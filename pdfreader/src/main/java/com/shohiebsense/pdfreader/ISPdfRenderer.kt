package com.shohiebsense.pdfreader

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException


/**
 * Created by Shohiebsense on 16/09/2018
 */

class ISPdfRenderer(val pdfRenderListener: PdfRenderListener, val filePath: String) {
    //index to track currentPage in rendered Pdf
    private var currentPageNumber = 0
    var mCurrentPage : PdfRenderer.Page? = null
    var mPdfRenderer: PdfRenderer? = null
    var mFileDescriptor: ParcelFileDescriptor? = null

    var isPreviousEnabled = true
    var isNextEnabled = true

    /**
     * API for initializing file descriptor and pdf renderer after selecting pdf
     * from list
     *
     * @param filePath
     */
    private fun openRenderer() {
        val file = File(filePath)
        try {
            mFileDescriptor = ParcelFileDescriptor.open(file,
                    ParcelFileDescriptor.MODE_READ_ONLY)
            mPdfRenderer = PdfRenderer(mFileDescriptor)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    private fun showPage(index: Int) {
        if (mPdfRenderer == null || mPdfRenderer!!.getPageCount() <= index
                || index < 0) {
            return
        }
        // For closing the current page before opening another one.
        try {
            if (mCurrentPage != null) {
                mCurrentPage?.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Open page with specified index
        mCurrentPage = mPdfRenderer?.openPage(index)
        val bitmap = Bitmap.createBitmap(mCurrentPage!!.getWidth(),
                mCurrentPage!!.getHeight(), Bitmap.Config.ARGB_8888)

        //Pdf page is rendered on Bitmap
        mCurrentPage!!.render(bitmap, null, null,
                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        //Set rendered bitmap to ImageView
        //switch the pdfview to listener
        //pdfView.setImageBitmap(bitmap)
        var index = mCurrentPage?.getIndex()
        val pageCount = mPdfRenderer?.getPageCount()
        isPreviousEnabled = 0 != index
        var pageNumber = 0
        if (index != null) {
            pageNumber = index +1
            isNextEnabled = pageNumber < pageCount!!
        }
        var title = "( $pageNumber / $pageCount )"
        pdfRenderListener.onRendered(bitmap,title)
    }




    /**
     * API for cleanup of objects used in rendering
     */
    private fun closeRenderer() {

        try {
            if (mCurrentPage != null)
                mCurrentPage?.close()
            if (mPdfRenderer != null)
                mPdfRenderer?.close()
            if (mFileDescriptor != null)
                mFileDescriptor?.close()
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

    }

    interface PdfRenderListener{
        fun onRendered(bitmap : Bitmap, title : String)
    }





}