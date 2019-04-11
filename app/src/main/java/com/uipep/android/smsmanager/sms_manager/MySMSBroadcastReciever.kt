package com.uipep.android.smsmanager

import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.auth.api.phone.SmsRetriever
import android.support.v4.app.NotificationCompat.getExtras
import android.os.Bundle
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.widget.Toast
import com.google.android.gms.common.api.Status


class MySMSBroadcastReceiver : BroadcastReceiver() {

    companion object {

        var mySMSManager : MySMSListener? = null

        fun bindListener(mySMSManager : MySMSListener) {
            this.mySMSManager =  mySMSManager
        }

        fun releaseListener() : Unit{
            mySMSManager = null
        }

    }

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status!!.getStatusCode()) {
                CommonStatusCodes.SUCCESS -> {
                    // Get SMS message contents
                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    /*you can extract the pin from text message here*/
                    if (mySMSManager != null) mySMSManager!!.onPINReceived(message)
                }

                CommonStatusCodes.TIMEOUT -> {
                    Toast.makeText(context, "SMS Timed Out", Toast.LENGTH_LONG).show()
                }
            }// Extract one-time code from the message and complete verification
            // by sending the code back to your server.
            // Waiting for SMS timed out (5 minutes)
            // Handle the error ...
        }
    }
}