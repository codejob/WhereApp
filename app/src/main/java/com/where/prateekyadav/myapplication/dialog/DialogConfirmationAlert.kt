package com.where.prateekyadav.myapplication.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView

import com.where.prateekyadav.myapplication.R
import com.where.prateekyadav.myapplication.appinterface.ConfirmationListener


/**
 * Created by Rohitashv on 14-Nov-16.
 */
class DialogConfirmationAlert
(context: Context,confirmationListener: ConfirmationListener?) : Dialog(context), View.OnClickListener, DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {
    private var mContext: Context? = null
    private var index = 0
    private var mRequestType = 0
    private lateinit var mConfirmationListener:ConfirmationListener
    // View
    private var mTvAlertMessage: TextView? = null
    private var mBtnCancel: Button? = null
    private var mBtnYes: Button? = null
    private var mTvAlertTitle: TextView? = null

    init {
        mContext = context
        mConfirmationListener=confirmationListener!!;
        initDialogProperties()
        //
    }

    /**
     *
     */
    private fun initDialogProperties() {

        // This is the layout XML file that describes your Dialog layout
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        this.setContentView(R.layout.dialog_confirmation_alert)
        val view = this.window!!.decorView
        //
        this.setCancelable(true)
        this.setOnDismissListener(this)
        this.setOnCancelListener(this)
        //
        initView(view)
    }

    /**
     *
     */
    private fun setFontFamily() {


    }

    /**
     * Init all view  here
     */
    private fun initView(view: View) {
        view.setBackgroundResource(android.R.color.transparent)
        mTvAlertMessage = this.findViewById<View>(R.id.tv_alert_message) as TextView
        mBtnCancel = this.findViewById<View>(R.id.btn_confirmation_cancel) as Button
        mBtnYes = this.findViewById<View>(R.id.btn_confirmation_yes) as Button
        mTvAlertTitle = this.findViewById<View>(R.id.tv_alert_message_title) as TextView
        //
        setFontFamily()
        setClickEvent()
        //
    }

    /**
     *
     */
    private fun setClickEvent() {
        try {
            mBtnCancel!!.setOnClickListener(this)
            mBtnYes!!.setOnClickListener(this)
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_confirmation_cancel
                /** Cancel click */
            -> {
                mConfirmationListener?.onNo()
                dismiss()
            }
            R.id.btn_confirmation_yes
                /** Yes click */
            -> {
                // sending call back both the method here
                if (mConfirmationListener != null) {
                    mConfirmationListener.onYes(index, mRequestType)
                }
                dismiss()
            }
        }
    }


    override fun onCancel(dialog: DialogInterface) {

    }

    override fun onDismiss(dialog: DialogInterface) {
        //mConfirmationListener.onNo();
    }

    override fun show() {
        super.show()
    }

    /**
     *
     */
    fun showConfirmationDialog(message: String, index: Int, requestType: Int) {
        this.index = index
        this.mRequestType = requestType
        mTvAlertMessage!!.text = message
        this.show()
    }


}