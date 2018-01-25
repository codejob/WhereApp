package com.where.prateekyadav.myapplication.view

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.where.prateekyadav.myapplication.R
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.where.prateekyadav.myapplication.Util.MySharedPref
import com.where.prateekyadav.myapplication.database.DBContract
import com.where.prateekyadav.myapplication.database.DataBaseController
import com.where.prateekyadav.myapplication.database.DatabaseHelper
import com.where.prateekyadav.myapplication.database.VisitedLocationInformation
import com.where.prateekyadav.myapplication.modal.SearchResult
import java.util.*


/**
 * Created by Infobeans on 1/23/2018.
 */
class VisitedActivity : AppCompatActivity() {
    var mLocationList: List<VisitedLocationInformation>? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visited)

        if (intent.hasExtra(DBContract.VisitedLocationData.COLUMN_PLACE_ID)){
            var placeID = intent.getStringExtra(DBContract.VisitedLocationData.COLUMN_PLACE_ID)
            mLocationList = DataBaseController(this).getVisitedLocationsFromPlaceid(placeID)
            val listView:ListView = findViewById<ListView>(R.id.lv_visited)
            listView.adapter = LocationsAdapter(mLocationList!!)
        }

    }

    inner class LocationsAdapter() : BaseAdapter() {
        var mContext: Context? = this@VisitedActivity;

        var inflater: LayoutInflater? = null
        var pref: MySharedPref? = null

        constructor(locationList: List<VisitedLocationInformation>) : this() {
            inflater = LayoutInflater.from(mContext);
            pref = MySharedPref.getinstance(mContext!!.applicationContext)
            pref!!.getFloat(AppConstant.SP_KEY_ACCURACY)

        }

        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            val mViewHolder: MyViewHolder
            var convertView = view
            if (convertView == null) {
                convertView = inflater!!.inflate(R.layout.layout_list_item, parent, false)
                mViewHolder = MyViewHolder(convertView)
                convertView.setTag(mViewHolder)
            } else {
                mViewHolder = convertView.getTag() as MyViewHolder
            }

            val calToTime = Calendar.getInstance();
            val calFromTime = Calendar.getInstance();
            calToTime.timeInMillis = mLocationList!!.get(position).toTime
            calFromTime.timeInMillis = mLocationList!!.get(position).fromTime

            var addresss =

                    """   Name:  ${mLocationList!!.get(position).address}

                 Visinity:   ${mLocationList!!.get(position).vicinity}

                 FROM time:=> ${calFromTime.time}

                 TO time:=> ${calToTime.time}

                 Req Type:=> ${mLocationList!!.get(position).locationRequestType}
                 Provider :=> ${mLocationList!!.get(position).locationProvider}
                 Last accuracy :=> ${mLocationList!!.get(position).accuracy}
                 Lat:=> ${mLocationList!!.get(position).latitude}
                 Long:=> ${mLocationList!!.get(position).longitude} """




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


}