package com.shohiebsense.straightidiomalearn

import android.app.Application
import android.content.Context
import com.cloudinary.android.MediaManager
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType
import com.shohiebsense.straightidiomalearn.db.IdiomDbHelper
import com.shohiebsense.straightidiomalearn.db.Idioms
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import org.jetbrains.anko.configuration


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

    fun initImageLoader(context: Context) {
        val config = ImageLoaderConfiguration.Builder(context)
        config.threadPriority(Thread.NORM_PRIORITY - 2)
        config.denyCacheImageMultipleSizesInMemory()
        config.diskCacheFileNameGenerator(Md5FileNameGenerator())
        config.diskCacheSize(50 * 1024 * 1024) // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO)
        config.threadPoolSize(2)
        config.diskCacheExtraOptions(480, 320, null)


        // config.writeDebugLogs();

        ImageLoader.getInstance().init(config.build())
    }

    override fun onCreate() {
        super.onCreate()
        initImageLoader(applicationContext)
        MediaManager.init(this);

    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}


