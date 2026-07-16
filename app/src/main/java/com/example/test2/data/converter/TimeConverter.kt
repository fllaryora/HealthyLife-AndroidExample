package com.example.test2.data.converter

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object TimeConverter {

    private val mFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    private val mFormatterDate = DateTimeFormatter.ISO_LOCAL_DATE

    @JvmStatic
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let {
            return mFormatter.parse(value, OffsetDateTime::from)
        }
    }

    @JvmStatic
    fun fromOffsetDateTime(date: OffsetDateTime?): String? {
        return date?.format(mFormatter)
    }

    @JvmStatic
    fun fromOffsetDate(date: OffsetDateTime?): String? {
        return date?.format(mFormatterDate)
    }

    @JvmStatic
    fun convertISOToHours(date : OffsetDateTime?): Float? {
        return if (date != null ) {
            date.toEpochSecond().toFloat()/3600F
        } else {
            null
        }
    }
}