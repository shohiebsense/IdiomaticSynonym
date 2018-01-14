package com.shohiebsense.idiomaticsynonym

import android.content.Context
import com.shohiebsense.idiomaticsynonym.utils.TestUtil
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import java.util.regex.Pattern

/**
 * Created by Shohiebsense on 14/01/2018.
 */

@RunWith(MockitoJUnitRunner::class)
class IdiomUnitTest {

    @Mock
    lateinit var context : Context

    @Test
    fun shouldSameRegardlessCombining(){
        val combined = TestUtil.page1 + TestUtil.page2 + TestUtil.page3 + TestUtil.page4


        val textArrayList = arrayListOf<String>()
        textArrayList.add(TestUtil.page1)
        textArrayList.add(TestUtil.page2)
        textArrayList.add(TestUtil.page3)
        textArrayList.add(TestUtil.page4)

        var combinedActual = ""
        textArrayList.forEach {
            combinedActual += it
        }

        Assert.assertEquals(combined, combinedActual)
    }

    @Test
    fun matchTheSentenceNumber(){
        val sentence = "shohieb ahmad nasruddin. Ia pandai dan rajin menabung. Minimal sholat daripada enggak sholat"
        val getSentence = getSentence(sentence, "pandai")


        Assert.assertEquals("Ia pandai dan rajin menabung", getSentence)





    }

    @Test
    fun shouldEndsWithPeriod(){

    }

    fun getSentence(text: String, word: String): String {
        val END_OF_SENTENCE = Pattern.compile("\\.\\s+")

        val lcword = word.toLowerCase()
        return END_OF_SENTENCE.splitAsStream(text)
                .filter({ s -> s.toLowerCase().contains(lcword) })
                .findAny()
                .orElse(null)
    }
}