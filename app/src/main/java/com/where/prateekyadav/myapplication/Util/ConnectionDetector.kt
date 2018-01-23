package com.where.prateekyadav.myapplication.Util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * Created by Infobeans on 12/9/2015.
 */
class ConnectionDetector private constructor(context: Context) {

    /**
     * Checking for all possible internet providers
     */
    /*  ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;
        *///
    val isConnectingToInternet: Boolean
        get() {
            val cm = _context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val activeNetwork = cm.activeNetworkInfo

            return activeNetwork != null && activeNetwork.isConnectedOrConnecting

        }

    /**
     * Check Network connection is through WIFI
     * @return
     */
    val isConnectedViaWifi: Boolean
        get() {
            val connectivityManager = _context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            return mWifi.isConnected
        }

    init {
        _context = context
    }

    companion object {

        private var _context: Context? = null
        private var sConnectionDetector: ConnectionDetector? = null

        fun getInstance(context: Context): ConnectionDetector {
            if (sConnectionDetector == null) {
                sConnectionDetector = ConnectionDetector(context)
            }
            return sConnectionDetector!!
        }
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = _context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}
