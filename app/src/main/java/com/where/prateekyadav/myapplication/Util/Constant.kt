package com.where.prateekyadav.myapplication.Util

import android.os.Environment


/**
 * Created by Infobeans on 1/11/2018.
 */
open class Constant {
    companion object {
        var sharedKeyLatitude: String = "latitude";
        var sharedKeyLongitude: String = "longitude";
        var sharedKeyLong: String = "long";
        val FOLDER_PATH = Environment.getExternalStorageDirectory().path + "/WhereApp/"
        val E_WORKBOOK_DEBUG_TAG = "eWorkBook-"
        // Seconds
        val LOCATION_SYNC_INSTERVAL: Long = 10*60 * 1000;
        val LOCATION_SYNC_TIMEOUT: Long = 30 * 1000;
        val RECEIVER_ACTION: String = "LOCATION";
        var MIN_DISTANCE_RANGE: Int = 200;
        val alarmID: Int = 1001;


    }

}