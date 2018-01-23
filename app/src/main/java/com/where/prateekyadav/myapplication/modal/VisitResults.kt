package com.where.prateekyadav.myapplication.modal

import com.where.prateekyadav.myapplication.database.VisitedLocationInformation


/**
 * Created by Infobeans on 1/21/2018.
 */
class VisitResults(var visitedLocationInformation: VisitedLocationInformation) {

    var noOfVisits: Int = 0
    var showFromNearBy: Boolean = false;
    var nearByPlaceIDToShow: NearByPlace? = null;
}