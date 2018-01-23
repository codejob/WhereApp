package com.where.prateekyadav.myapplication.Util

import android.os.Environment


/**
 * Created by Infobeans on 1/11/2018.
 */
open class AppConstant {
    companion object {
        var sharedKeyLatitude: String = "latitude";
        var sharedKeyLongitude: String = "longitude";
        var SP_KEY_SPENT_TIME: String = "sp_key_spent_time"
        var SP_KEY_FIRST_TIME: String = "sp_key_first_time"
        var SP_KEY_ACCURACY: String = "sp_key_accuracy";
        var SP_KEY_LAST_TIMER_TIME: String = "sp_key_last_timer_time";
        val FOLDER_PATH = Environment.getExternalStorageDirectory().path + "/WhereApp/"
        val TAG_KOTLIN_DEMO_APP = "KotlinDemoApp"
        // Seconds

        val LOCATION_SYNC_INSTERVAL: Long = 40 * 1000;
        val LOCATION_SYNC_TIMEOUT: Long = 30 * 1000;
        val RECEIVER_ACTION: String = "LOCATION";
        var MIN_DISTANCE_RANGE: Int = 200;
        var RADIUS_NEARBY_SEARCH: Int = 50;
        val alarmID: Int = 1001;
        //
        val INTENT_FILTER_UPDATE_LOCATION: String = "update_location";
        val INTENT_FILTER_INTERNET_CONNECTION: String = "internet_connection";
        val LOCATION_UPDATE_MESSAGE: String = "update_location_message";
        val KEY_IS_NETWORK_CONNECTED: String = "is_internet_connected"
        //
        val LOCATION_UPDATE_TYPE_LAST_KNOWN: String = "last_known";
        val LOCATION_UPDATE_TYPE_CURRENT: String = "current";
        val latArray = arrayOf(24.585445, 24.602968, 15.557049)
        val lngArray = arrayOf(73.712479, 73.685511, 73.754851)


    }

}