package fi.oulu.hshi.reminderapp;
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import android.content.BroadcastReceiver;
import android.content.ContentValues.TAG

import android.content.Context
import android.content.Intent

import java.util.*
import kotlin.math.absoluteValue

class GeofenceReceiver : BroadcastReceiver() {
    lateinit var key: String
    lateinit var text: String
    var reminderTime: Long = 0
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            val geofencingTransition = geofencingEvent.geofenceTransition

            if (geofencingTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofencingTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                // Retrieve data from intent
                if (intent != null) {
                    key = intent.getStringExtra("key")!!
                    reminderTime = intent.getLongExtra("reminderTime", 0)
                    text = intent.getStringExtra("message")!!

                    val currentTime = GregorianCalendar.getInstance().timeInMillis

                    if ((currentTime - reminderTime).absoluteValue <= 1000 * 60 * 20) {
                        MapsActivity2.showNotification(context.applicationContext,
                            "Notification Triggered due to location and time by task: $key"
                        )

                        val triggeringGeofences = geofencingEvent.triggeringGeofences
                        //MapsActivity2.removeGeofences(context, triggeringGeofences)
                    }
                }
            }
        }
    }
}