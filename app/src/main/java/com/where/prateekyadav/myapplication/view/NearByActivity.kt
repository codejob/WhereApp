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
import com.where.prateekyadav.myapplication.modal.NearByPlace
import com.where.prateekyadav.myapplication.modal.SearchResult
import java.util.*


/**
 * Created by Infobeans on 1/23/2018.
 */
class NearByActivity : AppCompatActivity() {
    var mNearByList = ArrayList<NearByPlace>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby)
        mNearByList = intent.getSerializableExtra("NearByList") as ArrayList<NearByPlace>
        if (mNearByList != null) {
            val listView = findViewById<ListView>(R.id.lv_nearby)
            listView.adapter = NearByAdapter(mNearByList)
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
                convertView = inflater!!.inflate(R.layout.layout_list_item, parent, false)
                mViewHolder = MyViewHolder(convertView)
                convertView.setTag(mViewHolder)
            } else {
                mViewHolder = convertView.getTag() as MyViewHolder
            }


            var addresss =

                    """   Name:  ${mNearByList!!.get(position).address}

                 Visinity:   ${mNearByList!!.get(position).vicinity}

                 Req Type:=> ${mNearByList!!.get(position).locationRequestType}
                 Last accuracy :=> ${pref!!.getFloat(AppConstant.SP_KEY_ACCURACY)}
                 Lat:=> ${mNearByList!!.get(position).latitude}
                 Long:=> ${mNearByList!!.get(position).longitude} """




            mViewHolder.tvTitle.setText(addresss)

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
            internal var tvTitle: TextView

            init {
                tvTitle = item.findViewById(R.id.tvTitle) as TextView
            }
        }

    }
}