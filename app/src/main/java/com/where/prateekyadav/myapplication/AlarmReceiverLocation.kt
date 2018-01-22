package com.where.prateekyadav.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import com.where.prateekyadav.myapplication.Services.TimerService
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.where.prateekyadav.myapplication.Util.AppUtility
import com.where.prateekyadav.myapplication.Util.MySharedPref
import com.where.prateekyadav.myapplication.database.VisitedLocationInformation
import com.where.prateekyadav.myapplication.modal.SearchResult


/**
 * Created by Infobeans on 1/10/2018.
 *
 */
class AlarmReceiverLocation : BroadcastReceiver(), UpdateLocation {

    override fun updateLocationAddressList(addressList: List<SearchResult>) {
        //AppUtility().sendUpdateMessage(mContext!!);
    }

    var mContext: Context? = null;

    override fun onReceive(context: Context?, intent: Intent?) {
        mContext = context;
       // mContext!!.startService(Intent(mContext, TimerService::class.java));
        Toast.makeText(context, "Receive",
                Toast.LENGTH_SHORT).show()
        var pref:MySharedPref = MySharedPref.getinstance(mContext);
        pref.setLong(System.currentTimeMillis(),AppConstant.SP_KEY_LAST_TIMER_TIME)
       /* try {
            context!!.startService(Intent(context, TimerService::class.java));
        } catch (e: Exception) {
            e.printStackTrace()
        }*/

        /*var listHandler = object : Handler() {
             override fun handleMessage(msg: Message) {
                 if (msg.what == 0) {

                 }
                 super.handleMessage(msg)
             }
         }
         if(!TimerService.running)
         (listHandler as Handler).sendEmptyMessageDelayed(0,5)*/


        LocationHelper.getInstance(context, this).setLocationListener();
    }


}