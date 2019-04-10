package com.uipep.android.smsmanager

class MySMSManager{
    interface MySMSListener {
        fun onPINReceived(pin : String)
    }
}