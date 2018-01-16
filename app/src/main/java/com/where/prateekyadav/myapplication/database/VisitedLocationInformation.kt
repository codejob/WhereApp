package com.where.prateekyadav.myapplication.database

/**
 * Created by Infobeans on 1/10/2018.
 */
    data class VisitedLocationInformation(val userid: Int, val latitude: Double, val longitude: Double,
                                     val address: String, val city: String, val state: String,
                                     val country: String, val postalCode: String, val knownName: String,
                                     val stayTime:Int, val dateTime: Long, val locationProvider: String)