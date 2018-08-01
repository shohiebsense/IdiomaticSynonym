package com.shohiebsense.idiomaticsynonym.view.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.shohiebsense.idiomaticsynonym.view.activity.home.fragment.BookmarksFragment
import com.shohiebsense.idiomaticsynonym.view.activity.home.fragment.PdfDisplayFragment
import com.shohiebsense.idiomaticsynonym.view.activity.home.fragment.StatisticsFragment

/**
 * Created by Shohiebsense on 19/01/2018.
 */
class MainPagerFragmentStatePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return PdfDisplayFragment()
            1 -> return BookmarksFragment.newInstance()
            2 -> return StatisticsFragment()
        }
        return PdfDisplayFragment()
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence? {

        return ""
    }
}