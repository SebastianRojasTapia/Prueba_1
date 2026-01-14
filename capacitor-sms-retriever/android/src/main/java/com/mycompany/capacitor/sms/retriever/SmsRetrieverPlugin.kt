package com.tuempresa.plugins.smsretriever

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.getcapacitor.*
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

@CapacitorPlugin(name = "SmsRetriever")
class SmsRetrieverPlugin : Plugin() {

    private var receiver: BroadcastReceiver? = null
    private var pendingCall: PluginCall? = null

    @PluginMethod
    fun start(call: PluginCall) {
        val activity = activity ?: run {
            call.reject("No activity")
            return
        }

        // Guarda la call para responder cuando llegue el SMS
        pendingCall = call

        val client = SmsRetriever.getClient(activity)
        val task = client.startSmsRetriever()

        task.addOnSuccessListener {
            registerReceiver(activity)
            // Respuesta inmediata: el listener quedó activo
            call.resolve(JSObject().put("started", true))
        }

        task.addOnFailureListener { e ->
            pendingCall = null
            call.reject("Failed to start SMS Retriever: ${e.message}")
        }
    }

    @PluginMethod
    fun stop(call: PluginCall) {
        unregisterReceiver()
        pendingCall = null
        call.resolve()
    }

    private fun registerReceiver(activity: Activity) {
        unregisterReceiver()

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (SmsRetriever.SMS_RETRIEVED_ACTION != intent.action) return

                val extras = intent.extras ?: return
                val status = extras.get(SmsRetriever.EXTRA_STATUS) as? Status ?: return

                when (status.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE) ?: ""
                        // Envía evento a JS (recomendado)
                        val data = JSObject().put("message", message)
                        notifyListeners("smsReceived", data)

                        // Si quieres resolver una llamada "pendiente", podrías hacerlo:
                        pendingCall?.resolve(JSObject().put("message", message))
                        pendingCall = null

                        unregisterReceiver()
                    }

                    CommonStatusCodes.TIMEOUT -> {
                        notifyListeners("smsTimeout", JSObject())
                        pendingCall?.reject("SMS Retriever timeout")
                        pendingCall = null
                        unregisterReceiver()
                    }
                }
            }
        }

        val filter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        activity.registerReceiver(receiver, filter)
    }

    private fun unregisterReceiver() {
        try {
            val a = activity
            if (a != null && receiver != null) a.unregisterReceiver(receiver)
        } catch (_: Exception) {
        } finally {
            receiver = null
        }
    }

    override fun handleOnDestroy() {
        unregisterReceiver()
        pendingCall = null
        super.handleOnDestroy()
    }
}