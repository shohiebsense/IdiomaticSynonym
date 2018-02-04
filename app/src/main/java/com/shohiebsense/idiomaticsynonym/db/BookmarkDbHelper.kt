package com.shohiebsense.idiomaticsynonym.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import org.jetbrains.anko.db.*

/**
 * Created by Shohiebsense on 13/01/2018.
 */
class BookmarkDbHelper(val context : Context) : ManagedSQLiteOpenHelper(context, Bookmark.NAME, null, Bookmark.VERSION) {



    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(Bookmark.TABLE_BOOKMARK_ENGLISH, true,
                Bookmark.COLUMN_ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                Bookmark.COLUMN_PDFFILENAME to TEXT,
                Bookmark.COLUMN_ENGLISH to TEXT,
                Bookmark.COLUMN_INDONESIAN to TEXT
        )

        db.createTable(Bookmark.TABLE_BOOKMARK_INDEXED_SENTENCES, true,
                Bookmark.COLUMN_ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                Bookmark.COLUMN_BOOKMARK_ENGLISH_ID to INTEGER,
                Bookmark.COLUMN_SENTENCE to TEXT,
                Bookmark.COLUMN_SENTENCE_INDEX to INTEGER,
                Bookmark.COLUMN_IDIOM to TEXT,
                FOREIGN_KEY(Bookmark.COLUMN_BOOKMARK_ENGLISH_ID, Bookmark.TABLE_BOOKMARK_ENGLISH, Bookmark.COLUMN_ID)
        )

        AppUtil.makeDebugLog("table created")
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
       // db.dropTable(Bookmark.TABLE_BOOKMARK_INDEXED_SENTENCES,true)
        //db.dropTable(Bookmark.TABLE_BOOKMARK_ENGLISH, true)
    }



}

val Context.bookmarkDatabase : BookmarkDbHelper
    get() = BookmarkDbHelper(this)