package com.sprotte.geolocator.tracking

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.sprotte.geolocator.R
import com.sprotte.geolocator.tracking.service.LocationTrackerUpdateBroadcastReceiver
import com.sprotte.geolocator.tracking.service.LocationTrackerUpdateIntentService
import com.sprotte.geolocator.utils.getRes
import com.sprotte.geolocator.utils.getSharedPrefs
import com.sprotte.geolocator.utils.log

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 *
 * @see https://github.com/googlesamples/android-play-location/blob/master/LocationUpdatesPendingIntent/app/src/main/java/com/google/android/gms/location/sample/locationupdatespendingintent/MainActivity.java
 *
 * https://codelabs.developers.google.com/codelabs/background-location-updates-android-o/
 * https://github.com/googlesamples/android-play-location
 */
object LocationTracker {

    private const val REQUEST_CODE = 5998
    internal val PREFS_NAME = LocationTrackerUpdateIntentService::class.java.canonicalName

    private fun getTrackingPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, LocationTrackerUpdateBroadcastReceiver::class.java)
        intent.action = LocationTrackerUpdateBroadcastReceiver.ACTION_PROCESS_UPDATES
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.

     * Sets up the location request. Android has two location request settings:
     * `ACCESS_COARSE_LOCATION` and `ACCESS_FINE_LOCATION`. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     *
     *
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     *
     *
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private fun getLocationRequest(context: Context, params: LocationTrackerParams = LocationTrackerParams(context)): LocationRequest {
        return LocationRequest().apply {
            interval = params.interval
            fastestInterval = params.fastestInterval
            priority = params.priority
            maxWaitTime = params.maxWaitTime
            smallestDisplacement = params.smallestDisplacement
        }
    }

    fun requestLocationUpdates(context: Context, clasz: Class<*>) {
        requestLocationUpdates(context, clasz, LocationTrackerParams(context))
    }

    /**
     * Do not forget to add one or both of the following permissions to AndroidManifest
     *  - ACCESS_COARSE_LOCATION
     *  - ACCESS_FINE_LOCATION
     *
     * Android has two location request settings:
     * `ACCESS_COARSE_LOCATION` and `ACCESS_FINE_LOCATION`. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     *
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     *
     */
    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(context: Context, clasz: Class<*>, params: LocationTrackerParams = LocationTrackerParams(context)) {

        context.getSharedPrefs().edit().putString(PREFS_NAME, clasz.canonicalName).apply()

        try {
            log("Starting location updates")
            LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(getLocationRequest(context, params), getTrackingPendingIntent(context))
        } catch (e: SecurityException) {
            log(e.message)
        }
    }

    fun removeLocationUpdates(context: Context) {
        log("Removing location updates")
        LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(getTrackingPendingIntent(context))
    }
}
