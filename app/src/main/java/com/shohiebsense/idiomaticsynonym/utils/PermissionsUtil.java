package com.shohiebsense.idiomaticsynonym.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by Shohiebsense on 14/09/2017.
 */

public class PermissionsUtil {




    public static void requestReadPermission(Activity context, int permissionCode){
        final int MY_PERMISSIONS_REQUEST_PHONE_CALL = 1;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.e("shohiebsensee p","Permission is granted");
            //File write logic here
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.e("shohiebsensee p","Permission read is granted");
            //File write logic here
            return;
        }

        ContextCompat.checkSelfPermission(context,      Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           /*context.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    232);*/

            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 233);

        }

    }
}
