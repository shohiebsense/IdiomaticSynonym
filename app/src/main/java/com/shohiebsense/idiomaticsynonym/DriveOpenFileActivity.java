/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shohiebsense.idiomaticsynonym;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.events.OpenFileCallback;
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish;
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter;
import com.shohiebsense.idiomaticsynonym.utils.AppUtil;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * An activity to illustrate how to open contents and listen
 * the download progress if the file is not already sync'ed.
 */
public class DriveOpenFileActivity extends BaseDemoActivity implements BookmarkDataEmitter.SingleBookmarkCallback {
    public static final String INTENT_ID = "englishid";

    int englishId = 0;

    private static final String TAG = "RetrieveWithProgress";

    /**
     * Progress bar to show the current download progress of the file.
     */
    private ProgressBar mProgressBar;

    /**
     * Text view for file contents
     */

    private boolean driveClientReady = false;
    BookmarkedEnglish bookmarkedEnglish;
    private TextView mFileContents;

    private ExecutorService mExecutorService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        BookmarkDataEmitter bookmarkDataEmitter = new BookmarkDataEmitter(this);
        bookmarkDataEmitter.getEnglishBookmark(englishId,this);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setMax(100);
        mFileContents = findViewById(R.id.fileContents);
        mFileContents.setText("");
        mExecutorService = Executors.newSingleThreadExecutor();
        englishId = getIntent().getIntExtra(INTENT_ID,0);
    }

    @Override
    protected void onDriveClientReady() {
        /*pickTextFile()
                .addOnSuccessListener(this,
                        driveId -> retrieveContents(driveId.asDriveFile()))
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "No file selected", e);
                    showMessage(getString(R.string.file_not_selected));
                    finish();
                });*/
        driveClientReady = true;
        if(bookmarkedEnglish != null){
            retrieveContents(DriveId.decodeFromString(bookmarkedEnglish.getUploadId()).asDriveFile());
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mExecutorService.shutdown();
    }

    @Override
    public void onFetched(@NotNull BookmarkedEnglish bookmark) {
        bookmarkedEnglish = bookmark;
    }

    private void retrieveContents(DriveFile file) {
        // [START read_with_progress_listener]
        AppUtil.Companion.makeErrorLog("yoww "+file.getDriveId().encodeToString());
        OpenFileCallback openCallback = new OpenFileCallback() {
            @Override
            public void onProgress(long bytesDownloaded, long bytesExpected) {
                // Update progress dialog with the latest progress.
                int progress = (int) (bytesDownloaded * 100 / bytesExpected);
                Log.d(TAG, String.format("Loading progress: %d percent", progress));
                mProgressBar.setProgress(progress);
            }

            @Override
            public void onContents(@NonNull DriveContents driveContents) {
                // onProgress may not be called for files that are already
                // available on the device. Mark the progress as complete
                // when contents available to ensure status is updated.
                mProgressBar.setProgress(100);
                // Read contents
                // [START_EXCLUDE]
                try {
                    try (BufferedReader reader = new BufferedReader(
                                 new InputStreamReader(driveContents.getInputStream()))) {
                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }

                        showMessage(getString(R.string.content_loaded));
                        mFileContents.setText(builder.toString());
                        getDriveResourceClient().discardContents(driveContents);

                        String url = "https://docs.google.com/file/d/"+file.getDriveId().getResourceId();
                        AppUtil.Companion.makeErrorLog(url);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                } catch (IOException e) {
                    onError(e);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onError(@NonNull Exception e) {
                // Handle error
                // [START_EXCLUDE]
                Log.e(TAG, "Unable to read contents", e);
                showMessage(getString(R.string.read_failed));
                finish();
                // [END_EXCLUDE]
            }
        };



        //getDriveResourceClient().openFile(DriveId.decodeFromString("CAESITF5NWwxZlo4cUd5M2x6SjZkbThSYVU1MGU4MjVlR05FcBjgUSDanavYgFcoAA").asDriveFile(), DriveFile.MODE_READ_ONLY, openCallback);
        // [END read_with_progress_listener]
        getDriveResourceClient().openFile(file, DriveFile.MODE_READ_WRITE, openCallback);

    }


}
