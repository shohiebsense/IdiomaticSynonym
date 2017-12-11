package com.shohiebsense.straightidiomalearn

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.shohiebsense.straightidiomalearn.services.emitter.DatabaseDataEmitter
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.Log
import com.shohiebsense.straightidiomalearn.db.IdiomDbHelper
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import com.shohiebsense.straightidiomalearn.view.custom.CustomSnackbar
import com.shohiebsense.straightidiomalearn.view.fragment.callbacks.DatabaseCallback
import kotlinx.android.synthetic.main.activity_splash.*


/**
 * Created by Shohiebsense on 22/10/2017.
 *
 *
 */
class SplashActivity : AppCompatActivity(), DatabaseCallback {
    override fun onFetchingData(idiomMode: Int) {
        loadingDescTextView.text = getString(R.string.text_action_loading_queries)
    }

    override fun onErrorFetchingData() {
        loadingDescTextView.text = getString(R.string.text_error_loading_queries)
    }

    override fun onFetchedTranslatedData() {
    }

    override fun onFetchedUntranslatedData() {
        AppUtil.makeDebugLog("beres translasi")
        startActivity(Intent(this, MainActivity::class.java))
        finish()    }



    lateinit var dbHelper : IdiomDbHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppUtil.makeDebugLog("haiiii ")
        isReadAndWritePermissionGranted()
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            ASK_MULTIPLE_PERMISSION_REQUEST_CODE -> {
                if(grantResults.size > 0){
                    if(grantResults[0]== PackageManager.PERMISSION_GRANTED && grantResults[1]== PackageManager.PERMISSION_GRANTED ){
                        //isReadAndWritePermissionGranted()
                        //resume tasks needing this permission
                        dbHelper = IdiomDbHelper.getInstance(applicationContext)
                        dbHelper.setupDb()

                        //FOR DEVELOPMENT, UNCOMMENT IT
                        DatabaseDataEmitter(this,this).getAll()


                    }
                    else{
                        var snackbar = CustomSnackbar.make(rootConstraintLayout,
                                CustomSnackbar.LENGTH_INDEFINITE)

                        snackbar.setAction(getString(R.string.text_action_requestPermission), {
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET),
                                    ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
                        })

                        var snackbarView = snackbar.view
                        snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.secondaryLightColor))
                        snackbar.show()
                    }
                }
                else{
                    var snackbar = CustomSnackbar.make(rootConstraintLayout,
                            CustomSnackbar.LENGTH_INDEFINITE)

                    snackbar.setAction("Minta Permission", {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION),
                                ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
                    })

                    var snackbarView = snackbar.view
                    snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.secondaryLightColor))
                    snackbar.show()
                }
            }
        }



    }


    fun isStoragePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                AppUtil.makeErrorLog("copyying db")


                return true
            } else {
                AppUtil.makeErrorLog("request permission copyying db")

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true
        }
    }

    val ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1

    fun isReadAndWritePermissionGranted() : Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                IdiomDbHelper.getInstance(applicationContext).setupDb()

                //FOR DEVELOPMENT PURPOSES UNCOMMENTED IT
                DatabaseDataEmitter(this,this).getAll()
                Log.e("shohiebsense ", "truee")


                return true
            } else {
                Log.e("shohiebsense ", "read write permission not granted, requesting ..")

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION),
                        ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true
        }
    }
}