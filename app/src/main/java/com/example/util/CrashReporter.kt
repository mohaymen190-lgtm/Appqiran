package com.example.util

import android.util.Log

object CrashReporter {
    private const val TAG = "CrashReporter"
    private val logs = mutableListOf<String>()

    fun log(module: String, message: String) {
        val entry = "[$module] $message"
        Log.i(TAG, entry)
        synchronized(logs) {
            logs.add(entry)
            if (logs.size > 100) {
                logs.removeAt(0)
            }
        }
    }

    fun logError(module: String, message: String, throwable: Throwable? = null) {
        val entry = "[$module ERROR] $message"
        if (throwable != null) {
            Log.e(TAG, entry, throwable)
        } else {
            Log.e(TAG, entry)
        }
    }
}
