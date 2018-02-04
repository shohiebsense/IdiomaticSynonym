package com.shohiebsense.idiomaticsynonym

import android.content.Context
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.support.constraint.ConstraintLayout
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import junit.framework.Assert.assertEquals
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Created by Shohiebsense on 15/01/2018.
 */

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(LOLLIPOP), packageName = "com.shohiebsense.idiomaticsynonym")
class DBServiceTest {


    lateinit var context : Context
    lateinit var bookmarkDataEmitter : BookmarkDataEmitter

    @Before
    fun setUp(){
        context = RuntimeEnvironment.application
        bookmarkDataEmitter = BookmarkDataEmitter(context)
    }

    //jalan sebenernya
    @Test
    fun insertion(){
        /*bookmarkDataEmitter.insertBookmarkEnglish("fileName","whoooolleee text")
        assertEquals(1,bookmarkDataEmitter.queryService.countBookmarkEnglishTable())*/
    }


    @Test
    fun sentenceInsertion(){
        /*bookmarkDataEmitter.insertBookmarkEnglish("fileName", "english")
        bookmarkDataEmitter.queryService.insertSentenceAndItsSource(1, "why me")
        bookmarkDataEmitter.queryService.insertSentenceAndItsSource(2, "why me tho")
        bookmarkDataEmitter.queryService.insertSentenceAndItsSource(3, "surely why me")*/

      //  bookmarkDataEmitter.queryService.countIndexedSentenceBasedOnFileName("fileName")
    }


    @Test
    fun registeringRequiredForm(){
       /* val activity = Robolectric.setupActivity(BookmarkedActivity::class.java)
        val parent= ConstraintLayout(activity)

         */
       /* bookmarkDataEmitter.insertBookmarkEnglish("fileName","whoooolleee text")
        //bookmarkDataEmitter.insertBookmarkEnglish("fileName2","whoooolleee aefaewftext")
        //bookmarkDataEmitter.insertBookmarkEnglish("fileNam32","whoooolleee aefaewftext")
        Assert.assertEquals(1, bookmarkDataEmitter.queryService.selectAllBookmarks().size)*/
    }









}