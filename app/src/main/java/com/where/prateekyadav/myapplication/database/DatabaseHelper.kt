package com.where.prateekyadav.myapplication.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.where.prateekyadav.myapplication.Util.AppUtility
import com.where.prateekyadav.myapplication.Util.Constant
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Infobeans on 1/10/2018.
 */
class DatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    var mContext: Context? = null;
    private val mOpenCounter = AtomicInteger()
    internal lateinit var sqLiteDatabase: SQLiteDatabase

    init {
        mContext = context;
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
        db.execSQL(SQL_CREATE_VISIT_LOCATION_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        db.execSQL(SQL_DELETE_VISITED_LOCATION)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    /**
     * Method to get writable data here we create object of SQLite DB
     *
     * @return SQLiteDatabase
     */
    protected fun getWritableDB(): SQLiteDatabase {
        if (mOpenCounter.incrementAndGet() == 1) {
            sqLiteDatabase = this.writableDatabase
            Log.d("dbcounter", "open db")
        }
        return sqLiteDatabase
    }

    /**
     * Method to close data base id DB is open
     *
     * @param sqLiteDatabase
     */
    protected fun closeDataBase(sqLiteDatabase: SQLiteDatabase?) {
        if (mOpenCounter.decrementAndGet() == 0 && sqLiteDatabase != null && sqLiteDatabase.isOpen) {
            // Closing database
            Log.d("dbcounter", "close db")
            sqLiteDatabase.close()
        }
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "Rohitashv.db"

        private val SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DBContract.UserEntry.TABLE_NAME_USER + " (" +
                        DBContract.UserEntry.COLUMN_USER_ID + " INTEGER," +
                        DBContract.UserEntry.COLUMN_USER_NAME + " TEXT," +
                        DBContract.UserEntry.COLUMN_PASSWORD + " TEXT," +
                        DBContract.UserEntry.COLUMN_EMAIL + " TEXT)"

        private val SQL_CREATE_VISIT_LOCATION_TABLE =
                "CREATE TABLE " +
                        DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION + " (" +
                        DBContract.UserEntry.COLUMN_USER_ID + " INTEGER," +
                        DBContract.VisitedLocationData.COLUMN_LATITUDE + " INTEGER," +
                        DBContract.VisitedLocationData.COLUMN_LONGITUDE + " INTEGER," +
                        DBContract.VisitedLocationData.COLUMN_ADDRESS + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_CITY + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_STATE + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_COUNTRY + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_POSTAL_CODE + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_KNOWN_NAME + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_STAY_TIME + " INTEGER," +
                        DBContract.VisitedLocationData.COLUMN_DATE_TIME + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_LOCATION_PROVIDER + " TEXT)"


        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.UserEntry.TABLE_NAME_USER
        private val SQL_DELETE_VISITED_LOCATION = "DROP TABLE IF EXISTS " + DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION
    }

    @Throws(SQLiteConstraintException::class)
    fun insertUser(user: UserModel): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.UserEntry.COLUMN_USER_ID, user.userid)
        values.put(DBContract.UserEntry.COLUMN_USER_NAME, user.name)
        values.put(DBContract.UserEntry.COLUMN_EMAIL, user.age)

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(DBContract.UserEntry.TABLE_NAME_USER, null, values)

        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteUser(userid: String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = DBContract.UserEntry.COLUMN_USER_ID + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(userid)
        // Issue SQL statement.
        db.delete(DBContract.UserEntry.TABLE_NAME_USER, selection, selectionArgs)

        return true
    }

    fun readUser(userid: String): ArrayList<UserModel> {
        val users = ArrayList<UserModel>()
        val db = getWritableDB()
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.UserEntry.TABLE_NAME_USER + " WHERE " + DBContract.UserEntry.COLUMN_USER_ID + "='" + userid + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var name: String
        var age: String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                name = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_USER_NAME))
                age = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_EMAIL))

                users.add(UserModel(userid, name, age))
                cursor.moveToNext()
            }
            cursor.close()
        }
        closeDataBase(sqLiteDatabase)
        return users
    }

    fun readAllUsers(): ArrayList<UserModel> {
        val users = ArrayList<UserModel>()
        val db = getWritableDB()
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.UserEntry.TABLE_NAME_USER, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var userid: String
        var name: String
        var age: String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                userid = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_USER_ID))
                name = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_USER_NAME))
                age = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_EMAIL))

                users.add(UserModel(userid, name, age))
                cursor.moveToNext()
            }
            cursor.close()
        }
        closeDataBase(sqLiteDatabase)
        return users
    }

    //
    @Throws(SQLiteConstraintException::class)
    fun insertVisitedLocation(infoLocation: VisitedLocationInformation): Boolean {
        // Gets the data repository in write mode
        val db = getWritableDB()
        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.UserEntry.COLUMN_USER_ID, infoLocation.userid)
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
        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION, null, values)
        if (newRowId > 0) {
            return true
        } else {
            return false
        }
        closeDataBase(sqLiteDatabase)

    }


    fun readAllVisitedLocation(): ArrayList<VisitedLocationInformation> {
        val visitedLocationInfoList = ArrayList<VisitedLocationInformation>()
        val db = getWritableDB()
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_VISIT_LOCATION_TABLE)
            return ArrayList()
        }

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
        //
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
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
                visitedLocationInfoList.add(VisitedLocationInformation(userId, latitude, longitude, address, city, state,
                        country, postalCode, knownName, stayTime, dateTime, locationProvider))
                cursor.moveToNext()
            }
           cursor.close()
        }
        closeDataBase(sqLiteDatabase)
        //
        return visitedLocationInfoList
    }

    fun readLastVisitedLocation(): VisitedLocationInformation? {
        var visitedLocationInfo: VisitedLocationInformation? = null;
        val db = getWritableDB()
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_VISIT_LOCATION_TABLE)
            return visitedLocationInfo
        }

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
        //
        if (cursor.count > 0 && cursor!!.moveToLast()) {
            while (cursor.isAfterLast == false) {
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
                visitedLocationInfo = VisitedLocationInformation(userId, latitude, longitude, address, city, state,
                        country, postalCode, knownName, stayTime, dateTime, locationProvider)
                cursor.moveToNext()
            }
            cursor.close()
        }
        closeDataBase(sqLiteDatabase)
        //
        return visitedLocationInfo
    }

    /**
     * Method to copy database into sd card
     */
    fun copyDataBaseToSDCard() {
        try {
            // if Storage permission granted
            if (AppUtility().checkStoragePermissions(mContext)) {
                val myInput = FileInputStream("/data/data/" + mContext!!.getPackageName() + "/databases/" + DATABASE_NAME)
                AppUtility().makeDirs(Constant.FOLDER_PATH)
                val file = File(Constant.FOLDER_PATH + "/" + DATABASE_NAME + ".db")
                if (!file.exists()) {
                    try {
                        file.createNewFile()
                    } catch (e: IOException) {
                        Log.i("FO", "File creation failed for " + file)
                    }

                }
                val myOutput = FileOutputStream(Constant.FOLDER_PATH + "/" + DATABASE_NAME + ".db")
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
}