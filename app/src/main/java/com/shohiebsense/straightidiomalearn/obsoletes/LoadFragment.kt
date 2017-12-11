package com.shohiebsense.straightidiomalearn.obsoletes

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shohiebsense.straightidiomalearn.MainActivity


import com.shohiebsense.straightidiomalearn.R
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import com.shohiebsense.straightidiomalearn.view.fragment.MainFragment
import com.tom_roush.pdfbox.pdmodel.PDDocument

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlinx.android.synthetic.main.fragment_read.*
import java.io.FileInputStream

/**
 * Created by Shohiebsense on 06/09/2017.
 */

class LoadFragment : Fragment() {


    var FILENAME = "sample.pdf"
    lateinit var document: PDDocument


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        return inflater.inflate(R.layout.fragment_read, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nextButton.setOnClickListener{
            var intent = Intent(activity, MainActivity::class.java)
            intent.putExtra(MainActivity.intentMessage, MainFragment::class.java.name)
            startActivity(intent)
        }
    }

    @Throws(IOException::class)
    fun loadPdf(context: Context) {
        val file = File(context.cacheDir, FILENAME)
        if (!file.exists()) {
            // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
            // the cache directory.
            val asset = context.assets.open(FILENAME)
            val output = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var size = 0
            while ({size = asset.read(buffer); size} () != -1) {
                output.write(buffer, 0, size)
            }
            asset.close()
            output.close()
        }
        document = PDDocument.load(file)
        AppUtil.makeDebugLog(document.numberOfPages.toString() + " ")
    }

    fun copy(file : File) {
        try {
            val rootDirectory = ""
            val directory = File(rootDirectory)
            if(!directory.exists()){
                directory.mkdir();
            }


            var destinationFile = File(directory, "namee.pdf")
            var inputStream = FileInputStream(file)
            val os = FileOutputStream(destinationFile)
            var byteRead = 0
            val buf = ByteArray(1024)
            while ( inputStream.read(buf).let { byteRead = inputStream.read(buf) ; byteRead != -1}) {
                os.write(buf, 0, byteRead)
            }

            /*  do{
                  byteRead = inputStream.read(buf)
                  os.write(buf, 0, byteRead)
              }while (inputStream.read(buf) > 0)*/

            inputStream.close()
            os.flush()
            os.close()
            AppUtil.makeDebugLog("sizzee "+destinationFile.length()+ "  "+destinationFile.absolutePath)

        } catch (e: IOException) {
            AppUtil.makeErrorLog("io error " +e.toString())
        }

    }



}
