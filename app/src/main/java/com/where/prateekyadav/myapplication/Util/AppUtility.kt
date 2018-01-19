package com.where.prateekyadav.myapplication.Util

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.where.prateekyadav.myapplication.AlarmReceiverLocation
import java.io.File
import java.util.*
import android.content.ContentValues.TAG
import android.location.Location
import android.location.LocationManager
import com.where.prateekyadav.myapplication.LocationHelper
import com.where.prateekyadav.myapplication.UpdateLocation
import com.where.prateekyadav.myapplication.database.VisitedLocationInformation
import kotlin.collections.ArrayList


/**
 * Created by Infobeans on 1/12/2018.
 */
class AppUtility {

    // Creates all directories needed to the specified location
    fun makeDirs(location: String): File {
        val dir = File(location)
        dir.mkdirs()
        return dir
    }

    fun checkStoragePermissions(context: Context?): Boolean {
        Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Checking permissions.")

        // Verify that all required Storage permissions have been granted.
        if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Storage permissions have not been granted.
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "permissions has NOT been granted. Requesting permissions.")
            return false
        } else {
            // Storage permissions have been granted. Show the contacts fragment.
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                    "storage permissions have already been granted.")
            return true
        }
    }

    fun startTimerAlarm(applicationContext: Context?) {
        if (!checkAlarmAlreadySet(applicationContext)) {
            //10 seconds later
            val cal = Calendar.getInstance()
            cal.add(Calendar.SECOND, 5)

            val intent = Intent(applicationContext, AlarmReceiverLocation::class.java);
            intent.action = AppConstant.RECEIVER_ACTION

            val pendingIntent = PendingIntent.getBroadcast(applicationContext,
                    AppConstant.alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager = applicationContext!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            //alarmManager.set(AlarmManager.RTC_WAKEUP,
            //        cal.getTimeInMillis(), pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(), AppConstant.LOCATION_SYNC_INSTERVAL, pendingIntent);

            /*Toast.makeText(applicationContext,
                    "call alarmManager.set()",
                    Toast.LENGTH_LONG).show();*/
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                    "TImer set")
        }
    }

    fun checkAlarmAlreadySet(context: Context?): Boolean {
        val intent = Intent(context, AlarmReceiverLocation::class.java)
        intent.action = AppConstant.RECEIVER_ACTION//the same as up
        val isWorking = PendingIntent.getBroadcast(context, AppConstant.alarmID, intent, PendingIntent.FLAG_NO_CREATE) != null//just changed the flag
        Log.d(TAG, "alarm is " + (if (isWorking) "" else "not") + " working...")
        return isWorking;
    }

    ///////////////////////////// Need to delete/////////////////////////
    fun inssertDemoLocation(context: Context?) {
        try {
            val latArr = AppConstant.latArray;
            val lngArr = AppConstant.lngArray;
            val locationList = ArrayList<Location>();
            for (i in latArr.indices) {
                var location = Location(LocationManager.GPS_PROVIDER);
                location.latitude = latArr[i]
                location.longitude = lngArr[i]
                locationList.add(location)
                LocationHelper.getInstance(context, DemoUpdate()).getCompleteAddressString(location!!, AppConstant.LOCATION_UPDATE_TYPE_LAST_KNOWN)
            }
        } catch (e: Exception) {
        }

    }

    class DemoUpdate() : UpdateLocation {
        override fun updateLocationAddress(address: String) {
        }

        override fun updateLocationAddressList(addressList: List<VisitedLocationInformation>) {
        }

    }
    ///////////////////////////Need to delete//////////////////////////////////
}