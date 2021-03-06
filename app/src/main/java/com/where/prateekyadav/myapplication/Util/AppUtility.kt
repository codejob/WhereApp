package com.where.prateekyadav.myapplication.Util

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.util.Log
import java.util.*
import android.content.ContentValues.TAG
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import com.where.prateekyadav.myapplication.modal.SearchResult
import com.where.prateekyadav.myapplication.search.network.RetroCallImplementor
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import com.where.prateekyadav.myapplication.*
import com.where.prateekyadav.myapplication.R.drawable.bg_round_corner_near_by
import com.where.prateekyadav.myapplication.R.drawable.bg_round_corner_original
import com.where.prateekyadav.myapplication.database.DatabaseHelper
import java.io.*


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

        fun showToastLong(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
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
        if ((!checkAlarmAlreadySet(applicationContext) || forceUpdate)
                && MySharedPref.getinstance(applicationContext).getBoolean(AppConstant.SP_KEY_APP_REACHED_MAIN_SCREEN)) {

            var pref: MySharedPref = MySharedPref.getinstance(applicationContext);
            pref.setLong(System.currentTimeMillis(), AppConstant.SP_KEY_LAST_TIMER_TIME)
            //10 seconds later
            val cal = Calendar.getInstance()
            cal.add(Calendar.SECOND, 2)


            val intent = Intent(applicationContext, AlarmReceiverLocation::class.java);
            intent.action = AppConstant.RECEIVER_ACTION

            val pendingIntent = PendingIntent.getBroadcast(applicationContext,
                    AppConstant.alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager = applicationContext!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
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
                var hanlder = LocationHelper.getInstance(context).Handleupdate()
                //LocationHelper.getInstance(context, DemoUpdate()).getCompleteAddressString(location!!, AppConstant.LOCATION_UPDATE_TYPE_LAST_KNOWN)
                //LocationHelper.getInstance(context, DemoUpdate()).getCompleteAddressString(location!!, AppConstant.LOCATION_UPDATE_TYPE_LAST_KNOWN)
                var retroCallImplementor = RetroCallImplementor()
                retroCallImplementor!!.getAllPlaces(location.latitude.toString() + "," + location.longitude.toString(), hanlder, location, location.provider, 0)

            }
        } catch (e: Exception) {
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

    fun validateAutoStartTimer(context: Context?, autoStart: Boolean): Boolean {
        try {
            var pref: MySharedPref = MySharedPref.getinstance(context);
            if (!pref.getBoolean(AppConstant.SP_KEY_APP_REACHED_MAIN_SCREEN)) {
                return false
            }
            val lastTimeStamp = pref.getLong(AppConstant.SP_KEY_LAST_TIMER_TIME)
            val diff = (System.currentTimeMillis() - lastTimeStamp)
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Last timer " + diff / 1000 + " seconds ago")
            if (diff > (AppConstant.LOCATION_SYNC_INSTERVAL * 3)) {
                startTimerAlarm(context, true)
                pref.setLong(System.currentTimeMillis(), AppConstant.SP_KEY_LAST_TIMER_TIME)
                if (autoStart && Build.BRAND.equals("xiaomi") && pref.getLong(AppConstant.SP_KEY_COUNTER_AUTO_START) < 3) {
                    val intent = Intent()
                    intent.setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                    context!!.startActivity(intent);
                    AppUtility.showToastLong(context, context.getString(R.string.str_auto_start_permission) + " " + context.getString(R.string.app_name))
                    //PermissionCheckHandler.goToSettings(context, context.getString(R.string.str_auto_start_permission) + context.getString(R.string.app_name))
                    pref.setLong(pref.getLong(AppConstant.SP_KEY_COUNTER_AUTO_START) + 1, AppConstant.SP_KEY_COUNTER_AUTO_START)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
        }
        return true
    }

    fun fetchLocationAfterLongDelay(context: Context?): Boolean {
        try {
            var pref: MySharedPref = MySharedPref.getinstance(context);
            if (!pref.getBoolean(AppConstant.SP_KEY_APP_REACHED_MAIN_SCREEN)) {
                return false
            }
            val lastTimeStamp = pref.getLong(AppConstant.SP_KEY_LAST_TIMER_TIME)
            val diff = (System.currentTimeMillis() - lastTimeStamp)
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Last timer " + diff / 1000 + " seconds ago")
            val locationHelper = LocationHelper.getInstance(context)
            if (locationHelper.checkLocationAvailable() && diff > (AppConstant.LOCATION_SYNC_INSTERVAL)) {
                locationHelper.fetchLocation(true)
                pref.setLong(System.currentTimeMillis(), AppConstant.SP_KEY_LAST_TIMER_TIME)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
        }
        return true
    }

    fun decorateFromAndToTime(from: Long, to: Long, context: Context?): String {
        var result = ""
        val calToTime = Calendar.getInstance();
        val calFromTime = Calendar.getInstance();
        calToTime.timeInMillis = to
        calFromTime.timeInMillis = from

        /*val fromSecond = calFromTime.get(Calendar.DAY_OF_MONTH)
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
        val toYear = calToTime.get(Calendar.YEAR)*/

        val formatter: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy");
        val fromString = formatter.format(from);

        val formatterTIme: SimpleDateFormat = SimpleDateFormat("hh:mm a");
        val fromTimeString = formatterTIme.format(from)
        val toTimeString = formatterTIme.format(to)


        result =
                context!!.getString(R.string.from) + " " + fromTimeString + " " + context!!.getString(R.string.to) + " " +
                toTimeString
        return result

    }

    fun getDecoratedDate(from: Long, context: Context?): String? {
        var result = ""
        val formatter: SimpleDateFormat = SimpleDateFormat("EEE, dd MMM yyyy");
        val fromString = formatter.format(from);
        result = context!!.getString(R.string.str_date) + " " + fromString
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


    fun showGpsOffNotification(context: Context?) {
        // Prepare intent which is triggered if the
        // notification is selected

        var pref: MySharedPref = MySharedPref.getinstance(context);

        val lastTimeStamp = pref.getLong(AppConstant.SP_KEY_LAST_NOTIFICATION_TIME)
        var diff = (System.currentTimeMillis() - lastTimeStamp)
        diff = diff / (1000 * 60 * 60)
        Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Last notification " + diff / 1000 * 60 * 60 + " hrs ago")
        if (diff > 3) {
            pref.setLong(System.currentTimeMillis(), AppConstant.SP_KEY_LAST_NOTIFICATION_TIME)

            var intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
            val pIntent = PendingIntent.getActivity(context, AppConstant.notificationRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Build notification
            // Actions are just fake
            val noti = Notification.Builder(context)
                    .setContentTitle(context!!.getString(R.string.location_turned_off))
                    .setContentText(context!!.getString(R.string.str_gps_notification_msg))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pIntent)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .addAction(R.mipmap.ic_launcher, "And more", pIntent).build();

            val notificationManager = context!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager;
            // hide the notification after its selected
            noti.flags = Notification.FLAG_AUTO_CANCEL

            notificationManager.notify(AppConstant.notificationRequestCode, noti);
        }

    }

    fun buildAlertMessageNoGps(context: Context?): AlertDialog {
        var builder: AlertDialog.Builder = AlertDialog.Builder(context!!, R.style.AlertDialogStyle);
        builder.setMessage(context.getString(R.string.msg_ask_to_turn_gps_on))
                .setCancelable(false)
                .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, id: Int) {
                        AppUtility.showToastLong(context, context.getString(R.string.msg_toast_choose_battery_saving))
                        context!!.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, id: Int) {
                        dialog.cancel();
                    }
                });
        val alert: AlertDialog = builder.create()
        alert.show();
        return alert
    }

    fun setColorBasedOnAccuracy(accuracy: Float, view: TextView, context: Context?): Int {
        when (true) {

            (accuracy < 30) -> {
                view.background = context!!.resources.getDrawable(bg_round_corner_original)
                view.setText(" " + context!!.resources.getString(R.string.str_original_location) + " ")
                view.setTextColor(Color.WHITE)
                return R.color.color_location_green
            }
            (accuracy > 30 && accuracy < 150) -> {
                view.background = context!!.resources.getDrawable(bg_round_corner_near_by)
                view.setText(" " + context!!.resources.getString(R.string.str_approx_location) + " ")
                view.setTextColor(Color.GRAY)

                return R.color.color_location_yellow
            }
            (accuracy > 150) -> {
                view.setText(" " + context!!.resources.getString(R.string.str_approx_location) + " ")
                view.setTextColor(Color.GRAY)
                view.background = context!!.resources.getDrawable(bg_round_corner_near_by)
                return R.color.color_location_red
            }
        }
        return R.color.color_location_green
    }

    fun getStaticMapUrl(lat: String, lng: String): String {
        return "http://maps.google.com/maps/api/staticmap?center=" + lat + "," + lng + "&zoom=16&size=400x400&sensor=true" +
                "&markers=color:red%7Clabel:L%7C$lat,$lng&key=${AppConstant.KEY_MAP}"

    }

    fun copyDataBase(context: Context?) {
        Log.i("Database",
                "New database is being copied to device!")
        val buffer = ByteArray(1024)
        var myOutput: OutputStream? = null
        var length: Int
        // Open your local db as the input stream
        var myInput: InputStream? = null
        var cw: ContextWrapper = ContextWrapper(context!!.getApplicationContext())
        var DB_PATH = cw.getFilesDir().getAbsolutePath() + "/databases/"; //edited to databases
        try {
            val DB_NAME = "Rohitashv.db"
            myInput = context.getAssets().open(DB_NAME)
            // transfer bytes from the inputfile to the
            // outputfile
            //myOutput = FileOutputStream(DB_PATH + DB_NAME)
            myOutput = FileOutputStream("/data/data/" + context!!.getPackageName() + "/databases/" + DatabaseHelper.DATABASE_NAME)

            do {
                length = myInput.read(buffer)
                if (length > 0) {
                    myOutput.write(buffer, 0, length)
                }
            } while (length > 0)
            myOutput!!.close()
            myOutput!!.flush()
            myInput!!.close()
            Log.i("Database",
                    "New database has been copied to device!")


        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}