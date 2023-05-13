package com.verygoodsecurity.vgscollect.core.model

import java.util.*

class VGSDate private constructor(
    val day: Int,
    val month: Int,
    val year: Int
) {
    val timeInMillis: Long = Calendar.getInstance().apply {
        set(Calendar.MILLISECOND, getActualMinimum(Calendar.MILLISECOND))
        set(Calendar.SECOND, getActualMinimum(Calendar.SECOND))
        set(Calendar.MINUTE, getActualMinimum(Calendar.MINUTE))
        set(Calendar.HOUR_OF_DAY, getActualMinimum(Calendar.HOUR_OF_DAY))
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.MONTH, month)
        set(Calendar.YEAR, year)
    }.timeInMillis


    companion object {

        fun create(day: Int, month: Int, year: Int): VGSDate? {
            // Create a calendar and set the components to create the date
            val calendar = Calendar.getInstance().apply {
                set(Calendar.MILLISECOND, getActualMinimum(Calendar.MILLISECOND))
                set(Calendar.SECOND, getActualMinimum(Calendar.SECOND))
                set(Calendar.MINUTE, getActualMinimum(Calendar.MINUTE))
                set(Calendar.HOUR_OF_DAY, getActualMinimum(Calendar.HOUR_OF_DAY))
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.MONTH, month - 1)
                set(Calendar.YEAR, year)
            }

            // Get calendar values stored in the calendar
            val calDay = calendar.get(Calendar.DAY_OF_MONTH)
            val calMonth = calendar.get(Calendar.MONTH)
            val calYear = calendar.get(Calendar.YEAR)

            // If the calendar has the same values sent by parameter, the date is valid
            if (day == calDay && month == (calMonth + 1) && year == calYear) {
                return VGSDate(day, month, year)
            }

            // If the date is not valid, return null
            return null
        }
    }
}