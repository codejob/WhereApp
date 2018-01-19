package com.where.prateekyadav.myapplication.Util

import android.content.Context
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
        editor.putFloat(AppConstant.sharedKeyLatitude, latitude.toFloat())
        editor.putFloat(AppConstant.sharedKeyLongitude, longitude.toFloat())
        editor.apply()
    }
    fun setLong(long: Long,key:String) {
        val editor = mPref.edit()
        editor.putLong(key, long)
        editor.apply()
    }

    fun getLatitude(): Double {
        return mPref.getFloat(AppConstant.sharedKeyLatitude, 0.0F).toDouble()
    }

    fun getLongitude(): Double {
        return mPref.getFloat(AppConstant.sharedKeyLongitude, 0.0F).toDouble()
    }
    fun getLong(key:String): Long {
        return mPref.getLong(key, 0L)
    }
}