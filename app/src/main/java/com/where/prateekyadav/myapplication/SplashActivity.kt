package com.where.prateekyadav.myapplication

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import com.where.prateekyadav.myapplication.Util.PermissionCheckHandler
import com.where.prateekyadav.myapplication.database.DBContract
import com.where.prateekyadav.myapplication.database.DataBaseController
import android.util.Log
import com.where.prateekyadav.myapplication.Util.AppConstant


class SplashActivity : AppCompatActivity() {
    // Splash screen timer
    private val SPLASH_TIME_OUT: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        validatePermissionAndDataIntoTable()
    }

    private fun validatePermissionAndDataIntoTable() {
        val hasData = DataBaseController(this)
                .isTableHasData(DBContract.VisitedLocationData.TABLE_NAME_VISITED_LOCATION)
        val isLocationPermissionGranted = PermissionCheckHandler.checkLocationPermissions(this)
        //
        if (hasData || isLocationPermissionGranted){
            startSplashScreenTimer()
        }else{
            PermissionCheckHandler.verifyLocationPermissions(this)
        }

    }

    /**
     *
     */
    private fun startSplashScreenTimer() {
        Handler().postDelayed(Runnable /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

        {
            // This method will be executed once the timer is over
            // Start your app main activity
            startMainActivity()

            // close this activity
            finish()
        }, SPLASH_TIME_OUT)
    }

    private fun startMainActivity() {
        val i = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(i)
    }

    /**
     * Method to check permission is granted or not
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
        //
            PermissionCheckHandler.REQUEST_LOCATION_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                // If request is cancelled, the result arrays are empty.
                val hasSth = grantResults.size > 0
                if (hasSth) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //user accepted
                        Log.d(AppConstant.TAG_KOTLIN_DEMO_APP,"Permission granted")
                        startMainActivity()
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        val should = PermissionCheckHandler.shouldShowRequestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        if (should) {
                            PermissionCheckHandler.openDialogForPermissionAlert(this,getString(R.string.str_location_permission_alert_message))
                        } else {
                            //user has denied with `Never Ask Again`, go to settings
                            PermissionCheckHandler.promptSettings(this,getString(R.string.str_never_ask_title),getString(R.string.str_never_ask_message))
                        }
                    }
                }
                return
            }
        //
            PermissionCheckHandler.REQUEST_EXTERNAL_STORAGE_LOCATION -> {

                return
            }
        //
        }
    }






}
