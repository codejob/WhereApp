package com.where.prateekyadav.myapplication.database

import java.io.Serializable

/**
 * Created by Infobeans on 1/10/2018.
 */
data class VisitedLocationInformation(var userUniqueNumber:String):Serializable{
    var userId: Int=0;
    var latitude: Double=0.0
    var longitude: Double=0.0
    var address: String=""
    var city: String=""
    var state: String=""
    var country: String=""
    var postalCode: String=""
    var knownName: String=""
    var toTime: Long=0L
    var fromTime: Long=0L
    var locationProvider: String=""
    var locationRequestType: String=""
    var rowID: Long=0L
    var vicinity: String=""
    var placeId: String=""
    var photoUrl: String=""
    var nearByPlacesIds: String=""
    var isAddressSet: Int=0
    var accuracy: Float= 1000.0F
}