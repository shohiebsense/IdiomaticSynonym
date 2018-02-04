package com.shohiebsense.idiomaticsynonym.view.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.view.fragment.translateddisplay.EnglishResultFragment
import com.shohiebsense.idiomaticsynonym.view.fragment.translateddisplay.IndexedSentenceFragment
import com.shohiebsense.idiomaticsynonym.view.fragment.translateddisplay.TranslatedDisplayFragment

/**
 * Created by Shohiebsense on 10/01/2018.
 */
class TranslatedDisplayPagerAdapter(val context: Context, fm: FragmentManager, val lastId : Int) : FragmentStatePagerAdapter(fm) {
    lateinit var name : String
    lateinit var texts : String

    override fun getCount(): Int = 3

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> EnglishResultFragment.newInstance(lastId)
            1 -> TranslatedDisplayFragment.newInstance(lastId)
            else -> IndexedSentenceFragment.newInstance(lastId)
        }
    }

    override fun getItemPosition(`object`: Any): Int {
        return FragmentStatePagerAdapter.POSITION_NONE
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if(position == 0) context.getString(R.string.text_title_translation)
        else context.getString(R.string.text_title_sentence_index)
    }
}