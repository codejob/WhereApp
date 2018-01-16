package com.where.prateekyadav.myapplication

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.location.*
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import com.where.prateekyadav.myapplication.Util.AppUtility
import com.where.prateekyadav.myapplication.Util.Constant
import com.where.prateekyadav.myapplication.Util.MySharedPref
import com.where.prateekyadav.myapplication.database.DatabaseHelper
import com.where.prateekyadav.myapplication.database.VisitedLocationInformation
import com.where.prateekyadav.myapplication.search.model.placesdetails.Result
import com.where.prateekyadav.myapplication.search.network.RetroCallImplementor
import com.where.prateekyadav.myapplication.search.network.RetroCallIneractor
import java.io.IOException
import java.util.*


/**
 * Created by Infobeans on 1/11/2018.
 */
class LocationHelper {
    lateinit var usersDBHelper: DatabaseHelper

    var locationManager: LocationManager? = null;

    var mContext: Context? = null;
    var mUpdateLocation: UpdateLocation? = null;

    private var handler: Handleupdate? = null
    private var retroCallImplementor: RetroCallImplementor? = null

    constructor(context: Context?, updateLocation: UpdateLocation) {
        mContext = context
        mUpdateLocation = updateLocation
        usersDBHelper = DatabaseHelper(mContext);
        handler = Handleupdate()
        retroCallImplementor = RetroCallImplementor()
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        var gps_enabled = false
        var network_enabled = false

        locationManager = mContext?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        gps_enabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        network_enabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        var net_loc: Location? = null
        var gps_loc: Location? = null
        var passive_loc: Location? = null
        var finalLoc: Location? = null

         if (gps_enabled) {
             getLocationFromListner(LocationManager.GPS_PROVIDER)
         } else if (network_enabled) {
             getLocationFromListner(LocationManager.NETWORK_PROVIDER)
         } else {
             getLocationFromListner(LocationManager.PASSIVE_PROVIDER)
         }

       /* if (gps_enabled)
            gps_loc = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (network_enabled)
            net_loc = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        else {
            passive_loc = locationManager!!.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)

        }
        if (gps_enabled && gps_loc == null) {
            getLocationFromListner(LocationManager.GPS_PROVIDER)
        } else if (network_enabled && net_loc == null) {
            getLocationFromListner(LocationManager.NETWORK_PROVIDER)

        } else if (gps_loc != null || net_loc != null) {

            //smaller the number more accurate result will
            if (net_loc != null && gps_loc != null && gps_loc!!.getAccuracy() > net_loc!!.getAccuracy())
                finalLoc = net_loc
            else if (gps_loc != null)
                finalLoc = gps_loc
            else if (net_loc != null)
                finalLoc = net_loc

            mUpdateLocation?.updateLocationAddressList(getCompleteAddressString(finalLoc!!));

        } else if (passive_loc != null && !gps_enabled && !network_enabled) {
            //setLocation(passive_loc)
            mUpdateLocation?.updateLocationAddressList(getCompleteAddressString(passive_loc!!));

        } else {
            if (gps_enabled) {
                getLocationFromListner(LocationManager.GPS_PROVIDER)
            } else if (network_enabled) {
                getLocationFromListner(LocationManager.NETWORK_PROVIDER)

            } else {
                getLocationFromListner(LocationManager.PASSIVE_PROVIDER)
            }
        }*/

    }

