package com.where.prateekyadav.myapplication.Util

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.where.prateekyadav.myapplication.AlarmReceiverLocation
import java.io.File
import java.util.*
import android.content.ContentValues.TAG
import android.graphics.Typeface
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.widget.Toast
import com.where.prateekyadav.myapplication.LocationHelper
import com.where.prateekyadav.myapplication.R
import com.where.prateekyadav.myapplication.UpdateLocation
import com.where.prateekyadav.myapplication.database.VisitedLocationInformation
import com.where.prateekyadav.myapplication.modal.SearchResult
import com.where.prateekyadav.myapplication.search.model.placesdetails.Result
import com.where.prateekyadav.myapplication.search.network.RetroCallImplementor
import com.where.prateekyadav.myapplication.search.network.RetroCallIneractor
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList
import android.support.design.widget.Snackbar
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View


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

    companion object {
        fun showToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
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

    fun startTimerAlarm(applicationContext: Context?, forceUpdate: Boolean) {
        if (!checkAlarmAlreadySet(applicationContext) || forceUpdate) {

            var pref: MySharedPref = MySharedPref.getinstance(applicationContext);
            pref.setLong(System.currentTimeMillis(), AppConstant.SP_KEY_LAST_TIMER_TIME)
            //10 seconds later
            val cal = Calendar.getInstance()
            cal.add(Calendar.SECOND, 5)

            val calTest = Calendar.getInstance()
            calTest.add(Calendar.SECOND, 60)

            val intent = Intent(applicationContext, AlarmReceiverLocation::class.java);
            intent.action = AppConstant.RECEIVER_ACTION

            val pendingIntent = PendingIntent.getBroadcast(applicationContext,
                    AppConstant.alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager = applicationContext!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    calTest.getTimeInMillis(), pendingIntent);
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
    fun insertDemoLocation(context: Context?) {
        try {
            val latArr = AppConstant.latArray;
            val lngArr = AppConstant.lngArray;
            val locationList = ArrayList<Location>();
            for (i in latArr.indices) {
                var location = Location(LocationManager.GPS_PROVIDER);
                location.latitude = latArr[i]
                location.longitude = lngArr[i]
                location.accuracy = 50F
                locationList.add(location)
                var hanlder = LocationHelper.getInstance(context, DemoUpdate()).Handleupdate()
                //LocationHelper.getInstance(context, DemoUpdate()).getCompleteAddressString(location!!, AppConstant.LOCATION_UPDATE_TYPE_LAST_KNOWN)
                //LocationHelper.getInstance(context, DemoUpdate()).getCompleteAddressString(location!!, AppConstant.LOCATION_UPDATE_TYPE_LAST_KNOWN)
                var retroCallImplementor = RetroCallImplementor()
                retroCallImplementor!!.getAllPlaces(location.latitude.toString() + "," + location.longitude.toString(), hanlder, location, location.provider, 0)

            }
        } catch (e: Exception) {
        }

    }


    class DemoUpdate() : UpdateLocation {

        override fun updateLocationAddressList(addressList: List<SearchResult>) {
        }

    }
    ///////////////////////////Need to delete//////////////////////////////////


    // Send an Intent with an action named "custom-event-name". The Intent
    // sent should
    // be received by the ReceiverActivity.
    public fun sendUpdateMessage(context: Context?) {
        try {
            Log.d("sender", "Broadcasting message")
            val intent = Intent()
            intent.setAction(AppConstant.INTENT_FILTER_UPDATE_LOCATION)
            // You can also include some extra data.
            intent.putExtra("message", "Test Message")
            intent.putExtra(AppConstant.LOCATION_UPDATE_MESSAGE, true);
            context!!.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun validateAutoStartTimer(context: Context?) {
        try {
            var pref: MySharedPref = MySharedPref.getinstance(context);
            val lastTimeStamp = pref.getLong(AppConstant.SP_KEY_LAST_TIMER_TIME)
            val diff = (System.currentTimeMillis() - lastTimeStamp)
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Last timer " + diff / 1000 + " seconds ago")
            if (diff > (AppConstant.LOCATION_SYNC_INSTERVAL * 3)) {
                startTimerAlarm(context, true)
                if (Build.BRAND.equals("xiaomi") && pref.getLong(AppConstant.SP_KEY_COUNTER_AUTO_START) < 3) {
                    val intent = Intent()
                    intent.setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                    context!!.startActivity(intent);
                    showToast(context, "Please enable Auto Start permission for " + context.getString(R.string.app_name))
                    pref.setLong(pref.getLong(AppConstant.SP_KEY_COUNTER_AUTO_START) + 1, AppConstant.SP_KEY_COUNTER_AUTO_START)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            var pref: MySharedPref = MySharedPref.getinstance(context);
            pref.setLong(System.currentTimeMillis(), AppConstant.SP_KEY_LAST_TIMER_TIME)


        }
    }

    fun decorateFromAndToTime(from: Long, to: Long): String {
        var result = ""
        val calToTime = Calendar.getInstance();
        val calFromTime = Calendar.getInstance();
        calToTime.timeInMillis = to
        calFromTime.timeInMillis = from

        val fromSecond = calFromTime.get(Calendar.DAY_OF_MONTH)
        val fromMinute = calFromTime.get(Calendar.MINUTE)
        val fromHour = calFromTime.get(Calendar.HOUR_OF_DAY)
        val fromDay = calFromTime.get(Calendar.DAY_OF_MONTH)
        val fromMonth = calFromTime.get(Calendar.MONTH)
        val fromYear = calFromTime.get(Calendar.YEAR)

        val toSecond = calToTime.get(Calendar.DAY_OF_MONTH)
        val toMinute = calToTime.get(Calendar.MINUTE)
        val toHour = calToTime.get(Calendar.HOUR_OF_DAY)
        val toDay = calToTime.get(Calendar.DAY_OF_MONTH)
        val toMonth = calToTime.get(Calendar.MONTH)
        val toYear = calToTime.get(Calendar.YEAR)

        val formatter: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy");
        val fromString = formatter.format(from);

        val formatterTIme: SimpleDateFormat = SimpleDateFormat("hh:mm a");
        val fromTimeString = formatterTIme.format(from)
        val toTimeString = formatterTIme.format(to)


        result = """
            Date: ${fromString}
            From ${fromTimeString} To ${toTimeString}
                     """.trimMargin()


        return result

    }

    fun showSnackBar(message: String, layout: View) {
        val snackbar = Snackbar
                .make(layout, message, Snackbar.LENGTH_LONG)

        snackbar.show()
    }

    fun makeSectionOfTextBold(text: String, textToBold: String): SpannableStringBuilder {
        val builder = SpannableStringBuilder(text);
        if (textToBold.length > 0 && !textToBold.trim().equals("")) {
            //for counting start/end indexes
            val testText: String = text.toLowerCase(Locale.US);
            val testTextToBold = textToBold.toLowerCase(Locale.US);
            val startingIndex = testText.indexOf(testTextToBold);
            val endingIndex = startingIndex + testTextToBold.length;

            if (startingIndex >= 0 && endingIndex >= 0) {
                builder.setSpan(StyleSpan(Typeface.BOLD), startingIndex, endingIndex, 0);
            }
        }

        return builder;
    }
}