package com.where.prateekyadav.myapplication

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.where.prateekyadav.myapplication.database.VisitedLocationInformation
import android.view.LayoutInflater
import android.widget.TextView


/**
 * Created by Infobeans on 1/11/2018.
 */
class LocationsAdapter() : BaseAdapter() {
    var mContext: Context? = null;
    var mLocationList: List<VisitedLocationInformation>? = null;
    var inflater: LayoutInflater? = null

    constructor(context: Context, locationList: List<VisitedLocationInformation>) : this() {
        mContext = context;
        mLocationList = locationList;
        inflater = LayoutInflater.from(context);

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
        var addresss =
                   """
                  Name:  ${mLocationList!!.get(position).address}

                 Visinity:   ${mLocationList!!.get(position).knownName}

                    Stay time:=> ${mLocationList!!.get(position).stayTime} minutes

                   """

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