package com.where.prateekyadav.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.where.prateekyadav.myapplication.Util.MySharedPref
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
        Toast.makeText(context, "Receive",
                Toast.LENGTH_SHORT).show()
        var pref:MySharedPref = MySharedPref.getinstance(mContext);
        pref.setLong(System.currentTimeMillis(),AppConstant.SP_KEY_LAST_TIMER_TIME)

        LocationHelper.getInstance(context, this).setLocationListener();
    }


}