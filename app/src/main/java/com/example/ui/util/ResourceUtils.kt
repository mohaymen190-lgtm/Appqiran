package com.example.ui.util

import android.content.Context

object ResourceUtils {
    fun getDrawableResId(context: Context, name: String): Int {
        return try {
            val id = context.resources.getIdentifier(name, "drawable", context.packageName)
            id
        } catch (e: Exception) {
            0
        }
    }
}
