package com.where.prateekyadav.myapplication.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Handler

import com.where.prateekyadav.myapplication.Services.AddressUpdateService
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.where.prateekyadav.myapplication.Util.AppUtility


/**
 * Created by Infobeans on 12/10/2015.
 */
class NetworkConnectionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val info = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
        AppUtility.showToast(context, info.state.toString())
        if (info != null && info.isConnected) {
            if (info.state == NetworkInfo.State.CONNECTED && ISONLINE != 1) {
                updateNetworkConnection(context, info.isConnected)
                ISONLINE = 1
                // Start  service for form sync
                runCodeAfterSomeDelay(context)
            }
            if (info.typeName.equals("WIFI", ignoreCase = true)) {
                // Do your work.
                // e.g. To check the Network Name or other info:
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                val ssid = wifiInfo.ssid
                // TODO : When ever wifi connection established need to check remaining add video
            } else if (info.typeName.equals("MOBILE", ignoreCase = true)) {
                // TODO : Mobile data Connection send impression info when ever connected

            }
        }// for network disconnected
        if (info != null) {
            val state = info.state
            if (state == NetworkInfo.State.DISCONNECTED && ISONLINE != 0) {
                // TODO Code for network disconnected
                updateNetworkConnection(context, info.isConnected)
                ISONLINE = 0
            }
        }
        /*  // Make sure it's an event we're listening for ...
        if (!intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) &&
                !intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION) &&
                !intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            return;

        }
        ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (cm == null) {
            return;
        }
        // Now to check if we're actually connected
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
            // Start the service to do our thing
            Toast.makeText(context, "Start service", Toast.LENGTH_SHORT).show();
        }*/


    }

    /**
     *
     * @param context
     */
    private fun runCodeAfterSomeDelay(context: Context) {
        try {
            val handler = Handler()
            handler.postDelayed({
                //Do something after 100ms
                startAddressUpdateService(context)
            }, 1500)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Method to start service to synced the draft forms
     *
     * @param context
     */
    private fun startAddressUpdateService(context: Context) {
        val serviceIntent = Intent(context, AddressUpdateService::class.java)
        context.startService(serviceIntent)
    }

    companion object {
        internal var ISONLINE = 0
        //
        // This function will create an intent. This intent must take as parameter the "unique_name" that you registered your activity with
        internal fun updateNetworkConnection(context: Context, isConnected: Boolean?) {
            val intent = Intent(AppConstant.INTENT_FILTER_INTERNET_CONNECTION);
            //put whatever data you want to send, if any
            intent.putExtra(AppConstant.KEY_IS_NETWORK_CONNECTED, isConnected);
            //send broadcast
            context.sendBroadcast(intent);
            //
        }
    }
}

