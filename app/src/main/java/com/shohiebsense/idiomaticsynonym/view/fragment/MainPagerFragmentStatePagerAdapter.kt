package com.shohiebsense.idiomaticsynonym.view.fragment

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.view.fragment.pdfdisplay.PdfDisplayFragment

/**
 * Created by Shohiebsense on 19/01/2018.
 */
class MainPagerFragmentStatePagerAdapter(fm: FragmentManager, val bookmarks: ArrayList<BookmarkedEnglish>) : FragmentStatePagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return PdfDisplayFragment()
            1 -> return BookmarksFragment.newInstance(bookmarks)
            2 -> return StatisticsFragment()
        }
        return PdfDisplayFragment()
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence? {

        return ""
    }
}