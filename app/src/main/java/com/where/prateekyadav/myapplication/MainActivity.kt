package com.where.prateekyadav.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.where.prateekyadav.myapplication.Services.AddressUpdateService
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.where.prateekyadav.myapplication.Util.AppUtility
import com.where.prateekyadav.myapplication.Util.PermissionCheckHandler
import com.where.prateekyadav.myapplication.database.DataBaseController
import com.where.prateekyadav.myapplication.modal.SearchResult


class MainActivity : AppCompatActivity(), UpdateLocation {


    var mLocationHelper: LocationHelper? = null;
    var mListView: ListView? = null
    var mAdapter: LocationsAdapter? = null
    var mSearchResultsList = ArrayList<SearchResult>();
    var mSearchEdittext: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mListView = findViewById(R.id.lv_address) as ListView
        mAdapter = LocationsAdapter(this, mSearchResultsList)
        mListView!!.adapter = mAdapter
        mListView!!.emptyView = findViewById(R.id.tv_no_records) as TextView
        mLocationHelper = LocationHelper.getInstance(applicationContext, this);
        //
        DataBaseController(this).copyDataBaseToSDCard()
        setSearchListener()
        setClickListener()
        startAddressUpdateServiceToUpdateAnyRemainingAddresss()
       // AppUtility().startTimerAlarm(this,true);
    }

    fun setClickListener() {

        mListView!!.onItemClickListener = object : AdapterView.OnItemClickListener {

            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {


            }
        }
    }

    fun setSearchListener() {
        mSearchEdittext = findViewById<EditText>(R.id.edt_search)
        mSearchEdittext!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (s.length > 2)
                    search(s.toString())
                else if (s.length == 0) {
                    setLocationResults(DataBaseController(this@MainActivity).readRecentVisitedLocation(),true)
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
                        Log.d(AppConstant.TAG_KOTLIN_DEMO_APP, "Permission granted")

                        AppUtility().validateAutoStartTimer(this)

                        AppUtility().startTimerAlarm(applicationContext, true)
                        //startService(Intent(this, TimerService::class.java));

                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        val should = PermissionCheckHandler.shouldShowRequestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        if (should) {
                            PermissionCheckHandler.openDialogForPermissionAlert(this, getString(R.string.str_location_permission_alert_message))
                        } else {
                            //user has denied with `Never Ask Again`, go to settings
                            PermissionCheckHandler.promptSettings(this, getString(R.string.str_never_ask_title), getString(R.string.str_never_ask_message))
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
            setLocationResults(DataBaseController(this).readRecentVisitedLocation(),false)

            //mLocationHelper?.getLocation()
            /*if (!AppUtility().checkAlarmAlreadySet(this)) {
                AppUtility().startTimerAlarm(this);
            }*/
        } else {

        }
        AppUtility().validateAutoStartTimer(this)
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


    fun setLocationResults(address: List<SearchResult>, fromSearch: Boolean) {
        try {
            if (mSearchEdittext!!.text.length == 0 || fromSearch) {
                mSearchResultsList.clear()
                mSearchResultsList.addAll(address as ArrayList<SearchResult>)
                // mSearchResultsList=address as ArrayList<SearchResult>
                // mAdapter = LocationsAdapter(this, mSearchResultsList)
                //mListView!!.adapter = mAdapter

                //mAdapter!!.notifyDataSetInvalidated()
                mAdapter!!.notifyDataSetChanged()
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
                runOnUiThread({
                    setLocationResults(
                            DataBaseController(this@MainActivity).readRecentVisitedLocation(),false)

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



    fun search(place: String) {
        try {
            // var searchResultsList = DataBaseController(this).searchLocationOnline(place)
            var searchResultsList = DataBaseController(this).searchLocationOffline(place)

            if (searchResultsList != null) {
                searchResultsList.forEach {
                    //Toast.makeText(this, it.visitedLocationInformation.address, Toast.LENGTH_SHORT).show()
                    Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Search results: " + it.visitResults.visitedLocationInformation.vicinity)

                }
                setLocationResults(searchResultsList,true)
            } else {
                //setLocation(DataBaseController(this).readAllVisitedLocation())

                // Toast.makeText(this, "No result found", Toast.LENGTH_SHORT).show()

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }



    override fun updateLocationAddressList(addressList: List<SearchResult>) {
        setLocationResults(addressList,false)
    }

    /**
     * Method to start service to synced the draft forms
     *
     * @param context
     */
    private fun startAddressUpdateServiceToUpdateAnyRemainingAddresss() {
        if (!AddressUpdateService.RUNNING) {
            val serviceIntent = Intent(this, AddressUpdateService::class.java)
            startService(serviceIntent)
        }
    }
}
