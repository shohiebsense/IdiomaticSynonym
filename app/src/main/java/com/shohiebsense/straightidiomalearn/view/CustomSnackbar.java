package com.shohiebsense.straightidiomalearn.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.shohiebsense.straightidiomalearn.R;

import org.w3c.dom.Text;


/**
 * Created by shohiebsense on 03/08/17.
 */

public class CustomSnackbar extends BaseTransientBottomBar {
    private final WindowManager windowManager;


    /**
     * Constructor for the transient bottom bar.
     *  @param parent              The parent for this transient bottom bar.
     * @param content             The content view for this transient bottom bar.
     * @param contentViewCallback The content view callback for this transient bottom bar.
     *
     */
    protected CustomSnackbar(@NonNull ViewGroup parent, @NonNull View content, @NonNull ContentViewCallback contentViewCallback) {
        super(parent, content, contentViewCallback);
        this.windowManager = (WindowManager) parent.getContext().getSystemService(Context.WINDOW_SERVICE);
    }

    public static CustomSnackbar make(ViewGroup parent, int duration) {
        // inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View content = inflater.inflate(R.layout.view_snackbar_download_progress, parent, false);

        //content.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.accent));

        // create snackbar with custom view
        ContentViewCallback callback= new ContentViewCallback(content);
        CustomSnackbar customSnackbar = new CustomSnackbar(parent, content, callback);

        // set snackbar duration
        customSnackbar.setDuration(duration);
        return customSnackbar;
    }

    // set text in custom layout
    public CustomSnackbar setText(CharSequence text) {
        TextView textView = (TextView) getView().findViewById(R.id.loadingTextView);
        textView.setText(text);
        return this;
    }

    /*public void setProgress(int progress){
        ProgressBar loadingProgressBar = (ProgressBar) getView().findViewById(R.id.loadingProgressBar);
        loadingProgressBar.setProgress(progress);
    }

    public CustomSnackbar setHideProgressBar(){
        ProgressBar loadingProgressBar = (ProgressBar) getView().findViewById(R.id.loadingProgressBar);
        loadingProgressBar.setVisibility(View.GONE);
        return this;
    }*/

    // set action in custom layout
    public CustomSnackbar setAction(CharSequence text, final View.OnClickListener listener) {
        TextView actionView = (TextView) getView().findViewById(R.id.requestPermissionTextView);
        actionView.setText(text);
        actionView.setVisibility(View.VISIBLE);
        getView().setOnClickListener(view -> {
            listener.onClick(view);
            // Now dismiss the Snackbar
            dismiss();
        });
        return this;
    }


    private static class ContentViewCallback implements BaseTransientBottomBar.ContentViewCallback {
        private View content;

        public ContentViewCallback(View content) {
            this.content = content;
        }

        @Override
        public void animateContentIn(int delay, int duration) {
            ViewCompat.setScaleY(content, 0f);
            ViewCompat.animate(content)
                    .scaleY(1f).setDuration(duration)
                    .setStartDelay(delay);
        }

        @Override
        public void animateContentOut(int delay, int duration) {
            // e.g. original snackbar uses alpha animation, from 1 to 0
            ViewCompat.setScaleY(content, 1f);
            ViewCompat.animate(content)
                    .scaleY(0f)
                    .setDuration(duration)
                    .setStartDelay(delay);
        }
    }
}
