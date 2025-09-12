package com.common.model.extension

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale

fun String.toFormattedTime(): String {
    return try {
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
        val date = inputFormatter.parse(this)
        val outputFormatter = SimpleDateFormat("h a", Locale.getDefault())
        date?.let {
            outputFormatter.format(it)
        } ?: ""
    } catch (exp: Exception) {
        Log.e("toFormattedTime", "Error parsing or formatting time: $exp")
        ""
    }
}