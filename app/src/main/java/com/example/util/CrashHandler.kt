package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log
import com.example.CrashActivity
import java.io.PrintWriter
import java.io.StringWriter

object CrashHandler {
    private const val TAG = "StartupTracker"

    fun init(context: Context) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                Log.e(TAG, "FATAL CRASH INTERCEPTED on thread: ${thread.name}", throwable)
                
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                throwable.printStackTrace(pw)
                val stackTrace = sw.toString()

                // Launch CrashActivity to display the error directly on screen
                val intent = Intent(context, CrashActivity::class.java).apply {
                    putExtra("CRASH_LOG", stackTrace)
                    putExtra("THREAD_NAME", thread.name)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)

                // Kill current process after starting crash activity
                Process.killProcess(Process.myPid())
                System.exit(10)
            } catch (e: Exception) {
                Log.e(TAG, "Error in CrashHandler", e)
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }
    }
}
