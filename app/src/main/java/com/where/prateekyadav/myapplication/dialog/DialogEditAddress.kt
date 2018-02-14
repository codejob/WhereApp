package com.where.prateekyadav.myapplication.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText

import com.where.prateekyadav.myapplication.R
import com.where.prateekyadav.myapplication.Util.AppConstant
import com.where.prateekyadav.myapplication.appinterface.ConfirmationListener
import com.where.prateekyadav.myapplication.database.DataBaseController
import android.text.Selection
import android.text.Editable



/**
 * Created by Rohitashv on 05-Feb-18.
 */
class DialogEditAddress

(context: Context,confirmationListener: ConfirmationListener?,address:String,rowId:Int) : Dialog(context), View.OnClickListener, DialogInterface.OnDismissListener, ConfirmationListener {
    private var mContext: Context?
    private var mEdtTextNote: EditText? = null
    private var mBtnCancel: Button? = null
    private var mBtnSave: Button? = null
    private var mConfirmationListener:ConfirmationListener
    private var mAddress:String
    private  var mRowId:Int;

    init {
        mContext = context
        mConfirmationListener=confirmationListener!!
        mAddress=address
        mRowId=rowId;
        initDialogProperties()
    }

    private fun initDialogProperties() {

        // This is the layout XML file that describes your Dialog layout
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        this.setContentView(R.layout.dialog_add_text_note)
        val view = this.getWindow().getDecorView()
        //
        val wmlp = this.getWindow().getAttributes()
        val window = getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        //
        this.setCancelable(false)
        this.setOnDismissListener(this)
        //
        initView(view)
    }

    /**
     * Initialize All view here
     * @param view
     */
    private fun initView(view: View) {
        view.setBackgroundResource(android.R.color.transparent)
        mEdtTextNote = this.findViewById(R.id.edt_note_text) as EditText
        mBtnCancel = this.findViewById(R.id.btn_cancel) as Button
        mBtnSave = this.findViewById(R.id.btn_save) as Button
        //
        setNotesData()
        //
        setClickListener()
    }

    private fun setNotesData() {
        // TODO : set address here
    }

    /**
     * Here we set all click listener
     */
    private fun setClickListener() {
        mBtnCancel!!.setOnClickListener(this)
        mBtnSave!!.setOnClickListener(this)
    }

    // Handle click call backs
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_cancel -> {
                // Before dismiss show alert for save notes
                checkNotesTextBeforeCancel()
                return
            }

            R.id.btn_save -> {
                //
                checkAndCallSaveNotationMethod()
                return
            }
        }
    }

    /**
     * Method to check string is empty or not before
     * call save notation Method.
     */
    private fun checkAndCallSaveNotationMethod() {

        val textNotes = mEdtTextNote!!.text.toString().trim()
        if (textNotes != null && !textNotes.isEmpty()&&!textNotes.equals(mAddress)) {
            openConfirmationDialog(mRowId, mContext!!.resources.getString(R.string.str_alert_message_save_text_notation), AppConstant.REQUEST_EDIT_ADDRESS)
        } else if(textNotes.equals(mAddress)){
             this.dismiss();
        }else {
            mEdtTextNote!!.setHint(R.string.hint_notation_text)
            this.dismiss();
        }

    }

    /**
     * Method to check  notes is not empty and any changes is apply
     * if any condition is true from above we open an confirmation dialog
     */
    private fun checkNotesTextBeforeCancel() {
        val textAddress = mEdtTextNote!!.text.toString().trim()
        if (textAddress != null && !textAddress.isEmpty() && !textAddress.equals(mAddress)) {
            openConfirmationDialog(mRowId, mContext!!.resources.getString(R.string.str_alert_message_change_address), AppConstant.REQUEST_ADDRESS_CHANGE_ALERT)
        } else {
            this.dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {

    }

    /**
     * Method to show dialog here
     */
    fun showDialog() {
        mEdtTextNote!!.setText(mAddress)
        val position = mEdtTextNote!!.length()
        val etext = mEdtTextNote!!.getText()
        Selection.setSelection(etext, position)
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        this.show()

    }


    /**
     * Method to open confirmation alert dialog
     * @param index
     */
    private fun openConfirmationDialog(index: Int, msg: String,requestCode: Int) {
        //
        val dialogConfirmationAlert = DialogConfirmationAlert(mContext!!, this)
        dialogConfirmationAlert.showConfirmationDialog(msg, index,requestCode)

    }

    override fun onYes(index: Int) {


    }

    override fun onNo() {
        this.dismiss()
    }

    override fun onYes(index: Int, requestType: Int) {

        if (requestType== AppConstant.REQUEST_EDIT_ADDRESS
                || requestType == AppConstant.REQUEST_ADDRESS_CHANGE_ALERT){
            val address = mEdtTextNote!!.text.toString().trim()
        DataBaseController(mContext).updateAddress(index,address)
        mConfirmationListener.onYes(index,requestType)
            this.dismiss()
        }
    }


}
