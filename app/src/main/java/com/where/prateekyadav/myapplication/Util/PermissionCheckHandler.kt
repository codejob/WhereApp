package com.where.prateekyadav.myapplication.Util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import com.where.prateekyadav.myapplication.R

/**
 * Created by Infobeans on 13-Jun-16.
 */
object PermissionCheckHandler {

    private val REPEAT_TIME = 5 * 60 * 1000
    private val BUFFER_SIZE = 1024
    // All Permissions
    val REQUEST_ALL_PERMISSIONS = 1
    // Storage Permissions
    val REQUEST_EXTERNAL_STORAGE_LOCATION = 2
    // Contact Permissions
    val REQUEST_CONTACT_CALL_LOGS = 3
    // Contact Permissions
    val REQUEST_CONTACT = 4
    // Network Permission
    val REQUEST_NETWORK_PERMISSION = 5
    // Audio Permission
    val REQUEST_AUDIO_PERMISSION = 6
    // Camera Permission
    val REQUEST_CAMERA_PERMISSION = 7
    // Camera Permission
    val REQUEST_LOCATION_PERMISSION = 8

    //
    private val ALL_PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS, Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_SMS, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)

    private val PERMISSIONS_CALL_AND_CONTACT = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS)

    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private val PERMISSIONS_CALL_PROCESS = arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.PROCESS_OUTGOING_CALLS)

    private val PERMISSIONS_CONTACT_PROCESS = arrayOf(Manifest.permission.READ_CONTACTS)
    private val PERMISSIONS_NETWORK_STATE = arrayOf(Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE)
    private val PERMISSIONS_AUDIO_RECORD = arrayOf(Manifest.permission.RECORD_AUDIO)
    private val PERMISSIONS_CAMERA = arrayOf(Manifest.permission.CAMERA)
    private val PERMISSIONS_LOCATION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)


    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    fun verifyAllPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
                activity,
                ALL_PERMISSIONS_STORAGE,
                REQUEST_ALL_PERMISSIONS
        )

    }

    /**
     *
     *
     */
    fun verifyLocationPermissions(activity: Activity) {
        // Check if we have write permission
        val permission = checkLocationPermissions(activity)
        //
        if (!permission) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LOCATION,
                    REQUEST_LOCATION_PERMISSION)
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    fun verifyStoragePermissions(activity: Activity) {
        // Check if we have write permission
        val permission = checkStoragePermissions(activity)

        if (!permission) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE_LOCATION)
            }
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    fun verifyContactPermissions(activity: Activity) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.READ_PHONE_STATE)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_CALL_AND_CONTACT,
                    REQUEST_CONTACT_CALL_LOGS)
        }
    }
    //
    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    fun verifyContactReadPermissions(activity: Activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.READ_CONTACTS)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_CONTACT_PROCESS,
                    REQUEST_CONTACT)
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    fun verifyRecordPermissions(activity: Activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.RECORD_AUDIO)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_AUDIO_RECORD,
                    REQUEST_AUDIO_PERMISSION)
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    fun verifyCameraPermissions(activity: Activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.CAMERA)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_CAMERA,
                    REQUEST_CAMERA_PERMISSION)
        }
    }


    /**
     *
     */
    fun checkLocationPermissions(context: Context): Boolean {
        Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Checking permissions.")

        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val grant = PackageManager.PERMISSION_GRANTED;
        // Verify that all required contact permissions have been granted.
        if (permissionCheck != grant && permission != grant) {
            // Locations permissions have not been granted.
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "permissions NOT been granted. Requesting permissions.")
            return false
        } else {
            // Locations permissions have been granted. Show the contacts fragment.
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                    "permissions have already been granted")
            return true
        }
    }

    /**
     *
     */
    fun checkStoragePermissions(context: Context): Boolean {
        Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Checking permissions.")

        // Verify that all required Storage permissions have been granted.
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Storage permissions have not been granted.
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "permissions has NOT been granted. Requesting permissions.")
            return false
        } else {
            // Storage permissions have been granted. Show the contacts fragment.
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                    "storage permissions have already been granted.")
            return true
        }
    }

    /**
     *
     */
    fun checkAudioRecordPermissions(context: Context): Boolean {
        Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Checking permissions.")

        // Verify that all required Storage permissions have been granted.
        if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Storage permissions have not been granted.
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "permissions has NOT been granted. Requesting permissions.")
            return false
        } else {
            // Storage permissions have been granted. Show the contacts fragment.
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                    "storage permissions have already been granted.")
            return true
        }
    }

    /**
     *
     */
    fun checkCameraPermissions(context: Context): Boolean {
        Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Checking permissions.")

        // Verify that all required Storage permissions have been granted.
        if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Storage permissions have not been granted.
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "permissions has NOT been granted. Requesting permissions.")
            return false
        } else {
            // Storage permissions have been granted. Show the contacts fragment.
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                    "storage permissions have already been granted.")
            return true
        }
    }

    /**
     *
     */
    fun checkCallPermissions(context: Context): Boolean {
        Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Checking permissions.")

        // Verify that all required contact permissions have been granted.
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "permissions has NOT been granted. Requesting permissions.")
            return false
        } else {
            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                    "permissions have already been granted. Displaying contact details.")
            return true
        }
    }
    //
    /**
     *
     */
    fun contactReadWritePermissions(context: Context): Boolean {
        Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Checking permissions.")

        // Verify that all required contact permissions have been granted.
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "permissions has NOT been granted. Requesting permissions.")
            return false
        } else {
            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                    "permissions have already been granted. Displaying contact details.")
            return true
        }
    }

    /**
     *
     */
    /**
     *
     */
    fun checkAllPermissions(context: Context): Boolean {
        Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Checking permissions.")

        // Verify that all required  permissions have been granted.
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            //permissions have not been granted.
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "permissions has NOT been granted. Requesting permissions.")
            return false
        } else {
            //permissions have been granted.
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                    "permissions have already been granted. Displaying contact details.")
            return true
        }
    }


    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    fun verifyNetWorkPermissions(activity: Activity) {
        // Check if we have write permission
        val permission = checkNetWorkPermissions(activity)

        if (!permission) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_NETWORK_STATE,
                    REQUEST_NETWORK_PERMISSION
            )
        }
    }

    /**
     *
     */
    fun checkNetWorkPermissions(context: Context): Boolean {
        Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Checking network permissions.")
        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "network permissions has NOT been granted. Requesting permissions.")
            return false
        } else {
            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(AppConstant.TAG_KOTLIN_DEMO_APP,
                    "permissions have already been granted. Displaying  details.")
            return true
        }
    }


    fun shouldShowRequestPermission(activity: Activity, requestedPermission: String): Boolean {
        val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(activity,
                requestedPermission);
        return shouldShow
    }

    fun openDialogForPermissionAlert(mActivity: Activity, alertMessage: String) {
        //user denied without Never ask again, just show rationale explanation
        val builder = AlertDialog.Builder(mActivity)
        builder.setTitle(mActivity.getString(R.string.str_title_permission_denied))
        builder.setMessage(mActivity.getString(R.string.str_location_permission_alert_message))
        builder.setPositiveButton(mActivity.getString(R.string.str_i_am_sure), DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
        builder.setNegativeButton(mActivity.getString(R.string.str_re_try), DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
            verifyLocationPermissions(mActivity)
        })
        builder.show()
    }

    fun promptSettings(mActivity: Activity, mDeniedNeverAskTitle: String, mDeniedNeverAskMsg: String) {
        val builder = AlertDialog.Builder(mActivity)
        builder.setTitle(mDeniedNeverAskTitle)
        builder.setMessage(mDeniedNeverAskMsg)
        builder.setPositiveButton(mActivity.getString(R.string.str_go_to_settings), DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
            goToSettings(mActivity,mActivity.getString(R.string.str_permission_enable))
        })
        builder.setNegativeButton(mActivity.getString(R.string.str_cancel), null)
        builder.show()
    }

    fun goToSettings(mActivity: Context,message:String) {
        AppUtility.showToast(mActivity, message)
        val myAppSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + mActivity.getPackageName()))
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mActivity.startActivity(myAppSettings)
    }
}
