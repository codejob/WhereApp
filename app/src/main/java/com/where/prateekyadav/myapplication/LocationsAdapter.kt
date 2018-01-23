package com.where.prateekyadav.myapplication

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.where.prateekyadav.myapplication.database.VisitedLocationInformation
import android.view.LayoutInflater
import android.widget.TextView
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.where.prateekyadav.myapplication.Util.MySharedPref
import com.where.prateekyadav.myapplication.modal.SearchResult
import java.util.*


/**
 * Created by Infobeans on 1/11/2018.
 */
class LocationsAdapter() : BaseAdapter() {
    var mContext: Context? = null;
    var mLocationList: List<SearchResult>? = null;
    var inflater: LayoutInflater? = null
    var pref: MySharedPref? = null

    constructor(context: Context, locationList: List<SearchResult>) : this() {
        mContext = context;
        mLocationList = locationList;
        inflater = LayoutInflater.from(context);
        pref = MySharedPref.getinstance(mContext!!.applicationContext)
        pref!!.getFloat(AppConstant.SP_KEY_ACCURACY)

    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val mViewHolder: MyViewHolder
        var convertView = view
        if (convertView == null) {
            convertView = inflater!!.inflate(R.layout.layout_location_list_item, parent, false)
            mViewHolder = MyViewHolder(convertView)
            convertView.setTag(mViewHolder)
        } else {
            mViewHolder = convertView.getTag() as MyViewHolder
        }
        if (mLocationList!!.get(position).visitResults.showFromNearBy) {
            val nearAddress = mLocationList!!.get(position).visitResults.nearByPlaceIDToShow!!.address
            val nearVisinity = mLocationList!!.get(position).visitResults.nearByPlaceIDToShow!!.vicinity
            mLocationList!!.get(position).visitResults.visitedLocationInformation.address = nearAddress
            mLocationList!!.get(position).visitResults.visitedLocationInformation.vicinity = nearVisinity

        } else {

        }
        val calToTime = Calendar.getInstance();
        val calFromTime = Calendar.getInstance();
        calToTime.timeInMillis = mLocationList!!.get(position).visitResults.visitedLocationInformation.toTime
        calFromTime.timeInMillis = mLocationList!!.get(position).visitResults.visitedLocationInformation.fromTime

        var addresss =

                """   Name:  ${mLocationList!!.get(position).visitResults.visitedLocationInformation.address}

                 Visinity:   ${mLocationList!!.get(position).visitResults.visitedLocationInformation.vicinity}

                 No of visits:   ${mLocationList!!.get(position).visitResults.noOfVisits}

                 FROM time:=> ${calFromTime.time}

                 TO time:=> ${calToTime.time}

                 Req Type:=> ${mLocationList!!.get(position).visitResults.visitedLocationInformation.locationRequestType}
                 Provider :=> ${mLocationList!!.get(position).visitResults.visitedLocationInformation.locationProvider}
                 Last accuracy :=> ${pref!!.getFloat(AppConstant.SP_KEY_ACCURACY)}
                 Lat:=> ${mLocationList!!.get(position).visitResults.visitedLocationInformation.latitude}
                 Long:=> ${mLocationList!!.get(position).visitResults.visitedLocationInformation.longitude} """




        mViewHolder.tvTitle.setText(addresss)

        return convertView!!
    }

    override fun getItem(p0: Int): Any {
        return mLocationList!!.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return mLocationList!!.size
    }

    private inner class MyViewHolder(item: View) {
        internal var tvTitle: TextView

        init {
            tvTitle = item.findViewById(R.id.tvTitle) as TextView
        }
    }


}

