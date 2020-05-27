package com.verygoodsecurity.vgscollect.view.date

/**
 * The enum class represents all available modes of expiration date field.
 *
 * @version 1.0.3
 */
enum class DatePickerMode {

    /** Configure date from CalendarView */
    CALENDAR,

    /** Configure date from spinner DatePicker */
    SPINNER,

    /** Configure date manually. It supports only ``dd``, ``MM``, ``yyyy``,``yy`` forms. */
    INPUT,

    DEFAULT
}