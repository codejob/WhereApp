package com.where.prateekyadav.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.where.prateekyadav.myapplication.Util.AppUtility
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
    override fun updateLocationAddress(address: String) {

    }

    override fun onReceive(context: Context?, intent: Intent?) {
        mContext = context;
        LocationHelper.getInstance(context, this).setLocationListener();
    }


}