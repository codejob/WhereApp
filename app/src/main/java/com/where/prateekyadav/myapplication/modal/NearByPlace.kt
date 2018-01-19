package com.where.prateekyadav.myapplication.modal


/**
 * Created by Infobeans on 1/19/2018.
 */
class NearByPlace(val rowID: Int,
                  val latitude: Double,
                  val longitude: Double,
                  val address: String,
                  val knownName: String,
                  val dateTime: Long,
                  val locationRequestType: String,
                  val vicinity: String,
                  val placeId: String,
                  val photoUrl: String) {
}