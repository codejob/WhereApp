package com.where.prateekyadav.myapplication.view

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.where.prateekyadav.myapplication.R
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.where.prateekyadav.myapplication.Util.MySharedPref
import com.where.prateekyadav.myapplication.appinterface.ConfirmationListener
import com.where.prateekyadav.myapplication.database.DataBaseController
import com.where.prateekyadav.myapplication.dialog.DialogConfirmationAlert
import com.where.prateekyadav.myapplication.modal.NearByPlace
import com.where.prateekyadav.myapplication.modal.SearchResult
import java.util.*


/**
 * Created by Infobeans on 1/23/2018.
 */
class NearByActivity : AppCompatActivity(), ConfirmationListener {


    var mNearByList = ArrayList<NearByPlace>()
    var mSearchResult: SearchResult? = null
    var listView: ListView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby)
        mSearchResult = intent.getSerializableExtra("SearchResult") as SearchResult
        mNearByList = mSearchResult!!.listNearByPlace as ArrayList<NearByPlace>
        listView = findViewById<ListView>(R.id.lv_nearby)
        if (mNearByList != null) {

            listView!!.adapter = NearByAdapter(mNearByList)

        }
        listView!!.emptyView = findViewById(R.id.tv_no_records) as TextView
        listView!!.onItemClickListener = object : AdapterView.OnItemClickListener {

            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                try {

                    val confirmationDialog = DialogConfirmationAlert(this@NearByActivity!!, this@NearByActivity)
                    confirmationDialog.showConfirmationDialog(this@NearByActivity!!.getString(R.string.str_alert_message_update_location),
                            position, AppConstant.REQUEST_CODE_REPLACE);


                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {

                }

            }
        }

    }


    inner class NearByAdapter() : BaseAdapter() {
        var mContext: Context = this@NearByActivity;
        var inflater: LayoutInflater? = null
        var pref: MySharedPref? = null

        constructor(locationList: List<NearByPlace>) : this() {
            inflater = LayoutInflater.from(mContext);
            pref = MySharedPref.getinstance(mContext.applicationContext)
            pref!!.getFloat(AppConstant.SP_KEY_ACCURACY)

        }

        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            val mViewHolder: MyViewHolder
            var convertView = view
            if (convertView == null) {
                convertView = inflater!!.inflate(R.layout.layout_list_item_nearby, parent, false)
                mViewHolder = MyViewHolder(convertView)
                convertView.setTag(mViewHolder)
            } else {
                mViewHolder = convertView.getTag() as MyViewHolder
            }


            mViewHolder.tvNBAddress.text = mNearByList!!.get(position).address
            mViewHolder.tvNBVicinity.text = mNearByList!!.get(position).vicinity


            return convertView!!
        }

        override fun getItem(p0: Int): Any {
            return mNearByList!!.get(p0)
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return mNearByList!!.size
        }

        private inner class MyViewHolder(item: View) {
            internal var tvNBAddress: TextView
            internal var tvNBVicinity: TextView

            init {
                tvNBAddress = item.findViewById(R.id.tv_nearby_address) as TextView
                tvNBVicinity = item.findViewById(R.id.tv_nearby_location_vicinity) as TextView
            }
        }
    }

    override fun onYes(index: Int) {
    }

    override fun onNo() {
    }

    override fun onYes(index: Int, requestType: Int) {

        if (requestType == AppConstant.REQUEST_CODE_REPLACE) {
            DataBaseController(this@NearByActivity).updateVisitedLocationWithNearBy(mSearchResult!!.visitResults.visitedLocationInformation,
                    mNearByList.get(index))
            setResult(1)
            finish()
        }

    }
}