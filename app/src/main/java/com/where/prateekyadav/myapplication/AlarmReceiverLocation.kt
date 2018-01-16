package com.where.prateekyadav.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.where.prateekyadav.myapplication.Util.AppUtility
import com.where.prateekyadav.myapplication.database.VisitedLocationInformation


/**
 * Created by Infobeans on 1/10/2018.
 *
 */
class AlarmReceiverLocation : BroadcastReceiver(), UpdateLocation {
    override fun updateLocationAddressList(addressList: List<VisitedLocationInformation>) {


        /*if (addressList != null && addressList.size > 0)
            Toast.makeText(mContext,
                    addressList.get(0).address,
                    Toast.LENGTH_LONG).show();*/
    }

    var mContext: Context? = null;
    override fun updateLocationAddress(address: String) {

    }

    override fun onReceive(context: Context?, intent: Intent?) {
        mContext = context;
        LocationHelper(context, this).getLocation();

        /*Toast.makeText(context,"AlarmReceiver.onReceive()",
                Toast.LENGTH_LONG).show();*/
    }
}