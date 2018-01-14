package com.shohiebsense.idiomaticsynonym.view.fragment.translateddisplay

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.model.IndexedSentence

/**
 * Created by Shohiebsense on 10/01/2018.
 */
class TranslatedDisplayPagerAdapter(val context :Context, fm : FragmentManager) : FragmentStatePagerAdapter(fm) {
    lateinit var translatedTexts : ArrayList<String>
    lateinit var indexedSentenceList : ArrayList<IndexedSentence>
    lateinit var indices : ArrayList<Int>
    lateinit var name : String
    lateinit var texts : String

    override fun getCount(): Int = 3

    override fun getItem(position: Int): Fragment {
        return if(position == 1) EnglishResultFragment.newInstance(name, texts)
        else if(position == 1) TranslatedDisplayFragment.newInstance(translatedTexts,indices)
        else IndexedSentenceListFragment.newInstance(indexedSentenceList)
    }

    override fun getItemPosition(`object`: Any): Int {
        return FragmentStatePagerAdapter.POSITION_NONE
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if(position == 0) context.getString(R.string.text_title_translation)
        else context.getString(R.string.text_title_sentence_index)
    }
}