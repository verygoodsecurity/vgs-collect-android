package com.verygoodsecurity.vgscollect.core.model

import java.util.*

public class VGSDate private constructor(
    val day: Int,
    val month: Int,
    val year: Int
) : Comparable<VGSDate> {

    companion object {

        fun createDate(day: Int, month: Int, year: Int): VGSDate? {
            // Create a calendar and set the components to create the date
            val calendar = GregorianCalendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, day)
            calendar.set(Calendar.MONTH, month - 1)
            calendar.set(Calendar.YEAR, year)

            // Get the values stored in the calendar
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

    // Returns zero if this object is equal to the specified other object
    // a negative number if it's less than other
    // or a positive number if it's greater than other.
    override fun compareTo(other: VGSDate): Int {
        // Test year
        when {
            year < other.year -> {
                return -1
            }
            year > other.year -> {
                return 1
            }
        }

        // Same year, test month
        when {
            month < other.month -> {
                return -1
            }
            year > other.month -> {
                return 1
            }
        }

        // Same year and month, check day
        when {
            day < other.day -> {
                return -1
            }
            day > other.day -> {
                return 1
            }
        }

        // Same day, month and year
        return 0
    }
}