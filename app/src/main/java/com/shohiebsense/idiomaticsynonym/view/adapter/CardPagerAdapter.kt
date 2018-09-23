package com.shohiebsense.idiomaticsynonym.view.adapter

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cloudinary.android.MediaManager
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.wang.avi.AVLoadingIndicatorView
import android.os.Build.VERSION


/**
 * Created by Shohiebsense on 25/11/2017.
 */
class CardPagerAdapter(var context: Activity) : PagerAdapter() {

    lateinit var inflater : LayoutInflater


    var images : MutableList<String>
   //
    var imageHeight = 0
    init {
        images = mutableListOf<String>()

        for(i in 0 .. 15){
            images.add(MediaManager.get().url().generate("$i.png"))
        }

    }




    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater = LayoutInflater.from(context)
        var layout = inflater.inflate(R.layout.item_factcard, container, false)
        var imageView = layout.findViewById<ImageView>(R.id.factCardImageView)
        var avLoadingIndicatorView = layout.findViewById<AVLoadingIndicatorView>(R.id.avLoadingIndicatorView)
        var imageName = position +1
        Glide.with(context)
                .load(images.get(position+1))
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        avLoadingIndicatorView.visibility = View.GONE
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        avLoadingIndicatorView.visibility = View.GONE
                        AppUtil.makeDebugLog("resource failed")

                        return false
                    }

                })
                .into(imageView)

        container?.addView(layout, 0)
        imageView.setOnClickListener {
            fullScreen()
        }


        return layout
    }


    fun fullScreen() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        val uiOptions = context.getWindow().getDecorView().getSystemUiVisibility()
        var newUiOptions = uiOptions
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        val isImmersiveModeEnabled = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY == uiOptions


        // Navigation bar hiding:  Backwards compatible to ICS.
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION


        // Status bar hiding: Backwards compatible to Jellybean
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN


        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY


        context.getWindow().getDecorView().setSystemUiVisibility(newUiOptions)
        //END_INCLUDE (set_ui_flags)
    }



    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view.equals(`object` as ConstraintLayout)
    }

    override fun getCount(): Int = 15



    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container!!.removeView(`object` as ConstraintLayout)
    }
}