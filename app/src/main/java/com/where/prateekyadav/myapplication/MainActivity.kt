package com.where.prateekyadav.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.content.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.*
import android.view.inputmethod.EditorInfo
import com.where.prateekyadav.myapplication.Services.AddressUpdateService
import com.where.prateekyadav.myapplication.database.DataBaseController
import com.where.prateekyadav.myapplication.modal.SearchResult
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.where.prateekyadav.myapplication.Util.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    var mLocationHelper: LocationHelper? = null;
    var mListView: ListView? = null
    var mAdapter: LocationsAdapter? = null
    var mSearchResultsList = ArrayList<SearchResult>();
    lateinit var mDrawableClear: Drawable;
    lateinit var mDrawableSearch: Drawable;
    var mSearchEdittext: EditText? = null
    var mRelativeLayout: RelativeLayout? = null
    var mAlertForGPS: AlertDialog? = null
    var mSwipeToRefresh: SwipeRefreshLayout? = null
    var mToolbar: android.support.v7.widget.Toolbar? = null
    private var mSearchAction: MenuItem? = null
    private var isSearchOpened = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        // AppUtility().startTimerAlarm(this,true);
    }



    /**
     * Method to initialize the view and object
     */
    private fun initView() {
        mListView = findViewById(R.id.lv_address)
        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar);
        mListView = findViewById(R.id.lv_address) as ListView
        mSwipeToRefresh = findViewById(R.id.swiperefresh)
        mRelativeLayout = findViewById(R.id.rly_lyt_main)
        mAdapter = LocationsAdapter(this, mSearchResultsList)
        mListView!!.adapter = mAdapter
        mListView!!.emptyView = findViewById(R.id.tv_no_records) as TextView
        mLocationHelper = LocationHelper.getInstance(applicationContext);
        mDrawableClear = getClearDrawable(this);

        //
        //setSearchListener()
        //setOnTouchListener()
        setClickListener()
        startAddressUpdateServiceToUpdateAnyRemainingAddresss()
        swiperefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {

                if (!LocationHelper.getInstance(this@MainActivity).checkLocationAvailable()) {
                    openGPSALert()
                    swiperefresh.isRefreshing = false
                } else {
                    LocationHelper.getInstance(this@MainActivity).fetchLocation(true)
                    val handler = Handler().postDelayed({
                        swiperefresh.isRefreshing = false

                    }, 12 * 1000)
                }
            }

        })
    }

    /**
     * set click listener here
     */
    fun setClickListener() {

        mListView!!.onItemClickListener = object : AdapterView.OnItemClickListener {

            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {


            }
        }
    }

    /**
     *
     */
    fun setOnTouchListener() {
        mSearchEdittext!!.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                //
                //
                val editText = v as EditText
                if (event!!.getAction() != MotionEvent.ACTION_UP)
                    return false

                if (event!!.getX() > editText.width - mDrawableClear.intrinsicWidth) {
                    // action here
                    clearSearchTextAndSetMessage(v!!)
                    return true
                }

                return false
            }

        })
    }

    /**
     * set search listener
     */
    fun setSearchListener() {
        mSearchEdittext = findViewById<EditText>(R.id.edt_search)
        mSearchEdittext!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                if (edt_search.getText().toString().length > 0) {
                    edt_search.setCompoundDrawables(mDrawableSearch, null, mDrawableClear, null)
                } else {
                    edt_search.setCompoundDrawables(mDrawableSearch, null, null, null)

                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (s.length > 2)
                    search(s.toString())
                else if (s.length == 0) {
                    setLocationResults(DataBaseController(this@MainActivity).readRecentVisitedLocation(), true)
                }
            }
        })
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
        //
            PermissionCheckHandler.REQUEST_LOCATION_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                // If request is cancelled, the result arrays are empty.
                val hasSth = grantResults.size > 0
                if (hasSth) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //user accepted,
                        Log.d(AppConstant.TAG_KOTLIN_DEMO_APP, "Permission granted")

                        AppUtility().validateAutoStartTimer(this, true)

                        AppUtility().startTimerAlarm(applicationContext, true)
                        //startService(Intent(this, TimerService::class.java));

                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        val should = PermissionCheckHandler.shouldShowRequestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        if (should) {
                            PermissionCheckHandler.openDialogForPermissionAlert(this, getString(R.string.str_location_permission_alert_message))
                        } else {
                            //user has denied with `Never Ask Again`, go to settings
                            PermissionCheckHandler.promptSettings(this, getString(R.string.str_never_ask_title), getString(R.string.str_never_ask_message))
                        }
                    }
                }
                return
            }
        //
            PermissionCheckHandler.REQUEST_NETWORK_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {

                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }

    }

    @SuppressLint("ResourceType")
    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            /* var clear: EditText = autocompleteFragment!!.getView().findViewById(R.id.place_autocomplete_search_input)
             if (clear != null &&
                     clear.tex s t.isBlank()) {
             }*/
            setLocationResults(DataBaseController(this).readRecentVisitedLocation(), false)

            //mLocationHelper?.getLocation()
            /*if (!AppUtility().checkAlarmAlreadySet(this)) {
                AppUtility().startTimerAlarm(this);
            }*/
        } else {

        }

        MySharedPref.getinstance(this).setBoolean(true, AppConstant.SP_KEY_APP_REACHED_MAIN_SCREEN)

        //registerReceiver()
        //AppUtility().inssertDemoLocation(this)
        //LocationHelper.getInstance(this, this).setLocationListener();
        if (!ConnectionDetector.getInstance(this).isNetworkAvailable()) {
            AppUtility().showSnackBar(getString(R.string.no_net_avail), mRelativeLayout as View)
        }

        openGPSALert()
        AppUtility().validateAutoStartTimer(this, true)


    }

    fun openGPSALert() {
        try {
            if (!LocationHelper.getInstance(this).checkLocationAvailable()) {
                if (mAlertForGPS == null || !mAlertForGPS!!.isShowing) {
                    mAlertForGPS = AppUtility().buildAlertMessageNoGps(this)
                }

            } else if (mAlertForGPS != null && mAlertForGPS!!.isShowing) {
                mAlertForGPS!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        if (ContextCompat.checkSelfPermission(this,
                 Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //locationManager!!.removeUpdates()
        }
    }


    fun setLocationResults(address: List<SearchResult>, fromSearch: Boolean) {
        try {
            if (mSearchEdittext == null || mSearchEdittext!!.text.length == 0 || fromSearch) {
                mSearchResultsList.clear()
                mSearchResultsList.addAll(address as ArrayList<SearchResult>)
                // mSearchResultsList=address as ArrayList<SearchResult>
                // mAdapter = LocationsAdapter(this, mSearchResultsList)
                //mListView!!.adapter = mAdapter

                //mAdapter!!.notifyDataSetInvalidated()
                mAdapter!!.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            Log.d("Receiver", "Receive message")
            val message = intent.getStringExtra("message")
            val isUpdated = intent.getBooleanExtra(AppConstant.LOCATION_UPDATE_MESSAGE, false);
            if (isUpdated) {
                runOnUiThread({
                    if (swiperefresh != null) {
                        swiperefresh.isRefreshing = false
                    }
                    setLocationResults(
                            DataBaseController(this@MainActivity).readRecentVisitedLocation(), false)

                });

            }
        }// Receiver
    }

    override fun onStop() {
        unregisterReceiver()
        super.onStop()
    }

    private fun unregisterReceiver() {
        try {
            unregisterReceiver(mMessageReceiver);
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver()
        // Permission
        val permission = PermissionCheckHandler.checkLocationPermissions(this)
        if (!permission) {
            PermissionCheckHandler.verifyLocationPermissions(this)
        }
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity()
        } else {
            finish()
        }
    }

    private fun registerReceiver() {
        try {
            registerReceiver(
                    mMessageReceiver, IntentFilter(AppConstant.INTENT_FILTER_UPDATE_LOCATION))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }// registerReceiver


    fun search(place: String) {
        try {
            Thread {
                // var searchResultsList = DataBaseController(this).searchLocationOnline(place)
                var searchResultsList = DataBaseController(this).searchLocationOffline(place)

                if (searchResultsList != null) {
                    searchResultsList.forEach {
                        //Toast.makeText(this, it.visitedLocationInformation.address, Toast.LENGTH_SHORT).show()
                        Log.i(AppConstant.TAG_KOTLIN_DEMO_APP, "Search results: " + it.visitResults.visitedLocationInformation.vicinity)

                    }
                    runOnUiThread(Runnable {
                        setLocationResults(searchResultsList, true)
                    })
                } else {
                    //setLocation(DataBaseController(this).readAllVisitedLocation())

                    // Toast.makeText(this, "No result found", Toast.LENGTH_SHORT).show()
                }
            }.run()// Thread
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }// Search


    /**
     * Method to start service to synced the draft forms
     *
     * @param context
     */
    private fun startAddressUpdateServiceToUpdateAnyRemainingAddresss() {
        if (!AddressUpdateService.RUNNING) {
            val serviceIntent = Intent(this, AddressUpdateService::class.java)
            startService(serviceIntent)
        }
    }


    fun getClearDrawable(context: Context): Drawable {
        var mDrawableClear: Drawable? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDrawableClear = context.resources.getDrawable(R.drawable.btn_close_main, null)
            mDrawableSearch = context.resources.getDrawable(R.drawable.icn_search, null)

        } else {
            mDrawableClear = context.resources.getDrawable(R.drawable.btn_close_main)
            mDrawableSearch = context.resources.getDrawable(R.drawable.icn_search)
        }
        //
        mDrawableClear!!.setBounds(0, 0, mDrawableClear.intrinsicWidth, mDrawableClear.intrinsicHeight)
        mDrawableSearch!!.setBounds(0, 0, mDrawableSearch.intrinsicWidth, mDrawableSearch.intrinsicHeight)
        return mDrawableClear
    }

    /**
     *
     */
    private fun clearSearchTextAndSetMessage(view: View) {
        var mSearchText = ""
        val editText = view as EditText
        editText.setText("")
        editText.setCompoundDrawables(mDrawableSearch, null, null, null)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            1 -> {
                if (mSearchEdittext!!.text != null && !mSearchEdittext!!.text.isBlank()) {
                    search(mSearchEdittext!!.text.toString())
                }

            }
        }
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        mSearchAction = menu!!.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.getItemId()

        when (id) {
            R.id.action_search -> {
                handleMenuSearch()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun handleMenuSearch() {
        val action: ActionBar? = getSupportActionBar(); //get the actionbar

        if (isSearchOpened) { //test if the search is open

            action!!.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
            imm.hideSoftInputFromWindow(mSearchEdittext!!.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction!!.setIcon(getResources().getDrawable(R.drawable.btn_search));

            isSearchOpened = false;
            setLocationResults(DataBaseController(this@MainActivity).readRecentVisitedLocation(), true)
        } else { //open the search entry

            action!!.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action!!.setCustomView(R.layout.search_bar);//add the custom view
            action!!.setDisplayShowTitleEnabled(false); //hide the title

            mSearchEdittext = action!!.getCustomView().findViewById(R.id.edtSearch); //the text editor

            //this is a listener to do a search when the user clicks on search button
            mSearchEdittext!!.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(tv: TextView?, actionId: Int, p2: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        search(tv!!.text.toString())
                        return true;
                    }
                    return false;
                }

            });

            mSearchEdittext!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (s!!.length > 2)
                        search(s.toString())
                    else if (s!!.length == 0) {
                        setLocationResults(DataBaseController(this@MainActivity).readRecentVisitedLocation(), true)
                    }
                }

            })


            mSearchEdittext!!.requestFocus();

            //open the keyboard focused in the edtSearch
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(mSearchEdittext, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            mSearchAction!!.setIcon(mDrawableClear);

            isSearchOpened = true;
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
