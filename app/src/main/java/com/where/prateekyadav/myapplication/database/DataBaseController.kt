package com.where.prateekyadav.myapplication.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.util.Log
import com.where.prateekyadav.myapplication.Util.AppUtility
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.where.prateekyadav.myapplication.database.DBContract.EQUALS_TO
import com.where.prateekyadav.myapplication.database.DBContract.SELECT_FROM
import com.where.prateekyadav.myapplication.database.DBContract.WHERE
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

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
    fun insertVisitedLocation(infoLocation: VisitedLocationInformation): Boolean {
        // Gets the data repository in write mode
        val db = getWritableDB()
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
        values.put(DBContract.VisitedLocationData.COLUMN_STAY_TIME, infoLocation.stayTime)
        values.put(DBContract.VisitedLocationData.COLUMN_DATE_TIME, infoLocation.dateTime)
        values.put(DBContract.VisitedLocationData.COLUMN_LOCATION_PROVIDER, infoLocation.locationProvider)
        values.put(DBContract.VisitedLocationData.COLUMN_LOCATION_REQUEST_TYPE, infoLocation.locationRequestType)
        values.put(DBContract.VisitedLocationData.COLUMN_VICINITY, infoLocation.vicinity)
        values.put(DBContract.VisitedLocationData.COLUMN_PLACE_ID, infoLocation.placeId)
        values.put(DBContract.VisitedLocationData.COLUMN_NEARBY_PLACES_IDS, infoLocation.photoUrl)
        values.put(DBContract.VisitedLocationData.COLUMN_PHOTO_URL, infoLocation.locationRequestType)
        values.put(DBContract.VisitedLocationData.COLUMN_IS_ADDRESS_SET, infoLocation.isAddressSet)


        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION, null, values)

        closeDataBase(sqLiteDatabase)
        return newRowId > 0
    }

    /**
     * Method to update stay time into data base for the visited location
     */
    fun updateStayTime(rowID: Int, stayTime: Int) {
        try {// Gets the data repository in write mode
            val db = getWritableDB()
            var time=0;
            val getStayTimeQuery="SELECT stay_time FROM visitedLocation where  id ="+rowID;
            var cursor: Cursor? = null
            try {
                cursor = db.rawQuery(getStayTimeQuery, null)
            } catch (e: SQLiteException) {

            }
            //
            if (cursor!!.moveToFirst()) {
                while (cursor.isAfterLast == false) {
                    time = cursor.getInt(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_STAY_TIME))
                    cursor.moveToNext()
                }
                cursor.close()
            }
            // Create a new map of values, where column names are the keys
            val values = ContentValues()
            //
            time=time+stayTime;
            values.put(DBContract.VisitedLocationData.COLUMN_STAY_TIME, time)
            val whereClause = "id = ?"
            val whereArgs = arrayOf(rowID.toString())

            db.update(DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION
                    , values, whereClause, whereArgs)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun readAllVisitedLocation(): ArrayList<VisitedLocationInformation> {
        val visitedLocationInfoList = ArrayList<VisitedLocationInformation>()
        val db = getWritableDB()
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION, null)
        } catch (e: SQLiteException) {
            createTables(db)
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

        val query= SELECT_FROM + DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION
                   WHERE+DBContract.VisitedLocationData.COLUMN_IS_ADDRESS_SET+ EQUALS_TO+0;



        val visitedLocationInfoList = ArrayList<VisitedLocationInformation>()
        val db = getWritableDB()
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: SQLiteException) {
            createTables(db)
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

    private fun prepareVisitedLocationObject(cursor: Cursor): VisitedLocationInformation? {
        var visitedLocationInfo: VisitedLocationInformation? = null;
        var rowID: Int
        var userId: Int
        var latitude: Double
        var longitude: Double
        var address: String
        var city: String
        var state: String
        var country: String
        var postalCode: String
        var knownName: String
        var stayTime: Int
        var dateTime: Long
        var locationProvider: String
        var locationRequestType: String
        var vicinity: String
        var placeId: String
        var photoUrl: String
        var nearByPlacesIds: String
        var isAddressSet: Int

        rowID = cursor.getInt(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_ROW_ID))
        userId = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_USER_ID))
        latitude = cursor.getDouble(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_LATITUDE))
        longitude = cursor.getDouble(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_LONGITUDE))
        address = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_ADDRESS))
        city = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_CITY))
        state = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_STATE))
        country = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_COUNTRY))
        postalCode = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_POSTAL_CODE))
        knownName = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_KNOWN_NAME))
        stayTime = cursor.getInt(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_STAY_TIME))
        dateTime = cursor.getLong(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_DATE_TIME))
        locationProvider = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_LOCATION_PROVIDER))
        locationRequestType = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_LOCATION_REQUEST_TYPE))
        vicinity = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_VICINITY))
        placeId = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_PLACE_ID))
        photoUrl = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_PHOTO_URL))
        nearByPlacesIds = cursor.getString(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_NEARBY_PLACES_IDS))
        isAddressSet = cursor.getInt(cursor.getColumnIndex(DBContract.VisitedLocationData.COLUMN_IS_ADDRESS_SET))


        // init VisitedLocationInformation object
        visitedLocationInfo = VisitedLocationInformation(userId, latitude, longitude,
                address, city, state, country, postalCode, knownName, stayTime, dateTime,
                locationProvider, locationRequestType, rowID, vicinity, placeId,
                photoUrl, nearByPlacesIds, isAddressSet)
        return visitedLocationInfo;
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
                    values.put(DBContract.VisitedLocationData.COLUMN_DATE_TIME, it.dateTime)
                    values.put(DBContract.VisitedLocationData.COLUMN_LOCATION_REQUEST_TYPE, it.locationRequestType)
                    values.put(DBContract.VisitedLocationData.COLUMN_VICINITY, it.vicinity)
                    values.put(DBContract.VisitedLocationData.COLUMN_PLACE_ID, it.placeId)
                    values.put(DBContract.VisitedLocationData.COLUMN_PHOTO_URL, it.locationRequestType)
                    val isExist=checkIsDataAlreadyInDBorNot(DBContract.NearByLocationData.TABLE_NAME_NEARBY_LOCATION,
                            DBContract.VisitedLocationData.COLUMN_PLACE_ID,it.placeId);
                    if (isExist) {
                        // Insert the new row, returning the primary key value of the new row
                        val whereClause = DBContract.VisitedLocationData.COLUMN_PLACE_ID + "= ?"
                        val whereArgs = arrayOf(it.placeId)
                        //
                        db.update(DBContract.NearByLocationData.TABLE_NAME_NEARBY_LOCATION
                                , values, whereClause, whereArgs)
                    }else{
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
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }



}