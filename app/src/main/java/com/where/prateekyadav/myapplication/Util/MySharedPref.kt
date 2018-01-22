package com.where.prateekyadav.myapplication.Util

import android.content.Context
import android.content.SharedPreferences


/**
 * Created by Infobeans on 1/11/2018.
 */
class MySharedPref {

    open var myPrefName: String = "mypref";

    companion object {
        var myPref: MySharedPref? = null
        var mPref: SharedPreferences?=null
        fun getinstance(context: Context?): MySharedPref {
            if (myPref == null) {
                myPref = MySharedPref(context)
            }
            return myPref as MySharedPref

        }
    }

    private constructor(mContext: Context?) {
        mPref = mContext!!.getSharedPreferences(myPrefName, Context.MODE_PRIVATE)
    }


    fun setLocation(latitude: Double, longitude: Double) {
        val editor = mPref!!.edit()
        editor.putFloat(AppConstant.sharedKeyLatitude, latitude.toFloat())
        editor.putFloat(AppConstant.sharedKeyLongitude, longitude.toFloat())
        editor.apply()
    }


    fun getLatitude(): Double {
        return mPref!!.getFloat(AppConstant.sharedKeyLatitude, 0.0F).toDouble()
    }

    fun getLongitude(): Double {
        return mPref!!.getFloat(AppConstant.sharedKeyLongitude, 0.0F).toDouble()
    }

    fun getLong(key: String): Long {
        return mPref!!.getLong(key, 0L)
    }

    fun setLong(long: Long, key: String) {
        val editor = mPref!!.edit()
        editor.putLong(key, long)
        editor.apply()
    }

    fun getFloat(key: String): Float {
        return mPref!!.getFloat(key, 0F)
    }

    fun setFloat(float: Float, key: String) {
        val editor = mPref!!.edit()
        editor.putFloat(key, float)
        editor.apply()
    }
}