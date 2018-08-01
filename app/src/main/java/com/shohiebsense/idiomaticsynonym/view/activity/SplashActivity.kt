package com.shohiebsense.idiomaticsynonym.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.db.IdiomDbHelper
import com.shohiebsense.idiomaticsynonym.services.emitter.TranslatedAndUntranslatedDataEmitter
import com.shohiebsense.idiomaticsynonym.view.callbacks.DatabaseCallback
import com.shohiebsense.idiomaticsynonym.view.activity.home.MainActivity
import kotlinx.android.synthetic.main.activity_splash.*


/**
 * Created by Shohiebsense on 22/10/2017.
 *
 *
 */
class SplashActivity : AppCompatActivity(), DatabaseCallback {
    override fun onFetchedBoth() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onFetchingData(idiomMode: Int) {
        loadingDescTextView.text = getString(R.string.text_action_loading_queries)
        loadingDescTextView.append("\n"+getString(R.string.make_sure_connect))
    }

    override fun onErrorFetchingData() {
        loadingDescTextView.text = getString(R.string.text_error_loading_queries)
    }

    override fun onFetchedTranslatedData() {
    }

    override fun onFetchedUntranslatedData() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }



    lateinit var dbHelper : IdiomDbHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //TranslatedAndUntranslatedDataEmitter(this,this).getAll()

        TranslatedAndUntranslatedDataEmitter(this,this).getAll()


        //val indexedSenteneDataEmitter = BookmarkDataEmitter(this)
        //indexedSenteneDataEmitter.insertIndexedSentence(21, "sentence" ,"idiom", "ganteng") //harusnya bukan idiom, tapi sentence

    }




    fun isStoragePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


                return true
            } else {

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true
        }

    }



}