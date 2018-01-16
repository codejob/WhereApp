package com.where.prateekyadav.myapplication.database

import android.provider.BaseColumns

/**
 * Created by Infobeans on 1/10/2018.
 */
object DBContract {

    /* Inner class that defines the table contents */
    class UserEntry : BaseColumns {
        companion object {
            val TABLE_NAME_USER = "users"
            val COLUMN_USER_ID = "userId"
            val COLUMN_USER_NAME = "userName"
            val COLUMN_EMAIL= "email"
            val COLUMN_PASSWORD="password"

        }


    }

    /* Inner class that defines the table contents */
    class VisitedLocationData : BaseColumns {
        companion object {
            val TABLE_NAME_VISITED_LOCATION = "visitedLocation"
            val COLUMN_USER_ID = "userId"
            val COLUMN_LATITUDE = "latitude"
            val COLUMN_LONGITUDE = "longitude"
            val COLUMN_ADDRESS = "address"
            val COLUMN_CITY = "city"
            val COLUMN_STATE= "state"
            val COLUMN_COUNTRY="country"
            val COLUMN_POSTAL_CODE="postalCode"
            val COLUMN_KNOWN_NAME="knownName"
            val COLUMN_STAY_TIME="stayTime"
            val COLUMN_DATE_TIME="dateTime"
            val COLUMN_LOCATION_PROVIDER="locationProvider"
        }


    }

}