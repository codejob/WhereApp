package com.where.prateekyadav.myapplication

import android.Manifest
import android.annotation.SuppressLint
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
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.where.prateekyadav.myapplication.Util.AppUtility
import com.where.prateekyadav.myapplication.Util.MySharedPref
import com.where.prateekyadav.myapplication.Util.PermissionCheckHandler
import com.where.prateekyadav.myapplication.database.DataBaseController
import com.where.prateekyadav.myapplication.database.VisitedLocationInformation
import com.where.prateekyadav.myapplication.modal.SearchResult
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
    //
    val ADDRESS_NOT_SET = 0
    val ADDRESS_SET = 1

    companion object {
        var mCurrentObject: LocationHelper? = null;
        fun getInstance(context: Context?, updateLocation: UpdateLocation): LocationHelper {
            if (mCurrentObject == null) {
                mCurrentObject = LocationHelper(context, updateLocation)
            }
            return mCurrentObject as LocationHelper;
        }
    }

    private var handler: Handleupdate? = null
    private var retroCallImplementor: RetroCallImplementor? = null

    private constructor(context: Context?, updateLocation: UpdateLocation) {
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

    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        var gps_enabled = false
        var network_enabled = false


        gps_enabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        network_enabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        var net_loc: Location? = null
        var gps_loc: Location? = null
        var passive_loc: Location? = null
        var finalLoc: Location? = null
        // Check Location permission here
        if (PermissionCheckHandler.checkLocationPermissions(mContext!!)) {

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
                    Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                            "Location updated changed")
                    Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                            "Accuracy: " + location.accuracy)
                    Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                            "Provider: " + location.provider)
                    if (bestLocation == null) {
                        bestLocation = location
                    } else if (location.accuracy < bestLocation!!.accuracy) {
                        bestLocation = location
                    }
                    if (!mLocationReceived) {
                        listHandler?.sendEmptyMessageDelayed(3, 5000);

                        mLocationReceived = true
                    }
                    // Called when a new location is found by the network location provider.
                    // mUpdateLocation?.updateLocationAddressList(getCompleteAddressString(location!!, AppConstant.LOCATION_UPDATE_TYPE_CURRENT));
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                    Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                            "onStatusChanged")
                }

                override fun onProviderEnabled(provider: String) {
                    Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                            "onProviderEnabled")
                }

                override fun onProviderDisabled(provider: String) {
                    //AppUtility().startTimerAlarm(mContext!!.applicationContext)
                    Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                            "onProviderDisabled")
                }
            }
            //
            listHandler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    if (msg.what == 0 && !mLocationReceived) {
                        locationManager!!.removeUpdates(locationListener)
                        Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Location Updates are now removed msg:= " + msg.what)
                        sendEmptyMessageDelayed(1, AppConstant.LOCATION_SYNC_INSTERVAL / 4);

                    } else if (msg.what == 1 && !mLocationReceived) {
                        Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Request location update msg:= " + msg.what)

                        requestLocation(locationManager!!, provider, locationListener)
                        sendEmptyMessageDelayed(2, AppConstant.LOCATION_SYNC_TIMEOUT);
                    } else if (msg.what === 2 && !mLocationReceived) {
                        locationManager!!.removeUpdates(locationListener)

                        Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Location Updates are now removed msg:= " + msg.what)
                        //Location Updates are now
                        var location = getLocation()
                        if (location != null) {
                            Log.v("Location Changed", location.getLatitude().toString() + " and " + location.getLongitude().toString());
                            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                                    "Location received last known")
                            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                                    "Accuracy: " + location.accuracy)
                            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                                    "Provider: " + location.provider)
                            mUpdateLocation?.updateLocationAddressList(getCompleteAddressString(location!!, AppConstant.LOCATION_UPDATE_TYPE_LAST_KNOWN));

                        }
                    } else if (msg.what === 3 && mLocationReceived) {
                        Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "best location accuracy " + bestLocation!!.accuracy)

                        locationManager!!.removeUpdates(locationListener)
                        mUpdateLocation?.updateLocationAddressList(getCompleteAddressString(bestLocation!!, AppConstant.LOCATION_UPDATE_TYPE_CURRENT));

                    } else {
                        locationManager!!.removeUpdates(locationListener)
                        Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Location Updates are now removed final:= ")
                    }
                    super.handleMessage(msg)
                }
            }
            //mUpdateLocation?.updateLocationAddressList(DatabaseHelper(mContext).readAllVisitedLocation())
            mLocationReceived = false;
            requestLocation(locationManager!!, provider, locationListener)
            listHandler.sendEmptyMessageDelayed(2, AppConstant.LOCATION_SYNC_TIMEOUT);
            //
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
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

    public fun getCompleteAddressString(location: Location, locationType: String): List<SearchResult> {
        //
        var LATITUDE: Double = location.latitude
        var LONGITUDE: Double = location.longitude

        /*Toast.makeText(mContext, address,
                Toast.LENGTH_LONG).show();*/

        var pref = MySharedPref.getinstance(mContext);
        var spLatitude: Double = pref.getLatitude();
        var spLongitude: Double = pref.getLongitude();
        var spacuuracy: Float = pref.getFloat(AppConstant.SP_KEY_ACCURACY);

        var previousLocation = Location(LocationManager.GPS_PROVIDER);
        previousLocation.latitude = spLatitude
        previousLocation.longitude = spLongitude
        previousLocation.accuracy = spacuuracy

        var currentLocation = location


        var dbLastLocation = Location(LocationManager.GPS_PROVIDER);
        var lastDBLocation = mDataBaseController.readLastVisitedLocation()
        var lastDBTime: Long = 0
        if (lastDBLocation != null) {
            dbLastLocation.latitude = lastDBLocation!!.latitude
            dbLastLocation.longitude = lastDBLocation!!.longitude
        }
        var insert = false;
        if (spLatitude == 0.0) {
            insert = false;
            pref.setLong(System.currentTimeMillis(), AppConstant.SP_KEY_FIRST_TIME)

        } else {
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                    "Distance prev and curr" + previousLocation.distanceTo(currentLocation))
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                    "Distance curr and DB" + currentLocation.distanceTo(dbLastLocation))
            if (previousLocation.distanceTo(currentLocation) < AppConstant.MIN_DISTANCE_RANGE) {
                insert = true;
            } else {
                //pref.setLong(System.currentTimeMillis(), AppConstant.SP_KEY_SPENT_TIME)
            }
            if (lastDBLocation != null && currentLocation.distanceTo(dbLastLocation) < AppConstant.MIN_DISTANCE_RANGE) {
                insert = false;
                if (spacuuracy > currentLocation.accuracy && lastDBLocation.isPreferred==0) {
                    insert = true
                }
                Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Updating stay time")
                //val stayTIme: Int = ((System.currentTimeMillis() - pref.getLong(AppConstant.SP_KEY_SPENT_TIME)) / (1000 * 60)).toInt()
                mDataBaseController.updateToTime(lastDBLocation.rowID);
                AppUtility().sendUpdateMessage(mContext!!);

            }

        }
        //
        pref.setLocation(LATITUDE, LONGITUDE);

        if (insert) {
            //insert location only here
            var visitedLocationInformation = VisitedLocationInformation("");
            visitedLocationInformation.userId = 0;
            visitedLocationInformation.latitude = LATITUDE
            visitedLocationInformation.longitude = LONGITUDE
            visitedLocationInformation.isAddressSet = ADDRESS_NOT_SET
            visitedLocationInformation.locationProvider = currentLocation.provider
            visitedLocationInformation.accuracy = currentLocation.accuracy
            var fromTime: Long = System.currentTimeMillis()
            val toTime = System.currentTimeMillis()
            if (lastDBLocation == null) {
                fromTime = pref.getLong(AppConstant.SP_KEY_FIRST_TIME)
            }
            visitedLocationInformation.toTime = toTime
            visitedLocationInformation.fromTime = fromTime
            //
            val insertedId = DataBaseController(mContext).insertVisitedLocation(visitedLocationInformation);
            //
            if (insertedId > 0L) {
                retroCallImplementor!!.getAllPlaces(LATITUDE.toString() + "," + LONGITUDE.toString(),
                        handler, location, locationType, insertedId)
            }
        } else {
            pref.setLong(System.currentTimeMillis(), AppConstant.SP_KEY_SPENT_TIME)
        }
        var visitedLocationList = mDataBaseController.readAllVisitedLocation()

        return (visitedLocationList as List<SearchResult>?)!!
    }

    /**
     * Method to add address into database
     */
    fun addAddressIntoDataBase(resultPlace: Result, currentLocation: Location, locationType: String,
                               mPlacesList: List<Result>, rowId: Long) {
        //
        var result: Long = 0
        try {
            var pref = MySharedPref.getinstance(mContext!!.applicationContext);
            var geocoder = Geocoder(mContext, Locale.getDefault())
            //var location: Location = Location(LocationManager.GPS_PROVIDER)
            //location.latitude = resultPlace.geometry.location.lat.toDouble()
            //location.longitude = resultPlace.geometry.location.lng.toDouble()
            val address = resultPlace.name;

            val vicinity = resultPlace.vicinity
            val placeId = resultPlace.placeId
            val photoUrl = resultPlace.photos.toString()
            val nearByPlaces = "";
            val isAddressSet = ADDRESS_SET;
            var LATITUDE: Double = currentLocation.latitude
            var LONGITUDE: Double = currentLocation.longitude
            val addresses: List<Address>

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
            val toTime = System.currentTimeMillis()
            val locationProvider = currentLocation.provider;
            var fromTime: Long = System.currentTimeMillis()
            //
            var dbLastLocation = Location(LocationManager.GPS_PROVIDER);
            var lastDBLocation = mDataBaseController.readLastVisitedLocation()
            var lastDBTime: Long = 0
            if (lastDBLocation != null) {
                dbLastLocation.latitude = lastDBLocation!!.latitude
                dbLastLocation.longitude = lastDBLocation!!.longitude
            }
            // set values for visited location information
            var visitedLocationInformation = VisitedLocationInformation("NA")
            visitedLocationInformation.userId = 1
            visitedLocationInformation.latitude = LATITUDE
            visitedLocationInformation.longitude = LONGITUDE
            visitedLocationInformation.address = address
            visitedLocationInformation.city = city
            visitedLocationInformation.state = state
            visitedLocationInformation.country = country
            visitedLocationInformation.postalCode = postalCode
            visitedLocationInformation.knownName = knownName
            visitedLocationInformation.toTime = toTime
            visitedLocationInformation.fromTime = fromTime
            visitedLocationInformation.locationProvider = locationProvider
            visitedLocationInformation.rowID = 0
            visitedLocationInformation.locationRequestType = locationType
            visitedLocationInformation.vicinity = vicinity
            visitedLocationInformation.placeId = placeId
            visitedLocationInformation.photoUrl = photoUrl
            visitedLocationInformation.nearByPlacesIds = nearByPlaces
            visitedLocationInformation.isAddressSet = isAddressSet
            visitedLocationInformation.accuracy = currentLocation.accuracy
            //
            if (lastDBLocation == null) fromTime = pref.getLong(AppConstant.SP_KEY_FIRST_TIME)

            if (lastDBLocation != null && currentLocation.distanceTo(dbLastLocation) < AppConstant.MIN_DISTANCE_RANGE) {

                val updatedRow = mDataBaseController.updateVisitedLocation(
                        visitedLocationInformation, lastDBLocation.rowID)
                Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Updating address")
            } else {
                result = mDataBaseController.insertVisitedLocation(
                        visitedLocationInformation)
                Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                        "Location inserted")
            }
            //
            pref.setLong(System.currentTimeMillis(), AppConstant.SP_KEY_SPENT_TIME)
            pref.setFloat(currentLocation.accuracy, AppConstant.SP_KEY_ACCURACY)
            //
            addNearByPlaces(mPlacesList, placeId, addresses)
            AppUtility().sendUpdateMessage(mContext!!);
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
    This will handle the response from the API
    we are setting the adapter here and update the recycler view.
     */
    internal inner class Handleupdate : RetroCallIneractor {

        override fun updatePlacesWithId(places: List<Result>, location: Location, locationType: String, rowId: Long) {
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
                    //Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, it.name);
                    var tempLoc = Location(LocationManager.GPS_PROVIDER);
                    tempLoc.latitude = it.geometry.location.lat.toDouble()
                    tempLoc.longitude = it.geometry.location.lng.toDouble()
                    val distance = location.distanceTo(tempLoc)
                    // Just to pick first prominent place within 10 metre
                    if (distance < AppConstant.RADIUS_NEARBY_SEARCH && pos == 0) {
                        minDistance = distance
                        result = it
                        pos += 1
                    }
                    // Code to pick nearest places
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
                if (result == null) {
                    if (places.size > 1) {
                        result = places.get(1)
                    } else {
                        result = places.get(0)
                    }
                }
                //Add address into data base
                addAddressIntoDataBase(result!!, location, locationType, places, rowId)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        override fun updatePlaces(places: MutableList<Result>?, location: Location?, locationType: String?) {

        }

        override fun updatePlaceDetails(place: Result) {

        }


        override fun onFailure() {

        }
    }

    @Synchronized
    fun addNearByPlaces(places: List<Result>, mainPlaceId: String, addresses: List<Address>) {
        //
        if (places != null && places.size > 1) {
            var nearByPlacesIds = "";
            var mList = ArrayList<VisitedLocationInformation>()
            for (i in places.indices) {
                var result: Boolean = false
                try {
                    val it = places.get(i)
                    var pref = MySharedPref.getinstance(mContext);
                    val address = it.name;
                    val vicinity = it.vicinity
                    val placeId = it.placeId
                    val photoUrl = it.photos.toString()
                    if (placeId == mainPlaceId) {
                        continue
                    }

                    val nearPlaces = "";
                    val isAddressSet = 1;

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
                    val fromTime = System.currentTimeMillis()
                    val locationProvider = "NA";
                    val locationType = "NA";
                    val toTime: Long = System.currentTimeMillis();
                    // set values for visited location information
                    var visitedLocationInformation = VisitedLocationInformation("NA")
                    visitedLocationInformation.userId = 1
                    visitedLocationInformation.latitude = LATITUDE
                    visitedLocationInformation.longitude = LONGITUDE
                    visitedLocationInformation.address = address
                    visitedLocationInformation.city = city
                    visitedLocationInformation.state = state
                    visitedLocationInformation.country = country
                    visitedLocationInformation.postalCode = postalCode
                    visitedLocationInformation.knownName = knownName
                    visitedLocationInformation.toTime = toTime
                    visitedLocationInformation.fromTime = fromTime
                    visitedLocationInformation.locationProvider = locationProvider
                    visitedLocationInformation.rowID = 0
                    visitedLocationInformation.locationRequestType = locationType
                    visitedLocationInformation.vicinity = vicinity
                    visitedLocationInformation.placeId = placeId
                    visitedLocationInformation.photoUrl = photoUrl
                    visitedLocationInformation.nearByPlacesIds = nearPlaces
                    visitedLocationInformation.isAddressSet = isAddressSet

                    mList.add(visitedLocationInformation);

                    // add near by place id
                    if (nearByPlacesIds.isEmpty()) {
                        nearByPlacesIds = placeId;
                    } else {
                        nearByPlacesIds = nearByPlacesIds + "," + placeId;
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
                //
                //Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, it.name);
            }
            // Here call method to insert the near by places into table
            DataBaseController(mContext).insertNearByPlaces(mList);
            DataBaseController(mContext).updateNearByPlaces(mainPlaceId, nearByPlacesIds)
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                    "near by location inserted")
        }

    }


}