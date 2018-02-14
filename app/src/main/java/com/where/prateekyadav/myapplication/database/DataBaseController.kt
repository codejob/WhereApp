package com.where.prateekyadav.myapplication.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Log
import com.google.android.gms.location.places.Place
import com.where.prateekyadav.myapplication.Util.AppUtility
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.where.prateekyadav.myapplication.database.DBContract.EQUALS_TO
import com.where.prateekyadav.myapplication.database.DBContract.OR
import com.where.prateekyadav.myapplication.database.DBContract.SELECT_FROM
import com.where.prateekyadav.myapplication.database.DBContract.WHERE
import com.where.prateekyadav.myapplication.modal.NearByPlace
import com.where.prateekyadav.myapplication.modal.SearchResult
import com.where.prateekyadav.myapplication.modal.VisitResults
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import android.database.sqlite.SQLiteStatement
import com.where.prateekyadav.myapplication.database.DBContract.AND
import com.where.prateekyadav.myapplication.database.DBContract.EQUALS_TO_STRING
import com.where.prateekyadav.myapplication.database.DBContract.SELECT_COUNT_FROM


/**
 * Created by Infobeans on 1/18/2018.
 */
class DataBaseController(context: Context?) : DatabaseHelper(context) {

    override var mContext: Context? = null;

    init {
        mContext = context;
    }

