package com.example

import android.app.Application
import android.util.Log
import com.example.data.local.QuranDatabase
import com.example.util.CrashReporter

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CrashReporter.log("Application", "MainApplication onCreate started")

        try {
            QuranDatabase.getDatabase(this)
            CrashReporter.log("Room", "QuranDatabase instantiated successfully")
        } catch (e: Exception) {
            Log.e("MainApplication", "Error initializing QuranDatabase", e)
        }
    }
}
