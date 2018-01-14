package com.shohiebsense.idiomaticsynonym.db

import android.content.Context
import android.database.sqlite.SQLiteCantOpenDatabaseException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteQueryBuilder
import android.util.Log
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Shohiebsense on 13/10/2017.
 */

class IdiomDbHelper(val context : Context) : ManagedSQLiteOpenHelper(context, Idioms.NAME, null, 1){
    private var dbPath: String = context.getDatabasePath(Idioms.NAME).toString()


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
        try {
            val db = SQLiteDatabase.openDatabase(dbPath, null,
                    SQLiteDatabase.OPEN_READONLY or SQLiteDatabase.NO_LOCALIZED_COLLATORS)
            val sql = SQLiteQueryBuilder.buildQueryString(true, "db_version", arrayOf("version"), null, null, null, null, null)
            val cur = db.rawQuery(sql, null)
            cur.moveToFirst()
            val version = cur.getInt(0)
            if (version != currentVersion) {
                Log.d("Kamus", "DB expired")
                cur.close()

                return false
            }
            cur.close()
        } catch (e: SQLiteException) {
            AppUtil.makeDebugLog("db not exists")
            return false
        } catch (e : SQLiteCantOpenDatabaseException){
            AppUtil.makeDebugLog("db not exists")
            return false
        }

        AppUtil.makeDebugLog("db exists")
        return true
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
    get() = IdiomDbHelper(this)