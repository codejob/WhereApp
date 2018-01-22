package com.where.prateekyadav.myapplication.database

/**
 * Created by Infobeans on 1/10/2018.
 */
data class VisitedLocationInformation(val userId: Int,
                                      val latitude: Double,
                                      val longitude: Double,
                                      var address: String,
                                      val city: String,
                                      val state: String,
                                      val country: String,
                                      val postalCode: String,
                                      val knownName: String,
                                      val toTime: Long,
                                      val fromTime: Long,
                                      val locationProvider: String,
                                      val locationRequestType: String,
                                      val rowID: Int,
                                      var vicinity: String,
                                      val placeId: String,
                                      val photoUrl: String,
                                      val nearByPlacesIds: String,
                                      val isAddressSet: Int)