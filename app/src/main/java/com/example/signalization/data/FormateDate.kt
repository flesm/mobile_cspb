package com.example.signalization.data

import java.util.Locale
import java.util.TimeZone

class FormateDate {
    companion object {
        fun formatDate(isoDate: String): String {
            return try {
                val parser = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                parser.timeZone = TimeZone.getTimeZone("UTC")
                val date = parser.parse(isoDate)

                val formatter = java.text.SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("ru", "RU"))
                formatter.timeZone = TimeZone.getDefault()

                formatter.format(date!!)
            } catch (e: Exception) {
                isoDate
            }
        }
    }
}
