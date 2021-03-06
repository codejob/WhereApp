package com.where.prateekyadav.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.location.LocationListener
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.where.prateekyadav.myapplication.Util.*
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
    public var mLocationReceived: Boolean = false;
    //
    val ADDRESS_NOT_SET = 0
    val ADDRESS_SET = 1

    companion object {
        var mCurrentObject: LocationHelper? = null;
        fun getInstance(context: Context?): LocationHelper {
            if (mCurrentObject == null) {
                mCurrentObject = LocationHelper(context)
            }
            return mCurrentObject as LocationHelper;
        }
    }

    private var handler: Handleupdate? = null
    private var retroCallImplementor: RetroCallImplementor? = null
    private lateinit var mConnectionDetector: ConnectionDetector;

    private constructor(context: Context?) {
        mContext = context
        mDataBaseController = DataBaseController(mContext);
        handler = Handleupdate()
        retroCallImplementor = RetroCallImplementor()
        mConnectionDetector = ConnectionDetector.getInstance(context!!)
    }


    fun fetchLocation(forceUpdateLoation: Boolean): Boolean {
        var gps_enabled = false
        var network_enabled = false

        locationManager = mContext?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        gps_enabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        network_enabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


        /// Will use in next version///////
        /*if (gps_enabled || network_enabled) {
            FuseLocation(mContext, forceUpdateLoation)
            return true
        }*/

        fetchLocationFromListener(forceUpdateLoation)
        return false

    }

    fun fetchLocationFromListener(forceUpdateLoation: Boolean): Boolean {
        var gps_enabled = false
        var network_enabled = false

        locationManager = mContext?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        gps_enabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        network_enabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


        if (gps_enabled) {
            getLocationFromListner(LocationManager.GPS_PROVIDER, forceUpdateLoation)
            return true
        } else if (network_enabled) {
            getLocationFromListner(LocationManager.NETWORK_PROVIDER, forceUpdateLoation)
            return true
        } else {
            getLocationFromListner(LocationManager.PASSIVE_PROVIDER, forceUpdateLoation)
        }
        return false
    }


    fun checkLocationAvailable(): Boolean {
        var gps_enabled = false
        var network_enabled = false

        locationManager = mContext?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        gps_enabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        network_enabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


        if (gps_enabled) {
            return true
        } else if (network_enabled) {
            return true
        }
        return false

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
            // Commenting last visited gps as it would give bad location
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

    ////Older way to access location //////////////////////
    fun getLocationFromListner(provider: String, forceUpdateLoation: Boolean) {
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
                            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                                    "Accuracy: " + location.accuracy)
                        }
                        if (location != null && (location.accuracy < AppConstant.MAX_ACCURACY_RANGE || forceUpdateLoation)) {
                            Log.v("Location Changed", location.getLatitude().toString() + " and " + location.getLongitude().toString());
                            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                                    "Location received last known")
                            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                                    "Accuracy: " + location.accuracy)
                            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                                    "Provider: " + location.provider)
                            getCompleteAddressString(location!!, AppConstant.LOCATION_UPDATE_TYPE_LAST_KNOWN, forceUpdateLoation)

                        } else if (!checkLocationAvailable()) {
                            AppUtility().showGpsOffNotification(mContext)
                        } else if (checkLocationAvailable()) {
                            //fetchLocationFromListener(forceUpdateLoation)
                        }
                    } else if (msg.what === 3 && mLocationReceived) {
                        Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "best location accuracy " + bestLocation!!.accuracy)

                        locationManager!!.removeUpdates(locationListener)
                        if (bestLocation!!.accuracy <= AppConstant.MAX_ACCURACY_RANGE || forceUpdateLoation) {
                            getCompleteAddressString(bestLocation!!, AppConstant.LOCATION_UPDATE_TYPE_CURRENT, forceUpdateLoation)
                        }
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
            if (forceUpdateLoation) {
                listHandler.sendEmptyMessageDelayed(2, AppConstant.LOCATION_SYNC_TIMEOUT_FORCE);
            } else {
                listHandler.sendEmptyMessageDelayed(2, AppConstant.LOCATION_SYNC_TIMEOUT);

            }
            //
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                    "requestLocationUpdates")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //////////////New fused location api/////////////////////
    inner class FuseLocation : GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {
        var mGoogleApiClient: GoogleApiClient? = null
        var bestLocation: Location? = null;
        var listHandler: Handler? = null;
        var forceUpdateLoation: Boolean = false

        constructor(context: Context?, forceUpdate: Boolean) {
            forceUpdateLoation = forceUpdate
            try {
                mGoogleApiClient = GoogleApiClient.Builder(context!!)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        //.enableAutoManage(context!!, this)
                        .addApi(LocationServices.API).build()

                mGoogleApiClient!!.connect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        fun geLocation() {
            try {
                var locationReceived = false
                val mLocationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        for (location in locationResult!!.locations) {
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
                            if (!locationReceived && location.accuracy < 100) {
                                listHandler?.sendEmptyMessageDelayed(3, 5000);

                                locationReceived = true
                            }
                        }
                    }
                }
                val mLocationRequest = LocationRequest()
                mLocationRequest.interval = (0).toLong()
                mLocationRequest.fastestInterval = 0
                mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY


                listHandler = object : Handler() {
                    override fun handleMessage(msg: Message) {
                        if (msg.what == 0 && !locationReceived) {

                        } else if (msg.what === 2 && !locationReceived) {
                            if (mGoogleApiClient!!.isConnected)
                                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationCallback)

                            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Location Updates are now removed msg:= " + msg.what)
                            //Location Updates are now
                            var location: Location? = null
                            if (mGoogleApiClient!!.isConnected) {
                                location = LocationServices.FusedLocationApi
                                        .getLastLocation(mGoogleApiClient)
                            }
                            if (location != null && location.accuracy < 100) {
                                Log.v("Location Changed", location.getLatitude().toString() + " and " + location.getLongitude().toString());
                                Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                                        "Location received last known")
                                Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                                        "Accuracy: " + location.accuracy)
                                Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                                        "Provider: " + location.provider)
                                getCompleteAddressString(location!!, AppConstant.LOCATION_UPDATE_TYPE_LAST_KNOWN, forceUpdateLoation)

                            } else if (!checkLocationAvailable()) {
                                AppUtility().showGpsOffNotification(mContext)
                            } else if (checkLocationAvailable()) {
                                fetchLocationFromListener(false)
                            }
                        } else if (msg.what === 3 && locationReceived) {
                            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "best location accuracy " + bestLocation!!.accuracy)

                            if (bestLocation!!.accuracy <= 100) {
                                getCompleteAddressString(bestLocation!!, AppConstant.LOCATION_UPDATE_TYPE_CURRENT, forceUpdateLoation)
                            } else if (checkLocationAvailable() && bestLocation!!.accuracy > 100) {
                                fetchLocationFromListener(false)
                            }
                            if (mGoogleApiClient!!.isConnected)
                                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationCallback)


                        }

                        try {
                            if (mGoogleApiClient!!.isConnected)
                                mGoogleApiClient!!.disconnect()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        super.handleMessage(msg)
                    }
                }

                locationReceived = false
                if (mGoogleApiClient!!.isConnected) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                            mLocationRequest, mLocationCallback, null)
                }
                if (forceUpdateLoation) {
                    listHandler!!.sendEmptyMessageDelayed(2, AppConstant.LOCATION_SYNC_TIMEOUT_FORCE);
                } else {
                    listHandler!!.sendEmptyMessageDelayed(2, AppConstant.LOCATION_SYNC_TIMEOUT);

                }

            } catch (e: SecurityException) {
                e.printStackTrace()

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        override fun onConnected(p0: Bundle?) {
            geLocation()
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "mGoogleApiClient connected")
        }

        override fun onConnectionSuspended(p0: Int) {
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "mGoogleApiClient onConnectionSuspended")

            try {
                if (listHandler != null) {
                    listHandler!!.removeCallbacks { this }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onConnectionFailed(p0: ConnectionResult) {
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "mGoogleApiClient onConnectionFailed")

            try {
                if (listHandler != null) {
                    listHandler!!.removeCallbacks { this }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

    public fun getCompleteAddressString(location: Location, locationType: String, forceUpdateLoation: Boolean): List<SearchResult> {
        //
        var LATITUDE: Double = location.latitude
        var LONGITUDE: Double = location.longitude
        var lastRowId: Long = 0;

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
            insert = true;
            pref.setLong(System.currentTimeMillis(), AppConstant.SP_KEY_FIRST_TIME)

        } else {
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                    "Distance prev and curr" + previousLocation.distanceTo(currentLocation))
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                    "Distance curr and DB" + currentLocation.distanceTo(dbLastLocation))
            if (previousLocation.distanceTo(currentLocation) < AppConstant.MIN_DISTANCE_RANGE) {
                insert = true;
            } else if (!insert && forceUpdateLoation) {
                insert = true
                pref.setLong(System.currentTimeMillis(), AppConstant.SP_KEY_LAST_TIMER_TIME)
            } else if (previousLocation.distanceTo(currentLocation) > AppConstant.MIN_DISTANCE_RANGE) {
                pref.setLong(System.currentTimeMillis(), AppConstant.SP_KEY_LAST_TIMER_TIME)
            }

            //
            if (lastDBLocation != null && currentLocation.distanceTo(dbLastLocation) < AppConstant.MIN_DISTANCE_RANGE) {
                if (lastDBLocation.accuracy > currentLocation.accuracy && lastDBLocation.isPreferred == 0) {
                    lastRowId = lastDBLocation.rowID;
                    insert = true
                } else {
                    insert = false
                    Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "not preferred or accuracy")

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
            visitedLocationInformation.rowID = lastRowId;
            visitedLocationInformation.userId = 0;
            visitedLocationInformation.latitude = LATITUDE
            visitedLocationInformation.longitude = LONGITUDE
            visitedLocationInformation.locationProvider = currentLocation.provider
            visitedLocationInformation.accuracy = currentLocation.accuracy
            var fromTime: Long = System.currentTimeMillis()
            val toTime = System.currentTimeMillis()
            // if (lastDBLocation == null) {
            fromTime = pref.getLong(AppConstant.SP_KEY_LAST_TIMER_TIME)
            //}
            visitedLocationInformation.toTime = toTime
            visitedLocationInformation.fromTime = fromTime
            visitedLocationInformation.locationRequestType = locationType
            //
            val insertedId = DataBaseController(mContext).insertVisitedLocation(visitedLocationInformation);
            //
            if (insertedId > 0L && mConnectionDetector.isNetworkAvailable()) {
                retroCallImplementor!!.getAllPlaces(LATITUDE.toString() + "," + LONGITUDE.toString(),
                        handler, location, locationType, insertedId)
            } else {
                Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Nearby api not called")
                Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Inserid : " + insertedId)
                Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Net avail = " + mConnectionDetector.isNetworkAvailable())

            }
            //
            pref.setLong(System.currentTimeMillis(), AppConstant.SP_KEY_SPENT_TIME)
            pref.setFloat(currentLocation.accuracy, AppConstant.SP_KEY_ACCURACY)
        } else {
            pref.setLong(System.currentTimeMillis(), AppConstant.SP_KEY_SPENT_TIME)
        }

        //
        var visitedLocationList = mDataBaseController.readAllVisitedLocation()

        return (visitedLocationList as List<SearchResult>?)!!
    }

    /**
     * Method to add address into database
     */
    fun addAddressIntoDataBase(resultPlace: Result, currentLocation: Location, locationType: String,
                               mPlacesList: List<Result>, rowId: Long, isPreferred: Int) {
        //
        var result: Long = 0
        try {
            var pref = MySharedPref.getinstance(mContext!!.applicationContext);
            var geocoder = Geocoder(mContext, Locale.getDefault())
            val address = resultPlace.name;
            val vicinity = resultPlace.vicinity
            val placeId = resultPlace.placeId
            val photoUrl = resultPlace.photos.toString()
            val nearByPlaces = "";
            val isAddressSet = ADDRESS_SET;
            var LATITUDE: Double = currentLocation.latitude
            var LONGITUDE: Double = currentLocation.longitude
            val addresses: List<Address>
            //
            addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses == null || addresses.size == 0) {
                return null!!;
            }
            //
            val city = addresses[0].getLocality()
            val state = addresses[0].getAdminArea()
            val country = addresses[0].getCountryName()
            val postalCode = addresses[0].getPostalCode()
            val knownName = addresses[0].getFeatureName() // Only if available else return NULL
            val toTime = System.currentTimeMillis()
            val locationProvider = currentLocation.provider;
            var fromTime: Long = System.currentTimeMillis()

            // set values for visited location information
            var visitedLocationInformation = VisitedLocationInformation("NA")
            visitedLocationInformation.userId = 1
            visitedLocationInformation.latitude = LATITUDE
            visitedLocationInformation.longitude = LONGITUDE
            visitedLocationInformation.address = address
            visitedLocationInformation.city = city
            visitedLocationInformation.state = state
            visitedLocationInformation.country = country
            if (postalCode != null)
                visitedLocationInformation.postalCode = postalCode
            visitedLocationInformation.knownName = knownName
            visitedLocationInformation.toTime = toTime
            visitedLocationInformation.fromTime = fromTime
            visitedLocationInformation.locationProvider = locationProvider
            visitedLocationInformation.rowID = rowId
            visitedLocationInformation.locationRequestType = locationType
            visitedLocationInformation.vicinity = vicinity
            visitedLocationInformation.placeId = placeId
            if (photoUrl != null)
                visitedLocationInformation.photoUrl = photoUrl
            visitedLocationInformation.nearByPlacesIds = nearByPlaces
            visitedLocationInformation.isAddressSet = isAddressSet
            visitedLocationInformation.accuracy = currentLocation.accuracy
            visitedLocationInformation.isPreferred = isPreferred

            val updatedRow = mDataBaseController.updateVisitedLocation(
                    visitedLocationInformation, rowId)
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                    "Updating address for row id -- " + updatedRow)

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

                var minDistance: Float = 0.0F;
                var selected: Int = 0;
                var isPreferred = 0
                for (it in places) {
                    //Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, it.name);
                    var tempLoc = Location(LocationManager.GPS_PROVIDER);
                    tempLoc.latitude = it.geometry.location.lat.toDouble()
                    tempLoc.longitude = it.geometry.location.lng.toDouble()
                    val distance = location.distanceTo(tempLoc)
                    // Just to pick first prominent place within 10 metre
                    if (distance < AppConstant.RADIUS_NEARBY_SEARCH && selected == 0) {
                        minDistance = distance
                        result = it
                        selected += 1
                    }
                    if (DataBaseController(mContext).isPreferredLocation(it.placeId)) {
                        minDistance = distance
                        result = it
                        selected += 1
                        isPreferred = 1
                        break
                    }

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
                addAddressIntoDataBase(result!!, location, locationType, places, rowId, isPreferred)
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
                    var pref = MySharedPref.getinstance(mContext)
                    val address = it.name
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
                    val locationProvider = "NA"
                    val locationType = "NA"
                    val toTime: Long = System.currentTimeMillis()
                    // set values for visited location information
                    var visitedLocationInformation = VisitedLocationInformation("NA")
                    visitedLocationInformation.userId = 1
                    visitedLocationInformation.latitude = LATITUDE
                    visitedLocationInformation.longitude = LONGITUDE
                    visitedLocationInformation.address = address
                    visitedLocationInformation.city = city
                    visitedLocationInformation.state = state
                    visitedLocationInformation.country = country
                    if (postalCode != null)
                        visitedLocationInformation.postalCode = postalCode
                    visitedLocationInformation.knownName = knownName
                    visitedLocationInformation.toTime = toTime
                    visitedLocationInformation.fromTime = fromTime
                    visitedLocationInformation.locationProvider = locationProvider
                    visitedLocationInformation.rowID = 0
                    visitedLocationInformation.locationRequestType = locationType
                    visitedLocationInformation.vicinity = vicinity
                    visitedLocationInformation.placeId = placeId
                    if (photoUrl != null)
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

