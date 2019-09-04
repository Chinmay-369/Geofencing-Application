package com.sprotte.geolocator.tracking.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.google.android.gms.location.LocationResult
import com.sprotte.geolocator.tracking.LocationTracker
import com.sprotte.geolocator.utils.getSharedPrefs
import com.sprotte.geolocator.utils.log


/**
 * Receiver for handling location updates.
 *
 * For apps targeting API level O
 * [android.app.PendingIntent.getBroadcast] should be used when
 * requesting location updates. Due to limits on background services,
 * [android.app.PendingIntent.getService] should not be used.
 *
 * Note: Apps running on "O" devices (regardless of targetSdkVersion) may receive updates
 * less frequently than the interval specified in the
 * [com.google.android.gms.location.LocationRequest] when the app is no longer in the
 * foreground.
 */
class LocationTrackerUpdateBroadcastReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent?) {

        log("onReceive $intent")

        if (intent == null) return

        val action = intent.action

        if (ACTION_PROCESS_UPDATES != action) return

        val result = LocationResult.extractResult(intent) ?: return
        log("$result")


        val intentClassName = context.getSharedPrefs().getString(LocationTracker.PREFS_NAME, "")
        log("$intentClassName")

        JobIntentService.enqueueWork(
            context,
            Class.forName(intentClassName!!),
            12445,
            intent
        )

    }

    companion object {

        internal val ACTION_PROCESS_UPDATES = "com.sprotte.geolocator.tracking.service" + ".PROCESS_UPDATES"
    }
}
