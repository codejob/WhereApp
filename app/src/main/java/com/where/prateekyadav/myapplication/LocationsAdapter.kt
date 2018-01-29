package com.where.prateekyadav.myapplication

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.where.prateekyadav.myapplication.database.VisitedLocationInformation
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.where.prateekyadav.myapplication.Util.AppUtility
import com.where.prateekyadav.myapplication.Util.MySharedPref
import com.where.prateekyadav.myapplication.appinterface.ConfirmationListener
import com.where.prateekyadav.myapplication.database.DBContract
import com.where.prateekyadav.myapplication.database.DataBaseController
import com.where.prateekyadav.myapplication.dialog.DialogConfirmationAlert
import com.where.prateekyadav.myapplication.modal.SearchResult
import com.where.prateekyadav.myapplication.view.NearByActivity
import com.where.prateekyadav.myapplication.view.VisitedActivity
import java.io.Serializable
import java.util.*


/**
 * Created by Infobeans on 1/11/2018.
 */
class LocationsAdapter() : BaseAdapter(), ConfirmationListener {

    var mContext: Context? = null;
    var mLocationList: MutableList<SearchResult>? = null;
    var inflater: LayoutInflater? = null
    var pref: MySharedPref? = null

    constructor(context: Context, locationList: MutableList<SearchResult>) : this() {
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
            convertView = inflater!!.inflate(R.layout.row_item_visited_location, parent, false)
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

        mViewHolder.btnAllVisits.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val listItem = getItem(position) as SearchResult
                val visit = listItem!!.visitResults.visitedLocationInformation
                var intent = Intent(mContext, VisitedActivity::class.java)
                intent.putExtra(DBContract.VisitedLocationData.COLUMN_PLACE_ID, visit.placeId)
                mContext!!.startActivity(intent)
            }

        })

        mViewHolder.btnChooseNearBy.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val listItem = getItem(position) as SearchResult
                val visit = listItem!!.listNearByPlace as Serializable
                var intent = Intent(mContext, NearByActivity::class.java)
                intent.putExtra("SearchResult", listItem)
                mContext!!.startActivity(intent)
            }

        })

        mViewHolder.btnDelete.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                // Delete a visited place code/////

                val confirmationDialog = DialogConfirmationAlert(mContext!!, this@LocationsAdapter)
                confirmationDialog.showConfirmationDialog(mContext!!.getString(R.string.str_alert_message_delete_location),
                        position, AppConstant.REQUEST_CODE_DELETE);


            }

        })

        val calToTime = Calendar.getInstance();
        val calFromTime = Calendar.getInstance();
        calToTime.timeInMillis = mLocationList!!.get(position).visitResults.visitedLocationInformation.toTime
        calFromTime.timeInMillis = mLocationList!!.get(position).visitResults.visitedLocationInformation.fromTime

        var addresss =

                """   Name:  ${mLocationList!!.get(position).visitResults.visitedLocationInformation.address}

                 Visinity:   ${
                mLocationList!!.get(position).visitResults.visitedLocationInformation.vicinity

                }

                  No of visits:   ${mLocationList!!.get(position).visitResults.noOfVisits}

              ${AppUtility().decorateFromAndToTime(mLocationList!!.get(position).visitResults.visitedLocationInformation.fromTime,
                        mLocationList!!.get(position).visitResults.visitedLocationInformation.toTime)}
                 """

        /*  Req Type:=> ${mLocationList!!.get(position).visitResults.visitedLocationInformation.locationRequestType}
          Provider :=> ${mLocationList!!.get(position).visitResults.visitedLocationInformation.locationProvider}
          Last accuracy :=> ${pref!!.getFloat(AppConstant.SP_KEY_ACCURACY)}
          Lat:=> ${mLocationList!!.get(position).visitResults.visitedLocationInformation.latitude}
          Long:=> ${mLocationList!!.get(position).visitResults.visitedLocationInformation.longitude} """*/




        mViewHolder.tvTitle.text = addresss

        //// use  for making search text bold
        /* if (mLocationList!!.get(position).visitResults.searchString != null) {
             mViewHolder.tvTitle.text = AppUtility().makeSectionOfTextBold(mLocationList!!.get(position).visitResults.visitedLocationInformation.vicinity,
                     mLocationList!!.get(position).visitResults.searchString!!)
         } else {
             mViewHolder.tvTitle.text = addresss

         }*/

        return convertView!!
    }

    private fun deleteLocationFromDatabase(position: Int) {
        val listItem = getItem(position) as SearchResult
        val visit = listItem!!.visitResults.visitedLocationInformation
        val visitList = ArrayList<VisitedLocationInformation>()
        visitList.add(visit)
        DataBaseController(mContext).deleteVisitedPlaceAndUniqueNearByForIt(visitList)
        this@LocationsAdapter.mLocationList!!.removeAt(position)
        this@LocationsAdapter.notifyDataSetChanged()
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
        internal var btnAllVisits: Button
        internal var btnChooseNearBy: Button
        internal var btnDelete: ImageButton

        init {
            tvTitle = item.findViewById(R.id.tvTitle) as TextView
            btnAllVisits = item.findViewById(R.id.btn_all_visits) as Button
            btnChooseNearBy = item.findViewById(R.id.btn_choose_nearby) as Button
            btnDelete = item.findViewById(R.id.btn_delete) as ImageButton

        }
    }


    override fun onYes(index: Int) {
    }

    override fun onNo() {

    }

    override fun onYes(index: Int, requestType: Int) {

        if (requestType == AppConstant.REQUEST_CODE_DELETE) {
            deleteLocationFromDatabase(index)
        }

    }

}

