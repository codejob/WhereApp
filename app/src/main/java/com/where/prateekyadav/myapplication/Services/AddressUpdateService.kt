package com.where.prateekyadav.myapplication.Services

import android.app.IntentService
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
import com.where.prateekyadav.myapplication.UpdateLocation
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.where.prateekyadav.myapplication.Util.ConnectionDetector
import com.where.prateekyadav.myapplication.Util.MySharedPref
import com.where.prateekyadav.myapplication.database.DataBaseController
import com.where.prateekyadav.myapplication.database.VisitedLocationInformation
import com.where.prateekyadav.myapplication.modal.SearchResult
import com.where.prateekyadav.myapplication.search.model.placesdetails.Result
import com.where.prateekyadav.myapplication.search.network.RetroCallImplementor
import com.where.prateekyadav.myapplication.search.network.RetroCallIneractor
import java.util.*

/**
 * Created by Infobeans on 21-Jul-16.
 */
class AddressUpdateService : IntentService("ADDRESS UPDATE"), UpdateLocation {

    private var mContext: Context? = null
    private var handler: AddressUpdateService.Handleupdate? = null
    private var updateLocation: UpdateLocation? = null;
    private lateinit var mConnectionDetector: ConnectionDetector

    companion object {
        var RUNNING: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()
        RUNNING = true
        Log.d("service", "oncreate service")

    }

    override fun onDestroy() {
        super.onDestroy()
        RUNNING=false
        Log.d("service", "onDestroy service")
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStart(intent: Intent, startId: Int) {
        super.onStart(intent, startId)
    }

    override fun onHandleIntent(intent: Intent?) {
        RUNNING = true
        mContext = this
        handler = Handleupdate()
        updateLocation = this;
        mConnectionDetector = ConnectionDetector.getInstance(this)
        callGetPlacesAPI()
        Log.d("service", "onHandleIntent service")
    }

    /*override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("service", "onStartCommand")
        callGetPlacesAPI()
        //
        return Service.START_STICKY
    }*/

    fun callGetPlacesAPI() {
        try {
            val mList = DataBaseController(mContext).getListOfNotUpdatedVisitedLocation();
            if (mList != null && mList.size > 0) {
                val it = mList.get(0)
                if (mConnectionDetector.isNetworkAvailable()) {
                    // Add address code here
                    val retroCallImplementor = RetroCallImplementor()
                    // var retroCallImplementor = RetroCallImplementor()
                    val latitude = it.latitude
                    val longitude = it.longitude
                    val location: Location = Location(it.locationProvider)
                    location.latitude = latitude
                    location.longitude = longitude
                    location.accuracy = it.accuracy
                    retroCallImplementor!!.getAllPlaces(latitude.toString() + "," + longitude.toString(),
                            handler, location, location.provider, it.rowID)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        RUNNING=false
        Log.d("service", "onTaskRemoved service")
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


        override fun updatePlacesWithId(places: List<Result>, location: Location, locationType: String, rowId: Long) {

            try {
                var result: Result? = null;

                var minDistance: Float = 0.0F;
                var selected: Int = 0;
                val isPreferred = 0
                for(it in places) {
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
                    if(DataBaseController(mContext).isPreferredLocation(it.placeId)){
                        minDistance = distance
                        result = it
                        selected += 1
                        break
                        isPreferred=1
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
                var locationHelper = LocationHelper.getInstance(mContext, updateLocation!!)
                locationHelper.addAddressIntoDataBase(result!!, location, locationType, places, rowId,isPreferred)

                callGetPlacesAPI()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        //
        override fun updatePlaces(places: MutableList<Result>?, location: Location?, locationType: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun updatePlaceDetails(place: Result) {

        }


        override fun onFailure() {

        }
    }


    override fun updateLocationAddressList(addressList: List<SearchResult>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