    fun getLocationFromListner(provider: String) {
        // Acquire a reference to the system Location Manager

        // Define a listener that responds to location updates
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.v("Location Changed", location.getLatitude().toString() + " and " + location.getLongitude().toString());
                Log.i(Constant.E_WORKBOOK_DEBUG_TAG,
                        "Location updated changed")
                Log.i(Constant.E_WORKBOOK_DEBUG_TAG,
                        "Accuracy: " + location.accuracy)
                Log.i(Constant.E_WORKBOOK_DEBUG_TAG,
                        "Provider: " + location.provider)
                // Called when a new location is found by the network location provider.
                mUpdateLocation?.updateLocationAddressList(getCompleteAddressString(location!!));

                //locationManager!!.removeUpdates(this);
                //AppUtility().startTimerAlarm(mContext!!.applicationContext)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                Log.i(Constant.E_WORKBOOK_DEBUG_TAG,
                        "onStatusChanged")
            }

            override fun onProviderEnabled(provider: String) {
                Log.i(Constant.E_WORKBOOK_DEBUG_TAG,
                        "onProviderEnabled")
            }

            override fun onProviderDisabled(provider: String) {
                //AppUtility().startTimerAlarm(mContext!!.applicationContext)
                Log.i(Constant.E_WORKBOOK_DEBUG_TAG,
                        "onProviderDisabled")
            }
        }
        try {
            mUpdateLocation?.updateLocationAddressList(DatabaseHelper(mContext).readAllVisitedLocation())
            // Register the listener with the Location Manager to receive location updates
            // @RequiresPermission(anyOf = arrayOf("android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION")) {
            locationManager!!.requestSingleUpdate(provider,locationListener,null)

            var listHandler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    if (msg.what === 0) {
                        locationManager!!.removeUpdates(locationListener)
                        Log.i(Constant.E_WORKBOOK_DEBUG_TAG,"Location Updates are now removed")
                        //Location Updates are now removed
                    }
                    super.handleMessage(msg)
                }
            }
            listHandler.sendEmptyMessageDelayed(0, Constant.LOCATION_SYNC_INSTERVAL-5);

            Log.i(Constant.E_WORKBOOK_DEBUG_TAG,
                    "requestLocationUpdates")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getAddress(latitude: Double, longitude: Double): String {
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(mContext, Locale.getDefault())

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 5) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            for (adress: Address in addresses) {
                var loc: Location = Location(LocationManager.GPS_PROVIDER);
                loc.latitude = latitude;
                loc.longitude = longitude;
                Log.d("Accuracy : ", loc.accuracy.toString());

            }
            return addresses[0].getAddressLine(0);

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return ""

    }

    // DownloadImage AsyncTask
    private inner class GetAddressFromLatLong : AsyncTask<String, Unit, String>() {

        var mProgressDialog: ProgressDialog? = null;
        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun doInBackground(vararg URL: String): String? {
            var address: String = "";

            try {
                //address = getCompleteAddressString(mLat, mLong);
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //
            return address;
        }

        override fun onPostExecute(result: String) {

        }
    }

    private fun getCompleteAddressString(location: Location): List<VisitedLocationInformation> {
        //
        var LATITUDE: Double = location.latitude
        var LONGITUDE: Double = location.longitude

        /*Toast.makeText(mContext, address,
                Toast.LENGTH_LONG).show();*/

        var pref = MySharedPref(mContext);
        var spLatitude: Double = pref.getLatitude();
        var spLongitude: Double = pref.getLongitude();
        var spTime: Long = pref.getLong();

        var previousLocation = Location(LocationManager.GPS_PROVIDER);
        previousLocation.latitude = spLatitude
        previousLocation.longitude = spLongitude

        var currentLocation = Location(LocationManager.GPS_PROVIDER);
        currentLocation.latitude = LATITUDE
        currentLocation.longitude = LONGITUDE


        var dbLastLocation = Location(LocationManager.GPS_PROVIDER);
        var lastDBLocation = usersDBHelper.readLastVisitedLocation()
        var lastDBTime:Long=0
        if (lastDBLocation != null) {
            dbLastLocation.latitude = lastDBLocation!!.latitude
            dbLastLocation.longitude = lastDBLocation!!.longitude
            lastDBTime=lastDBLocation.dateTime
        }
        var insert = true;
        if (spLatitude == 0.0) {
            insert = false;
            pref.setLong(System.currentTimeMillis())
        } else {
            Log.i(Constant.E_WORKBOOK_DEBUG_TAG,
                    "Distance prev and curr" + previousLocation.distanceTo(currentLocation))
            Log.i(Constant.E_WORKBOOK_DEBUG_TAG,
                    "Distance curr and DB" + currentLocation.distanceTo(dbLastLocation))
            if (previousLocation.distanceTo(currentLocation) < 500) {
                insert = true;
            }
            if (lastDBLocation != null && currentLocation.distanceTo(dbLastLocation) < 500) {
                insert = false;
            }

        }
        pref.setLocation(LATITUDE, LONGITUDE);

        if (insert) {
            retroCallImplementor!!.getAllPlaces(LATITUDE.toString() + "," + LONGITUDE.toString(), handler,location)
        }
        var visitedLocationList = usersDBHelper.readAllVisitedLocation()

        return visitedLocationList!!
    }

    fun insertAddress(resultPlace: Result,currentLocation: Location) {
        //
        var result: Boolean = false
        try {
            var pref = MySharedPref(mContext);
            var geocoder = Geocoder(mContext, Locale.getDefault())
            //var location: Location = Location(LocationManager.GPS_PROVIDER)
            //location.latitude = resultPlace.geometry.location.lat.toDouble()
            //location.longitude = resultPlace.geometry.location.lng.toDouble()
            val address = resultPlace.name;

            val knownName = resultPlace.vicinity
            var LATITUDE: Double = currentLocation.latitude
            var LONGITUDE: Double = currentLocation.longitude
            val addresses: List<Address>
            geocoder = Geocoder(mContext, Locale.getDefault())

            addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses == null || addresses.size == 0) {
                return null!!;
            }
            // val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            //val address = addresses
            val city = addresses[0].getLocality()
            val state = addresses[0].getAdminArea()
            val country = addresses[0].getCountryName()
            val postalCode = addresses[0].getPostalCode()
            //val knownName = addresses[0].getFeatureName() // Only if available else return NULL
            val tsLong = System.currentTimeMillis()
            val locationProvider = currentLocation.provider;
            val stayTIme:Int = ((System.currentTimeMillis()-pref.getLong())/(1000*60)).toInt()

            result = usersDBHelper.insertVisitedLocation(
                    VisitedLocationInformation(userid = 1, latitude = LATITUDE,
                            longitude = LONGITUDE, address = address, city = city,
                            state = state, country = country, postalCode = postalCode,
                            knownName = knownName, stayTime = stayTIme, dateTime = tsLong,
                            locationProvider = locationProvider))
            pref.setLong(System.currentTimeMillis())
            Log.i(Constant.E_WORKBOOK_DEBUG_TAG,
                    "Location inserted")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        println(result)
    }

    /*
    This will handle the response from the API
    we are setting the adapter here and update the recycler view.
     */
    internal inner class Handleupdate : RetroCallIneractor {

        override fun updatePlaces(places: List<Result>,location: Location) {

            try {
                var result: Result? = null;
                if (places != null && places.size > 1)
                    result = places.get(1)
                else
                    result = places.get(0)

                insertAddress(result,location)

                places.forEach {
                    Log.i(Constant.E_WORKBOOK_DEBUG_TAG, it.name);
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        override fun updatePlaceDetails(place: Result) {

        }


        override fun onFailure() {

        }


    }

}