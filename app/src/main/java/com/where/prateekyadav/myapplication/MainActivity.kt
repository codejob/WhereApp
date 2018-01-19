package com.where.prateekyadav.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.content.*
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.Status
import com.where.prateekyadav.myapplication.Util.AppUtility
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.where.prateekyadav.myapplication.database.VisitedLocationInformation
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.common.api.GoogleApiClient
import com.where.prateekyadav.myapplication.Util.PermissionCheckHandler
import com.where.prateekyadav.myapplication.database.DataBaseController
import com.where.prateekyadav.myapplication.database.DatabaseHelper
import com.where.prateekyadav.myapplication.modal.SearchResult


class MainActivity : AppCompatActivity(), UpdateLocation, GoogleApiClient.OnConnectionFailedListener, PlaceSelectionListener {


    val RQS_1 = 1
    var mLocationHelper: LocationHelper? = null;
    var autocompleteFragment: PlaceAutocompleteFragment? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mLocationHelper = LocationHelper.getInstance(applicationContext, this);
        if (PermissionCheckHandler.checkNetWorkPermissions(this)){
            checkLocationPermission()
        }else{
            PermissionCheckHandler.verifyLocationPermissions(this)
        }
        //
        DataBaseController(this).copyDataBaseToSDCard()
        setAutoCompleteView()
    }

    @SuppressLint("ResourceType")
    fun setAutoCompleteView() {
        autocompleteFragment = fragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as PlaceAutocompleteFragment
        autocompleteFragment!!.setOnPlaceSelectedListener(this)
        var clear: ImageButton = autocompleteFragment!!.getView().findViewById(R.id.place_autocomplete_clear_button)
        clear.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                // example : way to access view from PlaceAutoCompleteFragment
                // ((EditText) autocompleteFragment.getView()
                // .findViewById(R.id.place_autocomplete_search_input)).setText("");
                autocompleteFragment!!.setText("")
                view.setVisibility(View.GONE)
                setLocation(DataBaseController(this@MainActivity).readAllVisitedLocation())
            }
        })

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
                        //
                        PermissionCheckHandler.verifyNetWorkPermissions(this@MainActivity)

                        AppUtility().startTimerAlarm(applicationContext)
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                       return
            }
        //
            PermissionCheckHandler.REQUEST_NETWORK_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {

                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }

    }

    @SuppressLint("ResourceType")
    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            var clear: EditText = autocompleteFragment!!.getView().findViewById(R.id.place_autocomplete_search_input)
            if (clear != null &&
                    clear.text.isBlank()) {
                setLocation(DataBaseController(this).readAllVisitedLocation())
            }
            //mLocationHelper?.getLocation()
            if (!AppUtility().checkAlarmAlreadySet(this)) {
                AppUtility().startTimerAlarm(this);
            }
        }
        registerReceiver()
        //AppUtility().inssertDemoLocation(this)

    }

    override fun onPause() {
        super.onPause()
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //locationManager!!.removeUpdates()
        }
    }


    fun setLocation(address: List<VisitedLocationInformation>) {
        try {
            if (address != null) {
                var searchResults = DataBaseController(this).parseSearchResult(address)
                list.adapter = LocationsAdapter(this, searchResults!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun setLocationRestults(address: List<SearchResult>) {
        try {
            if (address != null) {
                list.adapter = LocationsAdapter(this, address!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            Log.d("Receiver", "Receive message")
            val message = intent.getStringExtra("message")
            val isUpdated = intent.getBooleanExtra(AppConstant.LOCATION_UPDATE_MESSAGE, false);
            if (isUpdated) {

                runOnUiThread(Runnable {
                    updateLocationAddressList(
                            DataBaseController(this@MainActivity).readAllVisitedLocation())

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
                    mMessageReceiver, IntentFilter(AppConstant.INTENT_FILTER_UPDATE_LOCATION))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPlaceSelected(place: Place) {
        try {
            var searchResultsList = DataBaseController(this).searchLocationOnline(place)
            if (searchResultsList != null && searchResultsList.size > 0) {
                searchResultsList.forEach {
                    //Toast.makeText(this, it.visitedLocationInformation.address, Toast.LENGTH_SHORT).show()
                    Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Place: " + it.visitedLocationInformation.vicinity)

                }
                setLocationRestults(searchResultsList)
            } else {
                //setLocation(DataBaseController(this).readAllVisitedLocation())

                Toast.makeText(this, "No result found", Toast.LENGTH_SHORT).show()

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onError(status: Status) {
        Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "An error occurred: " + status)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateLocationAddress(address: String) {
    }

    override fun updateLocationAddressList(addressList: List<VisitedLocationInformation>) {
        setLocation(addressList)
    }

}
