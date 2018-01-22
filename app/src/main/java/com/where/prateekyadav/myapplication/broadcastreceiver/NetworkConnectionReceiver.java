package com.where.prateekyadav.myapplication.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.widget.Toast;

import com.where.prateekyadav.myapplication.Services.AddressUpdateService;


/**
 *Created by Infobeans on 12/10/2015.
 */
public class NetworkConnectionReceiver extends BroadcastReceiver {

    private int ISONLINE=0;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Toast.makeText(context,"change state",Toast.LENGTH_LONG).show();

        NetworkInfo info =  intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        Toast.makeText(context,info.getState().toString(),Toast.LENGTH_LONG).show();

        if(info != null && info.isConnected()) {
            if (info.getState()==NetworkInfo.State.CONNECTED && ISONLINE!=1) {
                updateNetworkConnection(context, info.isConnected());
                ISONLINE=1;
                // Start  service for form sync
                runCodeAfterSomeDelay(context);
            }
            if (info.getTypeName().equalsIgnoreCase("WIFI")) {
                // Do your work.
                // e.g. To check the Network Name or other info:
                WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ssid = wifiInfo.getSSID();
                // TODO : When ever wifi connection established need to check remaining add video
            }
            else if (info.getTypeName().equalsIgnoreCase("MOBILE")) {
               // TODO : Mobile data Connection send impression info when ever connected

            }
       }// for network disconnected
        if (info != null){
            NetworkInfo.State state = info.getState();
            if(state == NetworkInfo.State.DISCONNECTED && ISONLINE!=0)
            {
                // TODO Code for network disconnected
                updateNetworkConnection(context,info.isConnected());
                ISONLINE=0;
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
    //
    // This function will create an intent. This intent must take as parameter the "unique_name" that you registered your activity with
    static void updateNetworkConnection(Context context, Boolean isConnected) {
        Intent intent = new Intent("Hello");
        //put whatever data you want to send, if any
        intent.putExtra("KEY",isConnected);
        //send broadcast
        context.sendBroadcast(intent);
        //
    }

    /**
     *
     * @param context
     */
    private void runCodeAfterSomeDelay(final Context context){
        try {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    startSyncedDraftService(context);
                }
            }, 1500);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Method to start service to synced the draft forms
     *
     * @param context
     */
    private void startSyncedDraftService(Context context) {
        Intent serviceIntent =
                new Intent(context, AddressUpdateService.class);
        context.startService(serviceIntent);
    }
}

