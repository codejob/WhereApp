package com.where.prateekyadav.myapplication.search.network;

import android.location.Location;

import com.where.prateekyadav.myapplication.search.model.placesdetails.Result;

import java.util.List;


/**
 * Created by Prateek on 11/19/2016.
 */

public interface RetroCallIneractor {
     void updatePlaces(List<Result> places, Location location,String locationType);
     void updatePlaceDetails(Result place);
     void onFailure();
     void updatePlacesWithId(List<Result> places, Location location,String locationType,long rowId);
}
