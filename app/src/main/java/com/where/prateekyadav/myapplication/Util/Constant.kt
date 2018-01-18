package com.where.prateekyadav.myapplication.Util

import android.os.Environment


/**
 * Created by Infobeans on 1/11/2018.
 */
open class Constant {
    companion object {
        var sharedKeyLatitude: String = "latitude";
        var sharedKeyLongitude: String = "longitude";
        var SP_KEY_SPENT_TIME: String = "sp_key_spent_time";
        val FOLDER_PATH = Environment.getExternalStorageDirectory().path + "/WhereApp/"
        val E_WORKBOOK_DEBUG_TAG = "eWorkBook-"
        // Seconds
        val LOCATION_SYNC_INSTERVAL: Long =  10* 60 * 1000;
        val LOCATION_SYNC_TIMEOUT: Long = 40 *1000;
        val RECEIVER_ACTION: String = "LOCATION";
        var MIN_DISTANCE_RANGE: Int = 200;
        val alarmID: Int = 1001;
        val INTENT_UPDATE_LOCATION: String = "update_location";
        val LOCATION_UPDATE_MESSAGE: String = "update_location_message";
        val LOCATION_UPDATE_TYPE_LAST_KNOWN: String = "last_known";
        val LOCATION_UPDATE_TYPE_CURRENT: String = "current";

    }

}