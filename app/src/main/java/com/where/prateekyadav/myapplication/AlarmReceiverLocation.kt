package com.where.prateekyadav.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.where.prateekyadav.myapplication.Util.AppUtility
import com.where.prateekyadav.myapplication.Util.MySharedPref
import com.where.prateekyadav.myapplication.modal.SearchResult


/**
 * Created by Infobeans on 1/10/2018.
 *
 */
class AlarmReceiverLocation : BroadcastReceiver() {


    var mContext: Context? = null;

    override fun onReceive(context: Context?, intent: Intent?) {
        mContext = context;
        var action: String = intent!!.action
        if (action != null && action.equals(android.content.Intent.ACTION_BOOT_COMPLETED, false)) {
            //AppUtility.showToast(context!!, "ACTION_BOOT_COMPLETED")
            AppUtility().startTimerAlarm(context, true)
        } else if (action != null && action.equals(android.location.LocationManager.PROVIDERS_CHANGED_ACTION, false)) {
            //AppUtility.showToast(context!!, "ACTION_PROVIDER_CHANGED")
            AppUtility().fetchLocationAfterLongDelay(context)
        } else {
            //AppUtility.showToast(context!!, "Receive")
            var pref: MySharedPref = MySharedPref.getinstance(mContext);
            pref.setLong(System.currentTimeMillis(), AppConstant.SP_KEY_LAST_TIMER_TIME)
            LocationHelper.getInstance(context).fetchLocation(false);
        }


    }


}