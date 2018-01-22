package com.where.prateekyadav.myapplication

import com.where.prateekyadav.myapplication.database.VisitedLocationInformation
import com.where.prateekyadav.myapplication.modal.SearchResult
import com.where.prateekyadav.myapplication.modal.VisitResults


/**
 * Created by Infobeans on 1/11/2018.
 */
interface UpdateLocation {
    fun updateLocationAddressList(addressList: List<SearchResult>);
}