package com.shohiebsense.idiomaticsynonym.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by Shohiebsense on 17/12/2017.
 */

class AntiSwipeViewPager : android.support.v4.view.ViewPager{

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

}