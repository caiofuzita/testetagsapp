package com.example.tagtrainermobile.utils

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object FirebaseAnalyticsHelper {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun init(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    }

    fun logEvent(eventName: String, params: Bundle?) {
        firebaseAnalytics.logEvent(eventName, params)
    }

    fun setUserId(userId: String) {
        firebaseAnalytics.setUserId(userId)
    }

    fun setUserProperty(propertyName: String, value: String) {
        firebaseAnalytics.setUserProperty(propertyName, value)
    }
}
