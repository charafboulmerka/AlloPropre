package com.sharingposts20.dzhealthmanager

import android.content.Context

private val SECOND_MILLIS = 1000
private val MINUTE_MILLIS = 60 * SECOND_MILLIS
private val HOUR_MILLIS = 60 * MINUTE_MILLIS
private val DAY_MILLIS = 24 * HOUR_MILLIS


fun getTimeAgo(time: Long): String? {
    var time = time
    if (time < 1000000000000L) {
        // if timestamp given in seconds, convert to millis
        time *= 1000
    }

    val now = System.currentTimeMillis()
    if (time > now || time <= 0) {
        return null
    }


    val diff = now - time
    if (diff < MINUTE_MILLIS) {
        return "juste maintenant";
    } else if (diff < 2 * MINUTE_MILLIS) {
        return "Il y'a une minute";
    } else if (diff < 50 * MINUTE_MILLIS) {
        return "il y a "+(diff / MINUTE_MILLIS).toString() + " minutes";
    } else if (diff < 90 * MINUTE_MILLIS) {
        return "il y a une heure";
    } else if (diff < 24 * HOUR_MILLIS) {
        return "il y a "+(diff / HOUR_MILLIS).toString() + " heures";
    } else if (diff < 48 * HOUR_MILLIS) {
        return "hier";
    } else {
        return "il y a "+ (diff / DAY_MILLIS).toString() + " jours";
    }
}