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
                Bookmark.COLUMN_INDONESIAN to TEXT,
                Bookmark.COLUMN_IDIOM to TEXT,
                Bookmark.COLUMN_SENTENCE_INDEX to TEXT,
                Bookmark.COLUMN_UPLOAD_ID to TEXT
        )

        db.createTable(ReplacedHistoryConstant.TABLE_REPLACED_HISTORY, true,
                ReplacedHistoryConstant.COLUMN_ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                ReplacedHistoryConstant.COLUMN_BOOKMARK_ID to INTEGER,
                ReplacedHistoryConstant.COLUMN_IDIOM to TEXT,
                ReplacedHistoryConstant.COLUMN_ORIGINAL_TRANSLATION to TEXT,
                ReplacedHistoryConstant.COLUMN_REPLACED_TRANSLATION to TEXT,
                ReplacedHistoryConstant.COLUMN_SENTENCE_ORDER to INTEGER,
                ReplacedHistoryConstant.COLUMN_IDIOM_INDEX to INTEGER,
                ReplacedHistoryConstant.COLUMN_IDIOM_ENDINDEX to INTEGER
        )
        AppUtil.makeDebugLog("table created")
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.dropTable(Bookmark.TABLE_BOOKMARK_ENGLISH, true)
        db.dropTable(ReplacedHistoryConstant.TABLE_REPLACED_HISTORY,true)
    }



}

val Context.bookmarkDatabase : BookmarkDbHelper
    get() = BookmarkDbHelper(this)