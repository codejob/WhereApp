package com.where.prateekyadav.myapplication.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Infobeans on 1/10/2018.
 */
open class DatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    open var mContext: Context? = null;
    private val mOpenCounter = AtomicInteger()
    internal lateinit var sqLiteDatabase: SQLiteDatabase

    init {
        mContext = context;
    }

    override fun onCreate(db: SQLiteDatabase) {
     createTables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        db.execSQL(SQL_DELETE_VISITED_LOCATION)
        db.execSQL(SQL_DELETE_NEARBY_PLACES)
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
           // Log.d("dbcounter", "open db")
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
            //Log.d("dbcounter", "close db")
            sqLiteDatabase.close()
        }
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "Rohitashv.db"

        private val SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DBContract.UserEntry.TABLE_NAME_USER + " (" +
                        DBContract.VisitedLocationData.COLUMN_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DBContract.UserEntry.COLUMN_USER_ID + " INTEGER," +
                        DBContract.UserEntry.COLUMN_USER_NAME + " TEXT," +
                        DBContract.UserEntry.COLUMN_PASSWORD + " TEXT," +
                        DBContract.UserEntry.COLUMN_EMAIL + " TEXT)"

        private val SQL_CREATE_VISIT_LOCATION_TABLE =
                "CREATE TABLE " +
                        DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION + " (" +
                        DBContract.VisitedLocationData.COLUMN_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DBContract.UserEntry.COLUMN_USER_ID + " INTEGER," +
                        DBContract.VisitedLocationData.COLUMN_LATITUDE + " INTEGER," +
                        DBContract.VisitedLocationData.COLUMN_LONGITUDE + " INTEGER," +
                        DBContract.VisitedLocationData.COLUMN_ADDRESS + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_CITY + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_STATE + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_COUNTRY + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_POSTAL_CODE + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_KNOWN_NAME + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_VICINITY + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_TO_TIME + " INTEGER," +
                        DBContract.VisitedLocationData.COLUMN_FROM_TIME + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_PLACE_ID + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_PHOTO_URL + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_NEARBY_PLACES_IDS + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_IS_ADDRESS_SET + " INTEGER," +
                        DBContract.VisitedLocationData.COLUMN_ACCURACY + " INTEGER," +
                        DBContract.VisitedLocationData.COLUMN_LOCATION_PROVIDER + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_LOCATION_REQUEST_TYPE + " TEXT)"

        private val SQL_CREATE_NEARBY_LOCATION_TABLE =
                "CREATE TABLE " +
                        DBContract.NearByLocationData.TABLE_NAME_NEARBY_LOCATION + " (" +
                        DBContract.VisitedLocationData.COLUMN_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DBContract.VisitedLocationData.COLUMN_LATITUDE + " INTEGER," +
                        DBContract.VisitedLocationData.COLUMN_LONGITUDE + " INTEGER," +
                        DBContract.VisitedLocationData.COLUMN_ADDRESS + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_KNOWN_NAME + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_VICINITY + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_PLACE_ID + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_PHOTO_URL + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_FROM_TIME + " TEXT," +
                        DBContract.VisitedLocationData.COLUMN_LOCATION_REQUEST_TYPE + " TEXT)"


        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.UserEntry.TABLE_NAME_USER
        private val SQL_DELETE_VISITED_LOCATION = "DROP TABLE IF EXISTS " + DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION
        private val SQL_DELETE_NEARBY_PLACES = "DROP TABLE IF EXISTS " + DBContract.NearByLocationData.TABLE_NAME_NEARBY_LOCATION
    }

    protected fun createTables(db: SQLiteDatabase){
        db.execSQL(SQL_CREATE_ENTRIES)
        db.execSQL(SQL_CREATE_VISIT_LOCATION_TABLE)
        db.execSQL(SQL_CREATE_NEARBY_LOCATION_TABLE)
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



}