package com.where.prateekyadav.myapplication.search.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.where.prateekyadav.myapplication.search.network.ApiClient;


/**
 * Created by Prateek on 11/20/2016.
 */

public class Utility {
    public String getPhotosUrl(String reference, String maxSize) {
        // https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=CoQBdwAAAGUi0qj1njgbj-KTLMSFewPyPxYLS0YJ7wy9K6XwGO8l6sLhdAbm1UljMuC51BaaI3KLqsty_H2Hw4hS0FxrM6aP_nVXq-RykiUSDYO4M-YfwoyjBNeD89qBUX6x1nqtuyIO2UlMtq1H_FHZrSH60eT0yfBDdHnhtgnEoDDqzJnIEhDvzZztoaJ3WcQ-4JD9kvxYGhRVVo8-gGmVuaY4QbqOd_s5WnKAPA
        // &key=AIzaSyA8oSQtjL2WTCD6fAoO76uzQc8AB2ZNujk

        String url = ApiClient.BASE_URL + "photo?maxwidth=" + maxSize + "&photoreference=" + reference + "&key=" + Constant.API_KEY_PLACES;
        return url;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isOnline(Context _context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) _context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager
                    .getActiveNetworkInfo();
            boolean connected = networkInfo != null
                    && networkInfo.isAvailable() && networkInfo.isConnected() && networkInfo.isConnectedOrConnecting();
            return connected;
        } catch (Exception e) {
            Log.v("connectivity", e.toString());
        }
        return false;
    }

    public static boolean hasSDCard() {
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        return isSDPresent;
    }

    public static String getString(Context context, int id) {
        return context.getResources().getString(id);
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
