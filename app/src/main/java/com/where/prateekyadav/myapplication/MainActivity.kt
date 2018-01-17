package com.where.prateekyadav.myapplication

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.content.*
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.where.prateekyadav.myapplication.Util.AppUtility
import com.where.prateekyadav.myapplication.Util.Constant
import com.where.prateekyadav.myapplication.database.DatabaseHelper
import com.where.prateekyadav.myapplication.database.VisitedLocationInformation


class MainActivity : AppCompatActivity(), UpdateLocation {
    override fun updateLocationAddress(address: String) {
    }

    override fun updateLocationAddressList(addressList: List<VisitedLocationInformation>) {
        setLocation(addressList)
    }

    val RQS_1 = 1
    var mLocationHelper: LocationHelper? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mLocationHelper = LocationHelper(applicationContext, this);
        checkLocationPermission()
        DatabaseHelper(this).copyDataBaseToSDCard()

    }


    val MY_PERMISSIONS_REQUEST_LOCATION = 99

    fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                        .setTitle("Title")
                        .setMessage("Location")
                        .setPositiveButton("ok", DialogInterface.OnClickListener { dialogInterface, i ->
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(this@MainActivity,
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    MY_PERMISSIONS_REQUEST_LOCATION)
                        })
                        .create()
                        .show()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_LOCATION)
            }
            return false
        } else {
            return true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        //mLocationHelper?.getLocation();
                        AppUtility().startTimerAlarm(applicationContext)
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setLocation(DatabaseHelper(this).readAllVisitedLocation())
            //mLocationHelper?.getLocation()
            if (!AppUtility().checkAlarmAlreadySet(this)) {
                AppUtility().startTimerAlarm(this);
            }
        }
        registerReceiver()

    }

    override fun onPause() {
        super.onPause()
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //locationManager!!.removeUpdates()
        }
    }


    fun setLocation(address: List<VisitedLocationInformation>) {
        if (address != null)
            list.adapter = LocationsAdapter(this, address)

    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            Log.d("Receiver", "Receive message")
            val message = intent.getStringExtra("message")
            val isUpdated = intent.getBooleanExtra(Constant.LOCATION_UPDATE_MESSAGE, false);
            if (isUpdated) {

                runOnUiThread(Runnable {
                    updateLocationAddressList(
                            DatabaseHelper(this@MainActivity).readAllVisitedLocation())

                });

            }
        }
    }

    override fun onStop() {
        unregisterReceiver()
        super.onStop()
    }

    private fun unregisterReceiver() {
        try {
            unregisterReceiver(mMessageReceiver);
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver()

    }

    override fun onRestart() {
        super.onRestart()
    }

    private fun registerReceiver() {
        try {
            registerReceiver(
                    mMessageReceiver, IntentFilter(Constant.INTENT_UPDATE_LOCATION))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
