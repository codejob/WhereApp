package com.where.prateekyadav.myapplication.appinterface

/**
 * Created by Infobeans on 16-Aug-16.
 */
interface ConfirmationListener {
    fun onYes(index: Int)
    fun onNo()
    fun onYes(index: Int, requestType: Int)
}
