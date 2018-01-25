package com.where.prateekyadav.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.content.*
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.Status
import com.where.prateekyadav.myapplication.Util.AppUtility
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.common.api.GoogleApiClient
import com.where.prateekyadav.myapplication.Util.PermissionCheckHandler
import com.where.prateekyadav.myapplication.database.DataBaseController
import com.where.prateekyadav.myapplication.modal.SearchResult
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.where.prateekyadav.myapplication.database.DBContract
import com.where.prateekyadav.myapplication.database.VisitedLocationInformation
import com.where.prateekyadav.myapplication.view.NearByActivity
import com.where.prateekyadav.myapplication.view.VisitedActivity
import java.io.Serializable


class MainActivity : AppCompatActivity(), UpdateLocation, GoogleApiClient.OnConnectionFailedListener, PlaceSelectionListener {


    val RQS_1 = 1
    var mLocationHelper: LocationHelper? = null;
    var mListView: ListView? = null
    var mAdapter: LocationsAdapter? = null
    var mSearchResultsList = ArrayList<SearchResult>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mListView = findViewById(R.id.lv_address) as ListView
        mAdapter = LocationsAdapter(this, mSearchResultsList)
        mListView!!.adapter = mAdapter
        mLocationHelper = LocationHelper.getInstance(applicationContext, this);
        //
        DataBaseController(this).copyDataBaseToSDCard()
        setSearchListener()
        setClickListener()
    }

    fun setClickListener() {

        mListView!!.onItemClickListener = object : AdapterView.OnItemClickListener {

            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {


                // Delete a visited place code/////
                /*
                 val listItem = mListView!!.getItemAtPosition(position) as SearchResult
                 val visit = listItem!!.visitResults.visitedLocationInformation
                 val visitList = ArrayList<VisitedLocationInformation>()
                 visitList.add(visit)
                 DataBaseController(this@MainActivity).deleteVisitedPlaceAndUniqueNearByForIt(visitList)
                 setLocationResults(DataBaseController(this@MainActivity).readAllVisitedLocation())*/

            }
        }
    }

    fun setSearchListener() {
        val searchEdittext = findViewById<EditText>(R.id.edt_search)
        searchEdittext.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (s.length > 2)
                    search(s.toString())
                else if (s.length == 0) {
                    setLocationResults(DataBaseController(this@MainActivity).readAllVisitedLocation())
                }
            }
        })
    }




    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
        //
            PermissionCheckHandler.REQUEST_LOCATION_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                // If request is cancelled, the result arrays are empty.
                val hasSth = grantResults.size > 0
                if (hasSth) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //user accepted,
                        Log.d(AppConstant.TAG_KOTLIN_DEMO_APP,"Permission granted")

                        AppUtility().validateAutoStartTimer(this)
                        AppUtility().startTimerAlarm(applicationContext)

                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        val should = PermissionCheckHandler.shouldShowRequestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        if (should) {
                            PermissionCheckHandler.openDialogForPermissionAlert(this,getString(R.string.str_location_permission_alert_message))
                        } else {
                            //user has denied with `Never Ask Again`, go to settings
                            PermissionCheckHandler.promptSettings(this,getString(R.string.str_never_ask_title),getString(R.string.str_never_ask_message))
                        }
                    }
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
            /* var clear: EditText = autocompleteFragment!!.getView().findViewById(R.id.place_autocomplete_search_input)
             if (clear != null &&
                     clear.text.isBlank()) {
             }*/
            setLocationResults(DataBaseController(this).readAllVisitedLocation())

            //mLocationHelper?.getLocation()
            /*if (!AppUtility().checkAlarmAlreadySet(this)) {
                AppUtility().startTimerAlarm(this);
            }*/
        } else {

        }
        AppUtility().validateAutoStartTimer(this)
        if (!AppUtility().checkAlarmAlreadySet(this)) {
            AppUtility().startTimerAlarm(this)
        }
        //registerReceiver()
        //AppUtility().inssertDemoLocation(this)
        //LocationHelper.getInstance(this, this).setLocationListener();


    }

    override fun onPause() {
        super.onPause()
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //locationManager!!.removeUpdates()
        }
    }


    fun setLocationResults(address: List<SearchResult>) {
        try {
            mSearchResultsList = address as ArrayList<SearchResult>
            mAdapter = LocationsAdapter(this, mSearchResultsList)
            mListView!!.adapter = mAdapter
            // mAdapter!!.notifyDataSetChanged()
            //mAdapter!!.notifyDataSetInvalidated()
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
                runOnUiThread({
                    setLocationResults(
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
        // Permission
        val permission = PermissionCheckHandler.checkLocationPermissions(this)
        if (!permission) {
            PermissionCheckHandler.verifyLocationPermissions(this)
        }
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
            // var searchResultsList = DataBaseController(this).searchLocationOnline(place)
            var searchResultsList = DataBaseController(this).searchLocationOffline(place.name.toString())

            if (searchResultsList != null && searchResultsList.size > 0) {
                searchResultsList.forEach {
                    //Toast.makeText(this, it.visitedLocationInformation.address, Toast.LENGTH_SHORT).show()
                    Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Place: " + it.visitResults.visitedLocationInformation.vicinity)

                }
                setLocationResults(searchResultsList)
            } else {
                //setLocation(DataBaseController(this).readAllVisitedLocation())

                Toast.makeText(this, "No result found", Toast.LENGTH_SHORT).show()

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun search(place: String) {
        try {
            // var searchResultsList = DataBaseController(this).searchLocationOnline(place)
            var searchResultsList = DataBaseController(this).searchLocationOffline(place)

            if (searchResultsList != null) {
                searchResultsList.forEach {
                    //Toast.makeText(this, it.visitedLocationInformation.address, Toast.LENGTH_SHORT).show()
                    Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Place: " + it.visitResults.visitedLocationInformation.vicinity)

                }
                setLocationResults(searchResultsList)
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


    override fun updateLocationAddressList(addressList: List<SearchResult>) {
        setLocationResults(addressList)
    }

}
