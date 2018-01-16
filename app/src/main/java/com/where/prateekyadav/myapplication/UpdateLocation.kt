package com.where.prateekyadav.myapplication

import com.where.prateekyadav.myapplication.database.VisitedLocationInformation


/**
 * Created by Infobeans on 1/11/2018.
 */
interface UpdateLocation {
    fun updateLocationAddress(address: String);
    fun updateLocationAddressList(addressList: List<VisitedLocationInformation>);
}