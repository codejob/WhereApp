package com.where.prateekyadav.myapplication.Services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import com.where.prateekyadav.myapplication.LocationHelper
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.where.prateekyadav.myapplication.Util.MySharedPref
import com.where.prateekyadav.myapplication.database.DataBaseController
import com.where.prateekyadav.myapplication.database.VisitedLocationInformation
import com.where.prateekyadav.myapplication.search.model.placesdetails.Result
import com.where.prateekyadav.myapplication.search.network.RetroCallImplementor
import com.where.prateekyadav.myapplication.search.network.RetroCallIneractor
import java.util.*

/**
 * Created by Infobeans on 21-Jul-16.
 */
class AddressUpdateService : Service() {

    private var mContext: Context? = null
    private var handler: AddressUpdateService.Handleupdate? = null

    private val testHandler = object : Handler() {
        override fun handleMessage(msg: Message) {

        }
    }


    override fun onCreate() {
        super.onCreate()
        Log.d("service", "oncreate service")
        mContext = this
        handler=Handleupdate()

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("service", "onDestroy service")
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStart(intent: Intent, startId: Int) {
        super.onStart(intent, startId)
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("service", "onStartCommand")
        val mList=DataBaseController(mContext).getListOfNotUpdatedVisitedLocation();
        mList!!.forEach {
            // TODO : write add address code here
            val retroCallImplementor= RetroCallImplementor()
           // var retroCallImplementor = RetroCallImplementor()
            val latitude=it.latitude
            val longitude=it.longitude
            val location : Location = Location(it.locationProvider)
            location.latitude=latitude
            location.longitude=longitude
            retroCallImplementor!!.getAllPlaces(latitude.toString() + "," + longitude.toString(), handler, location, location.provider)

        }
        //
        return Service.START_STICKY
    }


    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        try {
            //deleteExtractedFileFolder(mExtractedFolderPath);
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }



    /*
   This will handle the response from the API
   we are setting the adapter here and update the recycler view.
    */
    internal inner class Handleupdate : RetroCallIneractor {

        override fun updatePlaces(places: List<Result>, location: Location, locationType: String) {

            try {
                var result: Result? = null;

                var minDistance: Float = 0.0F;
                var pos: Int = 0;
                places.forEach {
                    //Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, it.name);
                    var tempLoc = Location(LocationManager.GPS_PROVIDER);
                    tempLoc.latitude = it.geometry.location.lat.toDouble()
                    tempLoc.longitude = it.geometry.location.lng.toDouble()
                    val distance = location.distanceTo(tempLoc)
                    // Just to pick first prominent place within 10 metre
                    if (distance < location.accuracy && pos == 0) {
                        minDistance = distance
                        result = it
                        pos += 1
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
                //
                insertAddress(result!!, location, locationType, places)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        override fun updatePlaceDetails(place: Result) {

        }


        override fun onFailure() {

        }
    }
    //
    fun insertAddress(resultPlace: Result, currentLocation: Location, locationType: String, mPlacesList: List<Result>) {
        //
        var result: Boolean = false
        try {
            var pref = MySharedPref.getinstance(mContext);
            var geocoder = Geocoder(mContext, Locale.getDefault())
            val address = resultPlace.name;

            val vicinity = resultPlace.vicinity
            val placeId = resultPlace.placeId
            val photoUrl = resultPlace.photos.toString()
            val nearByPlaces = "";
            val isAddressSet = 1;
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
            val fromTime = System.currentTimeMillis()
            val locationProvider = currentLocation.provider;
            val toTime: Long = System.currentTimeMillis()
            //
            result = DataBaseController(mContext).updateVisitedLocation(
                    VisitedLocationInformation(userId = 1, latitude = LATITUDE,
                            longitude = LONGITUDE, address = address, city = city,
                            state = state, country = country, postalCode = postalCode,
                            knownName = knownName, toTime = toTime, fromTime = fromTime,
                            locationProvider = locationProvider, rowID = 0,
                            locationRequestType = locationType, vicinity = vicinity,
                            placeId = placeId, photoUrl = photoUrl, nearByPlacesIds = nearByPlaces,
                            isAddressSet = isAddressSet),1)
            //
            pref.setLong(System.currentTimeMillis(), AppConstant.SP_KEY_SPENT_TIME)
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,

                    "Location inserted")

        } catch (e: Exception) {
            e.printStackTrace()
        }
        println(result)

    }

}
