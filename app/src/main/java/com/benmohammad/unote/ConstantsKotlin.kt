package com.benmohammad.unote

import java.text.SimpleDateFormat
import java.util.*

class ConstantsKotlin {

    fun getCurrentTime(format: String = "yyyy-MMM-dd HH:mm:ss"): String {
        val date = Date()
        val dateFormat = SimpleDateFormat(format)
        return dateFormat.format(date)
    }
}