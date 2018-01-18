package com.where.prateekyadav.myapplication

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.where.prateekyadav.myapplication.Util.Constant
import com.where.prateekyadav.myapplication.Util.MySharedPref
import com.where.prateekyadav.myapplication.database.DataBaseController
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
    lateinit var mDataBaseController: DataBaseController

    var locationManager: LocationManager? = null;

    var mContext: Context? = null;
    var mUpdateLocation: UpdateLocation? = null;
    var mLocationReceived: Boolean = false;

    private var handler: Handleupdate? = null
    private var retroCallImplementor: RetroCallImplementor? = null

    constructor(context: Context?, updateLocation: UpdateLocation) {
        mContext = context
        mUpdateLocation = updateLocation
        mDataBaseController = DataBaseController(mContext);
        handler = Handleupdate()
        retroCallImplementor = RetroCallImplementor()
    }

    fun setLocationListener() {
        var gps_enabled = false
        var network_enabled = false

        locationManager = mContext?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        gps_enabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        network_enabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


        if (gps_enabled) {
            getLocationFromListner(LocationManager.GPS_PROVIDER)
        } else if (network_enabled) {
            getLocationFromListner(LocationManager.NETWORK_PROVIDER)
        } else {
            getLocationFromListner(LocationManager.PASSIVE_PROVIDER)
        }


    }

    fun getLocation(): Location? {
        var gps_enabled = false
        var network_enabled = false


        gps_enabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        network_enabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        var net_loc: Location? = null
        var gps_loc: Location? = null
        var passive_loc: Location? = null
        var finalLoc: Location? = null



        if (gps_enabled)
            gps_loc = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (network_enabled)
            net_loc = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        else {
            passive_loc = locationManager!!.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)

        }
        if (gps_loc != null || net_loc != null) {

            //smaller the number more accurate result will
            if (net_loc != null && gps_loc != null && gps_loc!!.getAccuracy() > net_loc!!.getAccuracy())
                finalLoc = net_loc
            else if (gps_loc != null)
                finalLoc = gps_loc
            else if (net_loc != null)
                finalLoc = net_loc

            return finalLoc
        } else if (passive_loc != null && !gps_enabled && !network_enabled) {
            //setLocation(passive_loc)
            return passive_loc
        }
        return null
    }

    fun getLocationFromListner(provider: String) {
        // Acquire a reference to the system Location Manager

        // Define a listener that responds to location updates

        var bestLocation: Location? = null;
        var listHandler: Handler? = null;
        var locationListener: LocationListener? = null;
        try {

            locationListener = object : LocationListener {

                override fun onLocationChanged(location: Location) {
                    Log.v("Location Changed", location.getLatitude().toString() + " and " + location.getLongitude().toString());
                    Log.i(Constant.TAG_KOTLIN_DEMO_APP,
                            "Location updated changed")
                    Log.i(Constant.TAG_KOTLIN_DEMO_APP,
                            "Accuracy: " + location.accuracy)
                    Log.i(Constant.TAG_KOTLIN_DEMO_APP,
                            "Provider: " + location.provider)
                    if(bestLocation==null){
                        bestLocation=location
                    }else if(location.accuracy < bestLocation!!.accuracy){
                        bestLocation=location
                    }
                    if (!mLocationReceived) {
                        listHandler?.sendEmptyMessageDelayed(3, 3000);

                        mLocationReceived = true
                    }
                    // Called when a new location is found by the network location provider.
                   // mUpdateLocation?.updateLocationAddressList(getCompleteAddressString(location!!, Constant.LOCATION_UPDATE_TYPE_CURRENT));
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                    Log.i(Constant.TAG_KOTLIN_DEMO_APP,
                            "onStatusChanged")
                }

                override fun onProviderEnabled(provider: String) {
                    Log.i(Constant.TAG_KOTLIN_DEMO_APP,
                            "onProviderEnabled")
                }

                override fun onProviderDisabled(provider: String) {
                    //AppUtility().startTimerAlarm(mContext!!.applicationContext)
                    Log.i(Constant.TAG_KOTLIN_DEMO_APP,
                            "onProviderDisabled")
                }
            }



            listHandler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    if (msg.what == 0 && !mLocationReceived) {
                        locationManager!!.removeUpdates(locationListener)
                        Log.i(Constant.TAG_KOTLIN_DEMO_APP, "Location Updates are now removed msg:= " + msg.what)
                        sendEmptyMessageDelayed(1, Constant.LOCATION_SYNC_INSTERVAL / 4);

                    } else if (msg.what == 1 && !mLocationReceived) {
                        Log.i(Constant.TAG_KOTLIN_DEMO_APP, "Request location update msg:= " + msg.what)

                        requestLocation(locationManager!!, provider, locationListener)
                        sendEmptyMessageDelayed(2, Constant.LOCATION_SYNC_TIMEOUT);
                    } else if (msg.what === 2 && !mLocationReceived) {
                        locationManager!!.removeUpdates(locationListener)

                        Log.i(Constant.TAG_KOTLIN_DEMO_APP, "Location Updates are now removed msg:= " + msg.what)
                        //Location Updates are now
                        var location = getLocation()
                        if (location != null)
                            mUpdateLocation?.updateLocationAddressList(getCompleteAddressString(location!!, Constant.LOCATION_UPDATE_TYPE_LAST_KNOWN));

                    } else if (msg.what === 3 && mLocationReceived) {
                        Log.i(Constant.TAG_KOTLIN_DEMO_APP, "best location accuracy "+bestLocation!!.accuracy)

                        locationManager!!.removeUpdates(locationListener)
                        mUpdateLocation?.updateLocationAddressList(getCompleteAddressString(bestLocation!!, Constant.LOCATION_UPDATE_TYPE_CURRENT));

                    }else {
                        locationManager!!.removeUpdates(locationListener)
                        Log.i(Constant.TAG_KOTLIN_DEMO_APP, "Location Updates are now removed final:= ")
                    }
                    super.handleMessage(msg)
                }
            }

            //mUpdateLocation?.updateLocationAddressList(DatabaseHelper(mContext).readAllVisitedLocation())
            mLocationReceived = false;
            requestLocation(locationManager!!, provider, locationListener)
            listHandler.sendEmptyMessageDelayed(2, Constant.LOCATION_SYNC_TIMEOUT);


            Log.i(Constant.TAG_KOTLIN_DEMO_APP,
                    "requestLocationUpdates")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun requestLocation(locationManager: LocationManager, provider: String, locationListener: LocationListener) {
        if (ContextCompat.checkSelfPermission(mContext!!, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mContext as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    99)
        } else {
            locationManager!!.requestLocationUpdates(provider, 0L, 0F, locationListener)
            //locationManager!!.requestSingleUpdate(provider, locationListener, null)

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

    private fun getCompleteAddressString(location: Location, locationType: String): List<VisitedLocationInformation> {
        //
        var LATITUDE: Double = location.latitude
        var LONGITUDE: Double = location.longitude

        /*Toast.makeText(mContext, address,
                Toast.LENGTH_LONG).show();*/

        var pref = MySharedPref(mContext);
        var spLatitude: Double = pref.getLatitude();
        var spLongitude: Double = pref.getLongitude();
        var spTime: Long = pref.getLong(Constant.SP_KEY_SPENT_TIME);

        var previousLocation = Location(LocationManager.GPS_PROVIDER);
        previousLocation.latitude = spLatitude
        previousLocation.longitude = spLongitude

        var currentLocation = Location(LocationManager.GPS_PROVIDER);
        currentLocation.latitude = LATITUDE
        currentLocation.longitude = LONGITUDE


        var dbLastLocation = Location(LocationManager.GPS_PROVIDER);
        var lastDBLocation = mDataBaseController.readLastVisitedLocation()
        var lastDBTime: Long = 0
        if (lastDBLocation != null) {
            dbLastLocation.latitude = lastDBLocation!!.latitude
            dbLastLocation.longitude = lastDBLocation!!.longitude
            lastDBTime = lastDBLocation.dateTime
        }
        var insert = false;
        if (spLatitude == 0.0) {
            insert = false;
            pref.setLong(System.currentTimeMillis(), Constant.SP_KEY_SPENT_TIME)
        } else {
            Log.i(Constant.TAG_KOTLIN_DEMO_APP,
                    "Distance prev and curr" + previousLocation.distanceTo(currentLocation))
            Log.i(Constant.TAG_KOTLIN_DEMO_APP,
                    "Distance curr and DB" + currentLocation.distanceTo(dbLastLocation))
            if (previousLocation.distanceTo(currentLocation) < Constant.MIN_DISTANCE_RANGE) {
                insert = true;
            } else {
                pref.setLong(System.currentTimeMillis(), Constant.SP_KEY_SPENT_TIME)
            }
            if (lastDBLocation != null && currentLocation.distanceTo(dbLastLocation) < Constant.MIN_DISTANCE_RANGE) {
                insert = false;
                val stayTIme: Int = ((System.currentTimeMillis() - pref.getLong(Constant.SP_KEY_SPENT_TIME)) / (1000 * 60)).toInt()
                mDataBaseController.updateStayTime(lastDBLocation.rowID,stayTIme);
            }

        }
        pref.setLocation(LATITUDE, LONGITUDE);

        if (insert) {
            retroCallImplementor!!.getAllPlaces(LATITUDE.toString() + "," + LONGITUDE.toString(), handler, location, locationType)
        }
        var visitedLocationList = mDataBaseController.readAllVisitedLocation()

        return visitedLocationList!!
    }

    fun insertAddress(resultPlace: Result, currentLocation: Location, locationType: String, mPlacesList:List<Result>) {
        //
        var result: Boolean = false
        try {
            var pref = MySharedPref(mContext);
            var geocoder = Geocoder(mContext, Locale.getDefault())
            //var location: Location = Location(LocationManager.GPS_PROVIDER)
            //location.latitude = resultPlace.geometry.location.lat.toDouble()
            //location.longitude = resultPlace.geometry.location.lng.toDouble()
            val address = resultPlace.name;

            val vicinity = resultPlace.vicinity
            val placeId = resultPlace.placeId
            val photoUrl = resultPlace.photos.toString()
            val nearByPlaces="";
            val isAddressSet=1;
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
            val knownName = addresses[0].getFeatureName() // Only if available else return NULL
            val tsLong = System.currentTimeMillis()
            val locationProvider = currentLocation.provider;
            val stayTIme: Int = ((System.currentTimeMillis() - pref.getLong(Constant.SP_KEY_SPENT_TIME)) / (1000 * 60)).toInt()
            //
            result = mDataBaseController.insertVisitedLocation(
                    VisitedLocationInformation(userId = 1, latitude = LATITUDE,
                            longitude = LONGITUDE, address = address, city = city,
                            state = state, country = country, postalCode = postalCode,
                            knownName = knownName, stayTime = stayTIme, dateTime = tsLong,
                            locationProvider = locationProvider,rowID = 0,
                            locationRequestType = locationType,vicinity = vicinity,
                            placeId = placeId,photoUrl = photoUrl,nearByPlacesIds = nearByPlaces,
                            isAddressSet = isAddressSet))
            //
            pref.setLong(System.currentTimeMillis(),Constant.SP_KEY_SPENT_TIME)
            Log.i(Constant.TAG_KOTLIN_DEMO_APP,

                    "Location inserted")

            //
            addNearByPlaces(mPlacesList,placeId,addresses)
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

        override fun updatePlaces(places: List<Result>, location: Location, locationType: String) {

            try {
                var result: Result? = null;

                /* if (places != null && places.size > 1)
                     result = places.get(1)
                 else
                     result = places.get(0)
 */

                var minDistance: Float = 0.0F;
                var pos: Int = 0;
                places.forEach {
                    //Log.i(Constant.TAG_KOTLIN_DEMO_APP, it.name);
                    var tempLoc = Location(LocationManager.GPS_PROVIDER);
                    tempLoc.latitude = it.geometry.location.lat.toDouble()
                    tempLoc.longitude = it.geometry.location.lng.toDouble()
                    val distance = location.distanceTo(tempLoc)
                    // Just to pick first prominent place within 10 metre
                    if(distance<10 && pos==0){
                        minDistance = distance
                        result = it
                        pos += 1
                    }


                   /* if (pos == 0) {
                        minDistance = distance
                        result = it

                    } else if (minDistance > distance) {
                        minDistance = distance
                        result = it
                    }
                    pos += 1
                    */

                    Log.i("Distance bt curr & res", it.name + "  " + location.distanceTo(tempLoc).toString());

                }
                insertAddress(result!!, location, locationType,places)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        override fun updatePlaceDetails(place: Result) {

        }


        override fun onFailure() {

        }
    }

    fun addNearByPlaces(places: List<Result>,mainPlaceId:String,addresses: List<Address>) {
        //
        if (places != null && places.size > 1)
        {
            var nearByPlacesIds="";
            var mList = ArrayList<VisitedLocationInformation>()
            places.forEach {
                var result: Boolean = false

                try {
                    var pref = MySharedPref(mContext);
                    val address = it.name;
                    val vicinity = it.vicinity
                    val placeId = it.placeId
                    val photoUrl = it.photos.toString()
                    val nearByPlaces="";
                    val isAddressSet=1;
                    var LATITUDE: Double = it.geometry.location.lat
                    var LONGITUDE: Double = it.geometry.location.lng

                    if (addresses == null || addresses.size == 0) {
                        return null!!;
                    }
                    //
                    val city = addresses[0].getLocality()
                    val state = addresses[0].getAdminArea()
                    val country = addresses[0].getCountryName()
                    val postalCode = addresses[0].getPostalCode()
                    val knownName = addresses[0].getFeatureName() // Only if available else return NULL
                    val tsLong = System.currentTimeMillis()
                    val locationProvider = "NA";
                    val locationType = "NA";
                    val stayTIme: Int = ((System.currentTimeMillis() - pref.getLong(Constant.SP_KEY_SPENT_TIME)) / (1000 * 60)).toInt()
                    //
                    mList.add( VisitedLocationInformation(userId = 1, latitude = LATITUDE,
                            longitude = LONGITUDE, address = address, city = city,
                            state = state, country = country, postalCode = postalCode,
                            knownName = knownName, stayTime = stayTIme, dateTime = tsLong,
                            locationProvider = locationProvider,rowID = 0,
                            locationRequestType = locationType,vicinity = vicinity,
                            placeId = placeId,photoUrl = photoUrl,nearByPlacesIds = nearByPlaces,
                            isAddressSet = isAddressSet));
                    //
                    nearByPlacesIds=nearByPlaces+","+placeId;

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                //
                //Log.i(Constant.TAG_KOTLIN_DEMO_APP, it.name);
            }
            // Here call method to insert the near by places into table
            DataBaseController(mContext).insertNearByPlaces(mList);
            DataBaseController(mContext).updateNearByPlaces(mainPlaceId,nearByPlacesIds)
            Log.i(Constant.TAG_KOTLIN_DEMO_APP,
                    "near by location inserted")
        }

    }



}