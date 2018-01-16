package com.where.prateekyadav.myapplication.Util

import android.R.id.edit
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences


/**
 * Created by Infobeans on 1/11/2018.
 */
class MySharedPref {
    var mPref: SharedPreferences
    var editor: SharedPreferences.Editor
    open var myPrefName: String = "mypref";

     constructor(mContext: Context?) {
        mPref = mContext!!.getSharedPreferences(myPrefName, Context.MODE_PRIVATE)
        editor = mPref.edit()
    }


    fun setLocation(latitude: Double, longitude: Double) {
        val editor = mPref.edit()
        editor.putFloat(Constant.sharedKeyLatitude, latitude.toFloat())
        editor.putFloat(Constant.sharedKeyLongitude, longitude.toFloat())
        editor.apply()
    }
    fun setLong(long: Long) {
        val editor = mPref.edit()
        editor.putLong(Constant.sharedKeyLong, long)
        editor.apply()
    }

    fun getLatitude(): Double {
        return mPref.getFloat(Constant.sharedKeyLatitude, 0.0F).toDouble()
    }

    fun getLongitude(): Double {
        return mPref.getFloat(Constant.sharedKeyLongitude, 0.0F).toDouble()
    }
    fun getLong(): Long {
        return mPref.getLong(Constant.sharedKeyLong, 0L)
    }
}