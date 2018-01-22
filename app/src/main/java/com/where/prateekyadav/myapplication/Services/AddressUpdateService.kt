package com.where.prateekyadav.myapplication.Services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.where.prateekyadav.myapplication.Util.MySharedPref
import com.where.prateekyadav.myapplication.database.DataBaseController
import com.where.prateekyadav.myapplication.database.VisitedLocationInformation
import com.where.prateekyadav.myapplication.search.model.placesdetails.Result
import java.util.*

/**
 * Created by Infobeans on 21-Jul-16.
 */
class AddressUpdateService : Service() {

    private var mContext: Context? = null

    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {

        }
    }


    override fun onCreate() {
        super.onCreate()
        Log.d("service", "oncreate service")
        mContext = this
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
            result = DataBaseController(mContext).insertVisitedLocation(
                    VisitedLocationInformation(userId = 1, latitude = LATITUDE,
                            longitude = LONGITUDE, address = address, city = city,
                            state = state, country = country, postalCode = postalCode,
                            knownName = knownName, toTime = toTime, fromTime = fromTime,
                            locationProvider = locationProvider, rowID = 0,
                            locationRequestType = locationType, vicinity = vicinity,
                            placeId = placeId, photoUrl = photoUrl, nearByPlacesIds = nearByPlaces,
                            isAddressSet = isAddressSet))
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
