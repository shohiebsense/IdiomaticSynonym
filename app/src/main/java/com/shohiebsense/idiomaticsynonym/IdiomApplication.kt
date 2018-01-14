package com.shohiebsense.idiomaticsynonym
import android.app.Application
import android.content.Context
import com.cloudinary.android.MediaManager
import com.shohiebsense.idiomaticsynonym.db.IdiomDbHelper


/**
 * Created by Shohiebsense on 11/10/2017.
 */

class IdiomApplication : Application() {



   /* val data: KotlinReactiveEntityStore<Persistable> by lazy {
        IdiomDbHelper.getInstance(applicationContext).setupDb()

        val source = IdiomDatabaseSource(this, Models.DEFAULT, Idioms.NAME+".db", 1)

        source.setLoggingEnabled(true)
        source.setTableCreationMode(TableCreationMode.CREATE_NOT_EXISTS)


        if (BuildConfig.DEBUG) {
            // use this in development mode to drop and recreate the tables on every upgrade
        }
        val dataStore = KotlinEntityDataStore<Persistable>(source.configuration)

        KotlinReactiveEntityStore<Persistable>(KotlinEntityDataStore(source.configuration))
    }*/


    override fun onCreate() {
        super.onCreate()
        val dbHelper = IdiomDbHelper(this)
        dbHelper.setupDb()
        MediaManager.init(this);
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}


