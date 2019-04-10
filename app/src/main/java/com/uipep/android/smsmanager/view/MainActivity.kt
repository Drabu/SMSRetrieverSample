package com.uipep.android.smsmanager.view

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.uipep.android.smsmanager.AppSignatureHelper
import com.uipep.android.smsmanager.MySMSBroadcastReceiver
import com.uipep.android.smsmanager.MySMSManager
import com.uipep.android.smsmanager.R


class MainActivity : AppCompatActivity(), MySMSManager.MySMSListener {

    var TAG = javaClass.simpleName


     override fun onPINReceived(pin: String) {
           Toast.makeText(this, "Inside MainActivity ${pin} ", Toast.LENGTH_LONG).show()
     }

     val RESOLVE_HINT = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//      You can use this get the hash in log cat
        AppSignatureHelper(this)

        MySMSBroadcastReceiver.bindListener(this)

        Handler().postDelayed({
            requestHint()
        }, 5000)

    }

    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
            .setHintPickerConfig(CredentialPickerConfig.Builder().setShowCancelButton(true).build())
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val intent = Auth.CredentialsApi.getHintPickerIntent(
            GoogleApiClient.Builder(this)
                .addApi(Auth.CREDENTIALS_API)
                .build(), hintRequest
        )
        startIntentSenderForResult(
            intent.getIntentSender(),
            RESOLVE_HINT, null, 0, 0, 0
        )
    }

    // Obtain the phone number from the result
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {
                val credential = data!!.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                Log.i(TAG, "User's Phone Number ${credential.id}")

                /*here you make the request to your server and send the sms*/
                initSMSRetriever()
            }
        }
    }

    private fun  initSMSRetriever(){
        val client = SmsRetriever.getClient(this )

        // Starts SmsRetriever1, which waits for ONE matching SMS message until timeout
        // (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
        // action SmsRetriever1#SMS_RETRIEVED_ACTION.
        val task = client.startSmsRetriever()

        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener {
            // Successfully started retriever, expect broadcast intent
            // ...
            Log.d(TAG, "Successfully started retriever, expect broadcast intent")
        }

        task.addOnFailureListener {
            // Failed to start retriever, inspect Exception for more details
            // ...
            Log.e(TAG, "Failed to start retriever, due to ${it.message}")

        }


    }


    override fun onDestroy() {
        super.onDestroy()
        MySMSBroadcastReceiver.releaseListener()
    }

}
