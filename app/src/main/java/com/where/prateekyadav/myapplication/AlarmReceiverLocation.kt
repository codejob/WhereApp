package com.where.prateekyadav.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import com.where.prateekyadav.myapplication.Util.AppUtility
import com.where.prateekyadav.myapplication.Util.Constant
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
        LocationHelper(context, this).setLocationListener();

        /*Toast.makeText(context,"AlarmReceiver.onReceive()",
                Toast.LENGTH_LONG).show();*/
        sendMessage()
    }

    // Send an Intent with an action named "custom-event-name". The Intent
    // sent should
    // be received by the ReceiverActivity.
    private fun sendMessage() {
        Log.d("sender", "Broadcasting message")
        val intent = Intent("custom-event-name")
        // You can also include some extra data.
        intent.putExtra("message", "Test Message")
        intent.putExtra(Constant.LOCATION_UPDATE_MESSAGE,true);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent)
    }
}