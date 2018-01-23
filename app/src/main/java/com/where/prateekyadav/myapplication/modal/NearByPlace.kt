package com.where.prateekyadav.myapplication.modal

import java.io.Serializable


/**
 * Created by Infobeans on 1/19/2018.
 */
class NearByPlace(var rowID: Int,
                  var latitude: Double,
                  var longitude: Double,
                  var address: String,
                  var knownName: String,
                  var dateTime: Long,
                  var locationRequestType: String,
                  var vicinity: String,
                  var placeId: String,
                  var photoUrl: String):Serializable {
}