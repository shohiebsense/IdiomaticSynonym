package com.shohiebsense.idiomaticsynonym

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.cloudinary.android.MediaManager
import com.shohiebsense.idiomaticsynonym.db.IdiomDbHelper
import com.shohiebsense.idiomaticsynonym.utils.LocaleManager


/**
 * Created by Shohiebsense on 13/06/2018
 */

class XprediomApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.setLocale(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }

    override fun onCreate() {
        super.onCreate()
        val dbHelper = IdiomDbHelper(this)
        dbHelper.setupDb()
        MediaManager.init(this)
        LocaleManager.setLocale(this)
    }
}