    //
    @Throws(SQLiteConstraintException::class)
    fun insertVisitedLocation(infoLocation: VisitedLocationInformation): Long {
        // Gets the data repository in write mode
        val db = getWritableDB()
        var values = getContentValuesForVisitedLocation(infoLocation)
        /// Removing this as it may reset the
        val rowId = infoLocation.rowID
        var newRowId: Long = 0
        if (rowId == 0L) {
            // Insert the new row, returning the primary key value of the new row
            newRowId = db.insert(DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION, null, values)
            var visitedLocationInformation = readLastVisitedLocation();
            newRowId = visitedLocationInformation!!.rowID;
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "inserting location data")

        } else {
            /// updating  only location data lat lng extra no address///
            updateVisitedLocationOnlyData(infoLocation, rowId)
            newRowId = rowId;
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "updating location data")


        }
        closeDataBase(sqLiteDatabase)
        return newRowId
    }

    @Throws(SQLiteConstraintException::class)
    fun updateVisitedLocation(infoLocation: VisitedLocationInformation, rowID: Long): Int {
        // Gets the data repository in write mode
        val db = getWritableDB()
        var values = getContentValuesForVisitedLocation(infoLocation)
        values.remove(DBContract.VisitedLocationData.COLUMN_TO_TIME)
        values.remove(DBContract.VisitedLocationData.COLUMN_FROM_TIME)
        values.remove(DBContract.VisitedLocationData.COLUMN_LOCATION_REQUEST_TYPE)

        // Update the new row, returning the primary key value of the new row
        val whereClause = "id = ?"
        val whereArgs = arrayOf(rowID.toString())

        val newRowId = db.update(DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION
                , values, whereClause, whereArgs)
        closeDataBase(sqLiteDatabase)
        return newRowId
    }

    fun updateVisitedLocationOnlyData(infoLocation: VisitedLocationInformation, rowID: Long): Int {
        // Gets the data repository in write mode
        val db = getWritableDB()
        var values = getContentValuesForUpdateVisitedLocationOnly(infoLocation)

        // Update the new row, returning the primary key value of the new row
        val whereClause = "id = ?"
        val whereArgs = arrayOf(rowID.toString())

        val newRowId = db.update(DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION
                , values, whereClause, whereArgs)
        closeDataBase(sqLiteDatabase)
        return newRowId
    }

    private fun getContentValuesForVisitedLocation(infoLocation: VisitedLocationInformation): ContentValues {
        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.UserEntry.COLUMN_USER_ID, infoLocation.userId)
        values.put(DBContract.VisitedLocationData.COLUMN_LATITUDE, infoLocation.latitude)
        values.put(DBContract.VisitedLocationData.COLUMN_LONGITUDE, infoLocation.longitude)
        values.put(DBContract.VisitedLocationData.COLUMN_ADDRESS, infoLocation.address)
        values.put(DBContract.VisitedLocationData.COLUMN_CITY, infoLocation.city)
        values.put(DBContract.VisitedLocationData.COLUMN_STATE, infoLocation.state)
        values.put(DBContract.VisitedLocationData.COLUMN_COUNTRY, infoLocation.country)
        values.put(DBContract.VisitedLocationData.COLUMN_POSTAL_CODE, infoLocation.postalCode)
        values.put(DBContract.VisitedLocationData.COLUMN_KNOWN_NAME, infoLocation.knownName)
        values.put(DBContract.VisitedLocationData.COLUMN_TO_TIME, infoLocation.toTime)
        values.put(DBContract.VisitedLocationData.COLUMN_FROM_TIME, infoLocation.fromTime)
        values.put(DBContract.VisitedLocationData.COLUMN_LOCATION_PROVIDER, infoLocation.locationProvider)
        values.put(DBContract.VisitedLocationData.COLUMN_LOCATION_REQUEST_TYPE, infoLocation.locationRequestType)
        values.put(DBContract.VisitedLocationData.COLUMN_VICINITY, infoLocation.vicinity)
        values.put(DBContract.VisitedLocationData.COLUMN_PLACE_ID, infoLocation.placeId)
        values.put(DBContract.VisitedLocationData.COLUMN_NEARBY_PLACES_IDS, infoLocation.nearByPlacesIds)
        values.put(DBContract.VisitedLocationData.COLUMN_PHOTO_URL, infoLocation.locationRequestType)
        values.put(DBContract.VisitedLocationData.COLUMN_IS_ADDRESS_SET, infoLocation.isAddressSet)
        values.put(DBContract.VisitedLocationData.COLUMN_ACCURACY, infoLocation.accuracy)
        values.put(DBContract.VisitedLocationData.COLUMN_ISPREFERRED, infoLocation.isPreferred)

        return values
    }

    private fun getContentValuesForUpdateVisitedLocationOnly(infoLocation: VisitedLocationInformation): ContentValues {
        // Create a new map of values, where column names are the keys

        val values = ContentValues()
        values.put(DBContract.UserEntry.COLUMN_USER_ID, infoLocation.userId)
        values.put(DBContract.VisitedLocationData.COLUMN_LATITUDE, infoLocation.latitude)
        values.put(DBContract.VisitedLocationData.COLUMN_LONGITUDE, infoLocation.longitude)
        values.put(DBContract.VisitedLocationData.COLUMN_LOCATION_PROVIDER, infoLocation.locationProvider)
        values.put(DBContract.VisitedLocationData.COLUMN_LOCATION_REQUEST_TYPE, infoLocation.locationRequestType)
        values.put(DBContract.VisitedLocationData.COLUMN_ACCURACY, infoLocation.accuracy)
        values.put(DBContract.VisitedLocationData.COLUMN_ISPREFERRED, infoLocation.isPreferred)

        return values
    }

    /**
     * Method to update stay time into data base for the visited location
     */
    fun updateStayTime(rowID: Int, stayTime: Int) {
        try {// Gets the data repository in write mode
            val db = getWritableDB()
            var time = 0;
            val getStayTimeQuery = "SELECT stay_time FROM visitedLocation where  id =" + rowID;
            var cursor: Cursor? = null
            try {
                cursor = db.rawQuery(getStayTimeQuery, null)
            } catch (e: SQLiteException) {

            }
            //
            if (cursor!!.moveToFirst()) {
                while (cursor.isAfterLast == false) {
                    time = cursor.getInt(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_TO_TIME))
                    cursor.moveToNext()
                }
                cursor.close()
            }
            // Create a new map of values, where column names are the keys
            val values = ContentValues()
            //
            time = time + stayTime;
            values.put(DBContract.VisitedLocationData.COLUMN_TO_TIME, time)
            val whereClause = "id = ?"
            val whereArgs = arrayOf(rowID.toString())

            db.update(DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION
                    , values, whereClause, whereArgs)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Method to update stay time into data base for the visited location
     */
    fun updateToTime(rowID: Long) {
        try {// Gets the data repository in write mode
            val db = getWritableDB()

            // Create a new map of values, where column names are the keys
            val values = ContentValues()
            //
            values.put(DBContract.VisitedLocationData.COLUMN_TO_TIME, System.currentTimeMillis())
            val whereClause = "id = ?"
            val whereArgs = arrayOf(rowID.toString())

            db.update(DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION
                    , values, whereClause, whereArgs)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Read all visited location from data base
     */
    fun readAllVisitedLocation(): ArrayList<SearchResult> {
        val visitResultsList = ArrayList<VisitResults>()
        val db = getWritableDB()
        var cursor: Cursor? = null
        try {
            var query = SELECT_FROM + DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION +
                    WHERE + DBContract.VisitedLocationData.COLUMN_IS_ADDRESS_SET + EQUALS_TO + 1

            cursor = db.rawQuery(query, null)
        } catch (e: SQLiteException) {
            createTables(db)
            return ArrayList()
        }
        //
        if (cursor != null && cursor!!.moveToLast()) {
            while (!cursor.isBeforeFirst) {
                val visit = prepareVisitedLocationObject(cursor)
                // get visited location object here through prepareVisitedLocationObject function
                val QueryCount = "select count(*) from " + DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION +
                        WHERE + DBContract.VisitedLocationData.COLUMN_PLACE_ID +
                        EQUALS_TO_STRING + visit!!.placeId + "'" +
                        " Group BY " + DBContract.VisitedLocationData.COLUMN_PLACE_ID
                val s = sqLiteDatabase.compileStatement(QueryCount)
                val count = s.simpleQueryForLong()
                var visitResults = VisitResults(visit!!)
                visitResults.noOfVisits = count.toInt()
                visitResultsList.add(visitResults)

                cursor.moveToPrevious()
            }
            cursor.close()
        }
        closeDataBase(sqLiteDatabase)
        //
        return parseSearchResult(visitResultsList) as ArrayList<SearchResult>
    }


    /**
     * Read all visited location from data base
     */
    fun readRecentVisitedLocation(): ArrayList<SearchResult> {
        val visitResultsList = ArrayList<VisitResults>()
        val db = getWritableDB()
        var cursor: Cursor? = null
        try {
            var query = SELECT_FROM + DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION +
                    WHERE + DBContract.VisitedLocationData.COLUMN_IS_ADDRESS_SET + EQUALS_TO + 1
            cursor = db.rawQuery(query, null)
        } catch (e: SQLiteException) {
            createTables(db)
            return ArrayList()
        }
        //
        if (cursor != null && cursor!!.moveToLast()) {
            while (!cursor.isBeforeFirst) {
                val visit = prepareVisitedLocationObject(cursor)
                // get visited location object here through prepareVisitedLocationObject function
                val QueryCount = "select count(*) from " + DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION +
                        WHERE + DBContract.VisitedLocationData.COLUMN_PLACE_ID +
                        EQUALS_TO_STRING + visit!!.placeId + "'" +
                        " Group BY " + DBContract.VisitedLocationData.COLUMN_PLACE_ID
                val s = sqLiteDatabase.compileStatement(QueryCount)
                val count = s.simpleQueryForLong()
                var visitResults = VisitResults(visit!!)
                visitResults.noOfVisits = count.toInt()
                visitResultsList.add(visitResults)
                if (visitResultsList.size === AppConstant.RECENT_COUNT) {
                    break;
                }

                cursor.moveToPrevious()
            }
            cursor.close()
        }
        closeDataBase(sqLiteDatabase)
        //
        return parseSearchResult(visitResultsList) as ArrayList<SearchResult>
    }

    /**
     * Method to get list of visited location
     */
    fun readLastVisitedLocation(): VisitedLocationInformation? {
        var visitedLocationInfo: VisitedLocationInformation? = null;
        val db = getWritableDB()
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION, null)
        } catch (e: SQLiteException) {
            createTables(db)
            return visitedLocationInfo
        }
        //
        if (cursor.count > 0 && cursor!!.moveToLast()) {
            while (cursor.isAfterLast == false) {
                // get visited location object here through prepareVisitedLocationObject function
                return prepareVisitedLocationObject(cursor)
                cursor.moveToNext()
            }
            cursor.close()
        }
        closeDataBase(sqLiteDatabase)
        //
        return visitedLocationInfo
    }


    /**
     * Method to get list of visited location which address is not updated
     */
    fun getListOfNotUpdatedVisitedLocation(): ArrayList<VisitedLocationInformation>? {

        val query = SELECT_FROM + DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION +
                WHERE + DBContract.VisitedLocationData.COLUMN_IS_ADDRESS_SET + EQUALS_TO + 0;


        val visitedLocationInfoList = ArrayList<VisitedLocationInformation>()
        val db = getWritableDB()
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: SQLiteException) {
            return ArrayList()
        }
        //
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                // get visited location object here through prepareVisitedLocationObject function
                visitedLocationInfoList.add(prepareVisitedLocationObject(cursor)!!);
                cursor.moveToNext()
            }
            cursor.close()
        }
        closeDataBase(sqLiteDatabase)
        //
        return visitedLocationInfoList
    }

    /**
     * Prepare location object
     */
    private fun prepareVisitedLocationObject(cursor: Cursor): VisitedLocationInformation? {
        var visitedLocationInfo: VisitedLocationInformation? = null;
        var rowID: Long
        var userId: Int
        var latitude: Double
        var longitude: Double
        var address: String
        var city: String
        var state: String
        var country: String
        var postalCode: String=""
        var knownName: String
        var stayTime: Long
        var dateTime: Long
        var locationProvider: String
        var locationRequestType: String
        var vicinity: String
        var placeId: String
        var photoUrl: String
        var nearByPlacesIds: String
        var isAddressSet: Int
        var isPreferred: Int
        var accuracy: Float

        rowID = cursor.getLong(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_ROW_ID))
        userId = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_USER_ID))
        latitude = cursor.getDouble(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_LATITUDE))
        longitude = cursor.getDouble(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_LONGITUDE))
        address = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_ADDRESS))
        city = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_CITY))
        state = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_STATE))
        country = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_COUNTRY))
        try {
            postalCode = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_POSTAL_CODE))
        } catch (e: Exception) {
            //e.printStackTrace()
        }
        knownName = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_KNOWN_NAME))
        stayTime = cursor.getLong(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_TO_TIME))
        dateTime = cursor.getLong(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_FROM_TIME))
        locationProvider = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_LOCATION_PROVIDER))
        locationRequestType = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_LOCATION_REQUEST_TYPE))
        vicinity = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_VICINITY))
        placeId = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_PLACE_ID))
        photoUrl = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_PHOTO_URL))
        nearByPlacesIds = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_NEARBY_PLACES_IDS))
        isAddressSet = cursor.getInt(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_IS_ADDRESS_SET))
        isPreferred = cursor.getInt(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_ISPREFERRED))
        accuracy = cursor.getFloat(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_ACCURACY))

        // set values for visited location information
        var visitedLocationInformation = VisitedLocationInformation("NA")
        visitedLocationInformation.userId = 1
        visitedLocationInformation.latitude = latitude
        visitedLocationInformation.longitude = longitude
        visitedLocationInformation.address = address
        visitedLocationInformation.city = city
        visitedLocationInformation.state = state
        visitedLocationInformation.country = country
        if(postalCode!=null)
            visitedLocationInformation.postalCode = postalCode
        visitedLocationInformation.knownName = knownName
        visitedLocationInformation.toTime = stayTime
        visitedLocationInformation.fromTime = dateTime
        visitedLocationInformation.locationProvider = locationProvider
        visitedLocationInformation.rowID = rowID
        visitedLocationInformation.locationRequestType = locationRequestType
        visitedLocationInformation.vicinity = vicinity
        visitedLocationInformation.placeId = placeId
        if(photoUrl!=null)
            visitedLocationInformation.photoUrl = photoUrl
        visitedLocationInformation.nearByPlacesIds = nearByPlacesIds
        visitedLocationInformation.isAddressSet = isAddressSet
        visitedLocationInformation.isPreferred = isPreferred
        visitedLocationInformation.accuracy = accuracy
        // return visited location information object
        return visitedLocationInformation;
    }

    /**
     * Prepare near by location object here
     */
    private fun prepareNearByLocationObject(cursor: Cursor): NearByPlace? {
        var nearByPlace: NearByPlace? = null;

        var rowID: Int
        var latitude: Double
        var longitude: Double
        var address: String
        var knownName: String
        var dateTime: Long
        var locationRequestType: String
        var vicinity: String
        var placeId: String
        var photoUrl: String

        rowID = cursor.getInt(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_ROW_ID))
        latitude = cursor.getDouble(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_LATITUDE))
        longitude = cursor.getDouble(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_LONGITUDE))
        address = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_ADDRESS))
        knownName = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_KNOWN_NAME))
        dateTime = cursor.getLong(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_FROM_TIME))
        locationRequestType = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_LOCATION_REQUEST_TYPE))
        vicinity = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_VICINITY))
        placeId = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_PLACE_ID))
        photoUrl = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_PHOTO_URL))


        // init VisitedLocationInformation object
        nearByPlace = NearByPlace(rowID, latitude, longitude,
                address, knownName, dateTime,
                locationRequestType, vicinity, placeId,
                photoUrl)
        return nearByPlace;
    }

    /**
     * Method is used for update the near by places ids
     */
    fun updateNearByPlaces(placeId: String, nearByPlaces: String) {
        try {

            val db = getWritableDB()
            val whereClause = DBContract.VisitedLocationData.COLUMN_PLACE_ID + "= ?"
            val whereArgs = arrayOf(placeId)
            val values = ContentValues()
            values.put(DBContract.VisitedLocationData.COLUMN_NEARBY_PLACES_IDS, nearByPlaces)
            //
            db.update(DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION
                    , values, whereClause, whereArgs)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * Method to copy database into sd card
     */
    fun copyDataBaseToSDCard() {
        try {
            // if Storage permission granted
            if (AppUtility().checkStoragePermissions(mContext)) {
                val myInput = FileInputStream("/data/data/" + mContext!!.getPackageName() + "/databases/" + DatabaseHelper.DATABASE_NAME)
                AppUtility().makeDirs(AppConstant.FOLDER_PATH)
                val file = File(AppConstant.FOLDER_PATH + "/" + DatabaseHelper.DATABASE_NAME + ".db")
                if (!file.exists()) {
                    try {
                        file.createNewFile()
                    } catch (e: IOException) {
                        Log.i("FO", "File creation failed for " + file)
                    }

                }
                val myOutput = FileOutputStream(AppConstant.FOLDER_PATH + "/" + DatabaseHelper.DATABASE_NAME + ".db")
                val buffer = ByteArray(1024)
                var length = 0;
//                while (length>0) {
//
//                    myOutput.write(buffer, 0, length)
//                }

                do {
                    length = myInput.read(buffer)
                    if (length > 0) {
                        myOutput.write(buffer, 0, length)
                    }
                } while (length > 0)
                //Close the streams
                myOutput.flush()
                myOutput.close()
                myInput.close()
                Log.i("FO", "copied")
            }
        } catch (e: Exception) {
            Log.i("FO", "exception=" + e)
        }
    }

    fun prepareContentValuesForNeaby(it: NearByPlace): ContentValues {
        val values = ContentValues()
        try {
            values.put(DBContract.VisitedLocationData.COLUMN_LATITUDE, it.latitude)
            values.put(DBContract.VisitedLocationData.COLUMN_LONGITUDE, it.longitude)
            values.put(DBContract.VisitedLocationData.COLUMN_ADDRESS, it.address)
            values.put(DBContract.VisitedLocationData.COLUMN_KNOWN_NAME, it.knownName)
            values.put(DBContract.VisitedLocationData.COLUMN_LOCATION_REQUEST_TYPE, it.locationRequestType)
            values.put(DBContract.VisitedLocationData.COLUMN_VICINITY, it.vicinity)
            values.put(DBContract.VisitedLocationData.COLUMN_PLACE_ID, it.placeId)
            values.put(DBContract.VisitedLocationData.COLUMN_PHOTO_URL, it.locationRequestType)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return values
    }

    /**
     * Method to insert or update near by places into Near by places table
     */
    fun insertNearByPlaces(nearByPlacesList: List<VisitedLocationInformation>): Boolean {
        // Gets the data repository in write mode
        try {
            if (nearByPlacesList.size > 0) {
                nearByPlacesList.forEach {
                    val db = getWritableDB()
                    // Create a new map of values, where column names are the keys
                    val values = ContentValues()
                    //
                    values.put(DBContract.VisitedLocationData.COLUMN_LATITUDE, it.latitude)
                    values.put(DBContract.VisitedLocationData.COLUMN_LONGITUDE, it.longitude)
                    values.put(DBContract.VisitedLocationData.COLUMN_ADDRESS, it.address)
                    values.put(DBContract.VisitedLocationData.COLUMN_KNOWN_NAME, it.knownName)
                    values.put(DBContract.VisitedLocationData.COLUMN_FROM_TIME, it.fromTime)
                    values.put(DBContract.VisitedLocationData.COLUMN_LOCATION_REQUEST_TYPE, it.locationRequestType)
                    values.put(DBContract.VisitedLocationData.COLUMN_VICINITY, it.vicinity)
                    values.put(DBContract.VisitedLocationData.COLUMN_PLACE_ID, it.placeId)
                    values.put(DBContract.VisitedLocationData.COLUMN_PHOTO_URL, it.locationRequestType)

                    val isExist = checkIsDataAlreadyInDBorNot(DBContract.NearByLocationData.TABLE_NAME_NEARBY_LOCATION,
                            DBContract.VisitedLocationData.COLUMN_PLACE_ID, it.placeId);
                    if (isExist) {
                        // Insert the new row, returning the primary key value of the new row
                        val whereClause = DBContract.VisitedLocationData.COLUMN_PLACE_ID + "= ?"
                        val whereArgs = arrayOf(it.placeId)
                        //
                        db.update(DBContract.NearByLocationData.TABLE_NAME_NEARBY_LOCATION
                                , values, whereClause, whereArgs)
                    } else {
                        val newRowId = db.insert(DBContract.NearByLocationData.TABLE_NAME_NEARBY_LOCATION, null, values)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace();
            return false
        }
        //
        closeDataBase(sqLiteDatabase)
        return true;
    }

    /**
     * @param TableName
     * @param dbfield
     * @param fieldValue
     * @return Boolean
     */
    private fun checkIsDataAlreadyInDBorNot(TableName: String, dbfield: String,
                                            fieldValue: String): Boolean {
        val sqLiteDatabase = getWritableDB()

        val Query = SELECT_FROM + TableName + WHERE + dbfield + " =?"
        val cursor = sqLiteDatabase.rawQuery(Query, arrayOf(fieldValue))
        if (cursor != null && cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        closeDataBase(sqLiteDatabase)
        return true
    }

    /* public fun searchLocationOnline(place: Place): List<SearchResult>? {
         var searchResultList = ArrayList<SearchResult>()
         try {
             var visitedLocationInfoList = ArrayList<VisitedLocationInformation>()
             visitedLocationInfoList = getVisitedLocationsFromPlaceid(place.id) as ArrayList<VisitedLocationInformation>

             if (visitedLocationInfoList == null || visitedLocationInfoList.size == 0) {
                 visitedLocationInfoList = getVisitedLocationsFromNearByPlaceidArray(place.id) as ArrayList<VisitedLocationInformation>
                 if (visitedLocationInfoList != null && visitedLocationInfoList.size > 0) {
                     searchResultList = parseSearchResult(visitedLocationInfoList) as ArrayList<SearchResult>

                 } else {
                     ///// No match found from place ID
                 }

             } else {
                 /////////////// Exact match///////////////////
                 searchResultList = parseSearchResult(visitedLocationInfoList) as ArrayList<SearchResult>

             }

             return searchResultList
         } catch (e: Exception) {
             e.printStackTrace()
         }
         return searchResultList
     }*/
    /**
     * Prepare search result here
     */
    fun parseSearchResult(visitResultsList: List<VisitResults>): List<SearchResult>? {
        var searchResultList = ArrayList<SearchResult>()

        try {
            var searchResult: SearchResult?
            //// Get the nearby of all the visited location
            visitResultsList.forEach {
                var nearByPlaceList = ArrayList<NearByPlace>()
                val visitResults = it;
                val nearByIDS = visitResults.visitedLocationInformation.nearByPlacesIds
                try {
                    nearByIDS.split(",").forEach {
                        try {
                            val id = it
                            nearByPlaceList.add(getNearbyPlace(id)!!)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                searchResult = SearchResult(visitResults, nearByPlaceList)

                searchResultList.add(searchResult!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return searchResultList
    }

    fun getVisitedLocationsFromPlaceid(placeID: String): List<VisitedLocationInformation>? {
        val visitedLocationInfoList = ArrayList<VisitedLocationInformation>()

        val sqLiteDatabase = getWritableDB()

        try {
            val Query = SELECT_FROM + DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION +
                    WHERE + DBContract.VisitedLocationData.COLUMN_PLACE_ID + " =?"
            val cursor = sqLiteDatabase.rawQuery(Query, arrayOf(placeID))
            //
            if (cursor != null && cursor!!.moveToFirst()) {
                while (cursor.isAfterLast == false) {
                    // get visited location object here through prepareVisitedLocationObject function
                    visitedLocationInfoList.add(prepareVisitedLocationObject(cursor)!!);
                    cursor.moveToNext()
                }
                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        closeDataBase(sqLiteDatabase)
        return visitedLocationInfoList
    }

    private fun getVisitedLocationsFromNearByPlaceidArray(placeID: String): List<VisitedLocationInformation>? {
        val visitedLocationInfoList = ArrayList<VisitedLocationInformation>()

        val sqLiteDatabase = getWritableDB()

        try {
            val Query = SELECT_FROM + DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION +
                    WHERE + DBContract.VisitedLocationData.COLUMN_NEARBY_PLACES_IDS +
                    " LIKE '%" + placeID + "%'"
            val cursor = sqLiteDatabase.rawQuery(Query, null)
            //
            if (cursor != null && cursor!!.moveToFirst()) {
                while (cursor.isAfterLast == false) {
                    // get visited location object here through prepareVisitedLocationObject function
                    visitedLocationInfoList.add(prepareVisitedLocationObject(cursor)!!);
                    cursor.moveToNext()
                }
                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        closeDataBase(sqLiteDatabase)
        return visitedLocationInfoList
    }

    private fun getNearbyPlace(placeID: String?): NearByPlace? {
        var nearbyPlace: NearByPlace? = null

        val sqLiteDatabase = getWritableDB()

        try {
            val Query = SELECT_FROM + DBContract.NearByLocationData.TABLE_NAME_NEARBY_LOCATION +
                    WHERE + DBContract.VisitedLocationData.COLUMN_PLACE_ID + " =?"
            val cursor = sqLiteDatabase.rawQuery(Query, arrayOf(placeID))
            //
            if (cursor != null && cursor!!.moveToFirst()) {
                nearbyPlace = prepareNearByLocationObject(cursor)
                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        closeDataBase(sqLiteDatabase)
        return nearbyPlace
    }

    public fun searchLocationOffline(place: String): List<SearchResult>? {
        var searchResultList = ArrayList<SearchResult>()
        try {
            var visitResultsList = ArrayList<VisitResults>()
            visitResultsList = getVisitedLocationsFromTextMatch(place) as ArrayList<VisitResults>

            //if (visitResultsList == null || visitResultsList.size == 0) {
            //visitResultsList = getVisitedFromNearbyPlaceListMatchesText(place) as ArrayList<VisitResults>
            visitResultsList.addAll(getVisitedFromNearbyPlaceListMatchesText(place) as ArrayList<VisitResults>)
            if (visitResultsList != null && visitResultsList.size > 0) {
                searchResultList = parseSearchResult(visitResultsList) as ArrayList<SearchResult>

            } else {
                //searchResultList = readAllVisitedLocation()
                ///// No match found from place ID
            }

//            } else {
//                /////////////// Exact match///////////////////
//                searchResultList = parseSearchResult(visitResultsList) as ArrayList<SearchResult>
//
//            }

            return searchResultList
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return searchResultList
    }

    private fun getVisitedLocationsFromTextMatch(placeName: String): List<VisitResults>? {
        val visitResultsList = ArrayList<VisitResults>()

        val sqLiteDatabase = getWritableDB()

        try {
            val Query = SELECT_FROM + DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION +
                    WHERE + DBContract.VisitedLocationData.COLUMN_ADDRESS +
                    " LIKE '%" + placeName + "%' " +
                    OR + DBContract.VisitedLocationData.COLUMN_VICINITY +
                    " LIKE '%" + placeName + "%'" +
                    " Group BY " + DBContract.VisitedLocationData.COLUMN_PLACE_ID

            val cursor = sqLiteDatabase.rawQuery(Query, null)
            //
            if (cursor != null && cursor!!.moveToLast()) {
                while (cursor.isBeforeFirst == false) {
                    // get visited location object here through prepareVisitedLocationObject function
                    var visit = prepareVisitedLocationObject(cursor)!!
                    val QueryCount = "select count(*) from " + DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION +
                            WHERE + DBContract.VisitedLocationData.COLUMN_PLACE_ID +
                            EQUALS_TO_STRING + visit.placeId + "'" +
                            " Group BY " + DBContract.VisitedLocationData.COLUMN_PLACE_ID
                    val s = sqLiteDatabase.compileStatement(QueryCount)
                    val count = s.simpleQueryForLong()
                    var visitResults = VisitResults(visit)
                    visitResults.noOfVisits = count.toInt()
                    visitResults.searchString=placeName
                    visitResultsList.add(visitResults)
                    cursor.moveToPrevious()
                }
                cursor.close()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
        closeDataBase(sqLiteDatabase)
        return visitResultsList
    }

    private fun getVisitedFromNearbyPlaceListMatchesText(placeName: String?): List<VisitResults> {
        val visitResultsList = ArrayList<VisitResults>()
        val sqLiteDatabase = getWritableDB()

        try {
            val Query = SELECT_FROM + DBContract.NearByLocationData.TABLE_NAME_NEARBY_LOCATION +
                    WHERE + DBContract.VisitedLocationData.COLUMN_ADDRESS +
                    " LIKE '%" + placeName + "%' " +
                    OR + DBContract.VisitedLocationData.COLUMN_VICINITY +
                    " LIKE '%" + placeName + "%'"
            val cursor = sqLiteDatabase.rawQuery(Query, null)
            //

            if (cursor != null && cursor!!.moveToLast()) {
                var visitedID = ArrayList<String>()
                while (cursor.isBeforeFirst == false) {

                    val nearbyPlace = prepareNearByLocationObject(cursor)
                    val visited = getLastVisitedLocationsFromNearByPlaceidArray(nearbyPlace!!.placeId)
                    if (visitedID.contains(visited!!.placeId)) {
                        cursor.moveToPrevious()
                        continue
                    }
                    visitedID.add(visited!!.placeId)

                    val QueryCount = "select count(*) from " + DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION +
                            WHERE + DBContract.VisitedLocationData.COLUMN_PLACE_ID +
                            EQUALS_TO_STRING + visited.placeId + "'" +
                            " Group BY " + DBContract.VisitedLocationData.COLUMN_PLACE_ID
                    val s = sqLiteDatabase.compileStatement(QueryCount)
                    val count = s.simpleQueryForLong()
                    var visitResults = VisitResults(visited!!)
                    visitResults.noOfVisits = count.toInt()
                    visitResults.showFromNearBy = true
                    visitResults.nearByPlaceIDToShow = nearbyPlace
                    visitResults.searchString=placeName
                    visitResultsList.add(visitResults)

                    cursor.moveToPrevious()
                }
                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        closeDataBase(sqLiteDatabase)
        return visitResultsList
    }

    private fun getLastVisitedLocationsFromNearByPlaceidArray(placeID: String): VisitedLocationInformation? {
        var visitedLocationInfo: VisitedLocationInformation? = null

        val sqLiteDatabase = getWritableDB()

        try {
            val Query = SELECT_FROM + DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION +
                    WHERE + DBContract.VisitedLocationData.COLUMN_NEARBY_PLACES_IDS +
                    " LIKE '%" + placeID + "%'"
            val cursor = sqLiteDatabase.rawQuery(Query, null)
            //
            if (cursor != null && cursor!!.moveToLast()) {
                visitedLocationInfo = prepareVisitedLocationObject(cursor);
                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        closeDataBase(sqLiteDatabase)
        return visitedLocationInfo
    }

    fun deleteVisitedPlaceAndUniqueNearByForIt(visitedLocationInformationList: List<VisitedLocationInformation>) {
        val sqLiteDatabase = getWritableDB()
        visitedLocationInformationList.forEach {
            val visitedLocationInformation = it

            val nearByIDS = visitedLocationInformation.nearByPlacesIds
            ////////////Deletting the current  visited row////
            deleteVisitEntry(visitedLocationInformation)

            try {
                nearByIDS.split(",").forEach {
                    val id = it
                    val list = getVisitedLocationsFromNearByPlaceidArray(id)
                    if (list!!.size == 0) {

                        deleteNearByEntry(id)
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        closeDataBase(sqLiteDatabase)


    }

    fun deleteVisitEntry(visitedLocationInformation: VisitedLocationInformation) {
        val whereClause = "id = ?"
        val whereArgs = arrayOf(visitedLocationInformation.rowID.toString())

        val newRowId = sqLiteDatabase.delete(DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION
                , whereClause, whereArgs)
    }

    fun deleteNearByEntry(placeID: String) {
        val whereClause = DBContract.VisitedLocationData.COLUMN_PLACE_ID + " = ?"
        val whereArgs = arrayOf(placeID)

        val newRowId = sqLiteDatabase.delete(DBContract.NearByLocationData.TABLE_NAME_NEARBY_LOCATION
                , whereClause, whereArgs)

    }

    /**
     * Method is used for update the near by places ids
     */
    fun updateNearByPlace(placeId: String, values: ContentValues) {
        try {

            val db = getWritableDB()
            val whereClause = DBContract.VisitedLocationData.COLUMN_PLACE_ID + "= ?"
            val whereArgs = arrayOf(placeId)
            //
            db.update(DBContract.NearByLocationData.TABLE_NAME_NEARBY_LOCATION
                    , values, whereClause, whereArgs)
            closeDataBase(sqLiteDatabase)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateVisitedLocationWithNearBy(visit: VisitedLocationInformation, nearByPlace: NearByPlace) {
        val sqLiteDatabase = getWritableDB()
        // Saving vist details
        val address = visit.address
        val visinity = visit.vicinity
        val lat = visit.latitude
        val lng = visit.longitude
        val placeID = visit.placeId

        visit.address = nearByPlace.address
        visit.vicinity = nearByPlace.vicinity
        visit.latitude = nearByPlace.latitude
        visit.longitude = nearByPlace.longitude
        visit.placeId = nearByPlace.placeId
        visit.nearByPlacesIds = visit.nearByPlacesIds.replace(nearByPlace.placeId, placeID)
        visit.isPreferred = 1

        updateVisitedLocation(visit, visit.rowID)

        nearByPlace.address = address
        nearByPlace.vicinity = visinity
        nearByPlace.latitude = lat
        nearByPlace.longitude = lng
        val placeIDNearBy = nearByPlace.placeId
        nearByPlace.placeId = placeID
        updateNearByPlace(placeIDNearBy, prepareContentValuesForNeaby(nearByPlace))

        val search = readAllVisitedLocation()

        closeDataBase(sqLiteDatabase)
    }

    /**
     * Method to check table has data
     */
    fun isTableHasData(tableName: String): Boolean {
        var db = getWritableDB()
        val query = SELECT_COUNT_FROM + tableName
        var mcursor = db.rawQuery(query, null);
        mcursor.moveToFirst();
        val count = mcursor.getInt(0);
        //
        closeDataBase(sqLiteDatabase)
        return count > 0

    }

    /**
     * Method to set preferred location into database
     */
    fun isPreferredLocation(placeID: String): Boolean {

        val sqLiteDatabase = getWritableDB()

        try {
            val Query = SELECT_FROM + DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION +
                    WHERE + DBContract.VisitedLocationData.COLUMN_PLACE_ID + " =?" +
                    AND + DBContract.VisitedLocationData.COLUMN_ISPREFERRED + " =?"
            val cursor = sqLiteDatabase.rawQuery(Query, arrayOf(placeID, "1"))
            //
            if (cursor != null && cursor!!.count > 0) {
                cursor.close()
                closeDataBase(sqLiteDatabase)
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    /**
     * Method to update stay time into data base for the visited location
     */
    fun updateAddress(rowID: Int, address: String):Int {
        try {// Gets the data repository in write mode
            val db = getWritableDB()
            // Create a new map of values, where column names are the keys
            val values = ContentValues()

            values.put(DBContract.VisitedLocationData.COLUMN_ADDRESS, address)
            val whereClause = "id = ?"
            val whereArgs = arrayOf(rowID.toString())

            var updated= db.update(DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION
                    , values, whereClause, whereArgs)
            return updated;

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0;
    }

}
