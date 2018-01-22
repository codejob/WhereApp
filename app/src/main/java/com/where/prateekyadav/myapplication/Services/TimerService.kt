package com.where.prateekyadav.myapplication.Services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import java.util.*
import android.widget.Toast
import android.R.string.cancel
import android.util.Log
import com.where.prateekyadav.myapplication.LocationHelper
import com.where.prateekyadav.myapplication.UpdateLocation
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.where.prateekyadav.myapplication.modal.SearchResult
import java.text.SimpleDateFormat
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Message
import com.where.prateekyadav.myapplication.AlarmReceiverLocation


/**
 * Created by Infobeans on 1/22/2018.
 */
class TimerService : Service(), UpdateLocation {

    // constant
    companion object {
        var running: Boolean = false
    }

    // run on another Thread to avoid crash
    private var mHandler = Handler()
    // timer handling
    private var mTimer: Timer? = null
    var listHandler: Handler? = null;


    override fun onBind(p0: Intent?): IBinder {
        return null!!
    }


    override fun onCreate() {
        //
        Log.d("service", "onCreate")
        Toast.makeText(applicationContext, "Hit",
                Toast.LENGTH_SHORT).show()
        listHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == 0) {
                    LocationHelper.getInstance(this@TimerService, this@TimerService).setLocationListener();
                    // display toast

                    sendEmptyMessageDelayed(0, AppConstant.LOCATION_SYNC_INSTERVAL)
                }
                super.handleMessage(msg)
            }
        }
        (listHandler as Handler).sendEmptyMessage(0)

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        running = true
        Log.d("service", "onStartCommand")

        return Service.START_STICKY
    }

    internal inner class TimeDisplayTimerTask : TimerTask() {

        private// get date time in custom format
        val dateTime: String
            get() {
                val sdf = SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]")
                return sdf.format(Date())
            }

        override fun run() {
            // run on another thread
            mHandler.post {
                LocationHelper.getInstance(this@TimerService, this@TimerService).setLocationListener();
                // display toast
                Toast.makeText(applicationContext, dateTime,
                        Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun updateLocationAddressList(addressList: List<SearchResult>) {
    }

    override fun onTrimMemory(level: Int) {
        val myIntent = Intent(applicationContext, AlarmReceiverLocation::class.java)
        //sendBroadcast(myIntent)
        //startServiceAgain()
        Log.d("service", "onTrimMemory")
        super.onTrimMemory(level)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.d("service", "onLowMemory")
    }

    override fun onDestroy() {
        super.onDestroy()
        val myIntent = Intent(applicationContext, AlarmReceiverLocation::class.java)
        //sendBroadcast(myIntent)
        Log.d("service", "onDestroy")

    }


    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        val myIntent = Intent(applicationContext, AlarmReceiverLocation::class.java)
        sendBroadcast(myIntent)
        Log.d("service", "onTaskRemoved")


    }

    fun startServiceAgain() {
        val myIntent = Intent(applicationContext, TimerService::class.java)

        val pendingIntent = PendingIntent.getService(applicationContext, 0, myIntent, 0)

        val alarmManager1 = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance()

        calendar.timeInMillis = System.currentTimeMillis()

        calendar.add(Calendar.SECOND, 10)

        alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(applicationContext, "Start Alarm", Toast.LENGTH_SHORT).show()
    }


}