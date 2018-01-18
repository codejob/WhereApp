package com.where.prateekyadav.myapplication.database

import android.provider.BaseColumns

/**
 * Created by Infobeans on 1/10/2018.
 */
object DBContract {

    val CREATE_TABLE = "CREATE TABLE "
    val SELECT = "SELECT "
    val SELECT_FROM = "SELECT * FROM "
    val SELECT_COUNT_FROM = "SELECT COUNT(*) FROM "
    val WHERE = " WHERE "
    val OR = " OR "
    val AND = " AND "
    val NULL = " is null "
    val NOT_NULL = " is not null "
    val EQUALS_TO = " = "
    val EQUALS_TO_STRING = " = '"
    val NOT_EQUALS_TO = " != "
    val NOT_EQUALS_TO_STRING = " != '"
    val ORDER_BY = " ORDER BY "
    val DESC = " DESC"
    val ASC = " ASC"
    val ON = " ON "
    val LEFT_JOIN = " LEFT JOIN "
    val INNER_JOIN = " INNER JOIN "
    val UPDATE_CONDITION_CHECK = " =? "
    val DELETE_FROM = "DELETE FROM "
    val UPDATE = "UPDATE "
    val SET = " SET "
    val IN = "  IN ( "
    val ALTER_TABLE = " ALTER TABLE "
    val ADD_COLUMN = " ADD COLUMN "
    val DEFAULT = " DEFAULT "
    val VACUUM = " VACUUM"
    val NOCASE = " COLLATE NOCASE "
    val CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS "


    /* Inner class that defines the UserEntry table contents */
    class UserEntry : BaseColumns {
        companion object {
            val TABLE_NAME_USER = "users"
            val COLUMN_USER_ID = "user_id"
            val COLUMN_USER_NAME = "user_name"
            val COLUMN_EMAIL = "email"
            val COLUMN_PASSWORD = "password"

        }


    }

    /* Inner class that defines the VisitedLocationData table contents */
    class VisitedLocationData : BaseColumns {
        companion object {
            val TABLE_NAME_VISITED_LOCATION = "visitedLocation"
            val COLUMN_ROW_ID = "id"
            val COLUMN_USER_ID = "user_id"
            val COLUMN_LATITUDE = "latitude"
            val COLUMN_LONGITUDE = "longitude"
            val COLUMN_ADDRESS = "address"
            val COLUMN_CITY = "city"
            val COLUMN_STATE = "state"
            val COLUMN_COUNTRY = "country"
            val COLUMN_POSTAL_CODE = "postal_code"
            val COLUMN_KNOWN_NAME = "known_name"
            val COLUMN_VICINITY = "vicinity"
            val COLUMN_STAY_TIME = "stay_time"
            val COLUMN_DATE_TIME = "date_time"
            val COLUMN_LOCATION_PROVIDER = "location_provider"
            val COLUMN_LOCATION_REQUEST_TYPE = "location_request_type"
            val COLUMN_PLACE_ID = "place_id"
            val COLUMN_PHOTO_URL = "photo_url"
            val COLUMN_NEARBY_PLACES_IDS = "near_by_places_ids"
            val COLUMN_IS_ADDRESS_SET = "is_address_set"
        }
    }

    /* Inner class that defines the VisitedLocationData table contents */
    class NearByLocationData : BaseColumns {
        companion object {
            val TABLE_NAME_NEARBY_LOCATION = "nearByLocationData"


        }
    }


}