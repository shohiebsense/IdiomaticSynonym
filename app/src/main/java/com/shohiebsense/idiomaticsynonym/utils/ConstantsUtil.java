package com.shohiebsense.idiomaticsynonym.utils;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by Shohiebsense on 07/09/2017.
 */

public class ConstantsUtil {

        public final static String ERROR_TAG = "shohiebTRACE-E";
        public final static String DEBUG_TAG = "shohiebTRACE-D";
        public final static String CLOUD_URL = "http://res.cloudinary.com/shohiebsense/image/upload/v1511640503/";

    public void anuu (){
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View view) {

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
    }
}
