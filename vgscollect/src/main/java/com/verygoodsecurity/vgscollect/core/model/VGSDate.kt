package com.verygoodsecurity.vgscollect.core.model

import com.verygoodsecurity.vgscollect.util.extension.setMaximumTime
import java.util.Calendar

/**
 * Date representation that includes a day, month and year.
 *
 * @param day The day of the month.
 * @param month The month of the year.
 * @param year The year.
 */
class VGSDate private constructor(
    val day: Int,
    val month: Int,
    val year: Int
) {
    /** The time in milliseconds from the epoch. */
    val timeInMillis: Long = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.MONTH, month - 1)
        set(Calendar.YEAR, year)
        setMaximumTime()
    }.timeInMillis

    companion object {

        /**
         * Creates a [VGSDate] object.
         *
         * @param day The day of the month.
         * @param month The month of the year.
         * @param year The year.
         *
         * @return The [VGSDate] object, or null if the date is invalid.
         */
        fun create(day: Int, month: Int, year: Int): VGSDate? {
            // Create a calendar and set the components to create the date
            val calendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.MONTH, month - 1)
                set(Calendar.YEAR, year)
                setMaximumTime()
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