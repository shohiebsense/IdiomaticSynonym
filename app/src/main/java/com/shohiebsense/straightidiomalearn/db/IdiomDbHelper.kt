package com.shohiebsense.straightidiomalearn.db

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteQueryBuilder
import android.support.v4.content.ContextCompat
import android.util.Log
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper
import org.jetbrains.anko.doAsync
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by Shohiebsense on 13/10/2017.
 */

class IdiomDbHelper(var context : Context) : ManagedSQLiteOpenHelper(context, Idioms.NAME, null, 1){


    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: IdiomDbHelper? = null

        private lateinit var dbPath: String

        @Synchronized
        fun getInstance(context: Context) : IdiomDbHelper {

            if (instance == null) {
                dbPath = context.getDatabasePath(Idioms.NAME).toString()
                instance = IdiomDbHelper(context.getApplicationContext())
            }


            return instance!!
        }

        fun resetInstance(){
            instance = null
        }
    }





    private val currentVersion = 1

    override fun onCreate(db: SQLiteDatabase?) {
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }




    fun setupDb()  {
        AppUtil.makeErrorLog("setup dbb")
        if (!check()) {
            AppUtil.makeErrorLog("db not exists")
            copy()
        }
        AppUtil.makeErrorLog("db exists")
    }

    fun check() : Boolean {
        val file = File(context.getDatabasePath(Idioms.NAME).getParent())
        return file.exists()
    }



    fun copy(){
        AppUtil.makeDebugLog("copyyingg ")
        try {
            val file = File(context.getDatabasePath(Idioms.NAME).getParent())
            file.mkdir()

            //            String zipFilePath = context.getDatabasePath("quran.zip").toString();
            //            Log.d("quranku", "ZIP FILE PATH " + zipFilePath);
            //            ZipInputStream zis = new ZipInputStream(context.getResources().openRawResource(R.raw.quran));
            //            zis.getNextEntry();

            val zis = context.assets.open(Idioms.NAME)
            AppUtil.makeDebugLog("Db path" + dbPath)
            FileOutputStream(dbPath).use { out ->
                zis.use {
                    it.copyTo(out)
                }
            }
            /*var byteRead  = 0
            val buf = ByteArray(1024)
            while (zis.read(buf).let {byteRead = zis.read(buf); byteRead > 0})  {
                os.write(buf, 0, byteRead)
            }*/
            zis.close()
           /* os.flush()
            os.close()*/
            AppUtil.makeDebugLog("Finish copying")

        } catch (e: Exception) {

            AppUtil.makeErrorLog("copy :" + e.toString())
        }

    }
}

val Context.database : IdiomDbHelper
    get() = IdiomDbHelper.getInstance(this)