package com.where.prateekyadav.myapplication.modal

import com.where.prateekyadav.myapplication.database.VisitedLocationInformation
import java.io.Serializable


/**
 * Created by Infobeans on 1/19/2018.
 */
class SearchResult(var visitResults: VisitResults,
                   var listNearByPlace: List<NearByPlace>):Serializable {


}