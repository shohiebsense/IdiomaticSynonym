package com.shohiebsense.straightidiomalearn.view.fragment.pdfdisplay

import android.content.Context
import android.graphics.drawable.Drawable
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
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.shohiebsense.straightidiomalearn.R
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import com.wang.avi.AVLoadingIndicatorView

/**
 * Created by Shohiebsense on 25/11/2017.
 */
class CardPagerAdapter(var context: Context) : PagerAdapter() {

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
                        AppUtil.makeDebugLog("resource readyyy")
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

        return layout
    }



    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view.equals(`object` as ConstraintLayout)
    }

    override fun getCount(): Int = 15



    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container!!.removeView(`object` as ConstraintLayout)
    }
}