package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.content.DialogInterface
import android.text.InputFilter
import android.view.View
import android.widget.DatePicker
import com.verygoodsecurity.vgscollect.core.model.VGSDate
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.conection.InputCardExpDateConnection
import com.verygoodsecurity.vgscollect.view.card.formatter.date.DatePickerFormatter
import com.verygoodsecurity.vgscollect.view.date.DatePickerBuilder
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
import java.util.*

internal class DateRangeInputField(context: Context) : BaseInputField(context),
    View.OnClickListener {

    enum class VGSDateFormat(val displayFormat: String) {
        MM_DD_YYYY("mm-dd-yyyy"),
        DD_MM_YYYY("dd-mm-yyyy"),
        YYYY_MM_DD("yyyy-mm-dd");

        internal val daysCharacters = 2
        internal val monthCharacters = 2
        internal val yearCharacters = 4

        /// Date format pattern used to display in the text field
        internal val formatPattern: String
        get() {
            return when (this) {
                MM_DD_YYYY, DD_MM_YYYY -> {
                    "##-##-####"
                }
                YYYY_MM_DD -> {
                    "####-##-##"
                }
            }
        }

        fun dateFromInput(input: String?): VGSDate? {
            // Make sure if is a valid input string
            if (input.isNullOrEmpty()) {
                return null
            }
            // Check the amount of chars per date component are correct
            val expectedCount = daysCharacters + monthCharacters + yearCharacters
            if (input.length != expectedCount) {
                return null
            }
            // Format the date
            when (this) {
                // Get month, day and year
                MM_DD_YYYY -> return try {
                    // Get month, day and year
                    val month = input.take(monthCharacters).toInt()
                    val day = input.substring(monthCharacters, monthCharacters + daysCharacters).toInt()
                    val year = input.takeLast(yearCharacters).toInt()
                    // Try to create the date
                    VGSDate.createDate(day, month, year)
                } catch (e: Exception) {
                    null
                }
                // Get day, month and year
                DD_MM_YYYY -> return try {
                    // Get day, month and year
                    val day = input.take(daysCharacters).toInt()
                    val month = input.substring(daysCharacters, daysCharacters + monthCharacters).toInt()
                    val year = input.takeLast(yearCharacters).toInt()
                    // Try to create the date
                    VGSDate.createDate(day, month, year)
                } catch (e: Exception) {
                    null
                }
                // Get year, month and day
                YYYY_MM_DD -> return try {
                    // Get year, month and day
                    val year = input.take(yearCharacters).toInt()
                    val month = input.substring(yearCharacters, yearCharacters + monthCharacters).toInt()
                    val day = input.takeLast(daysCharacters).toInt()
                    // Try to create the date
                    VGSDate.createDate(day, month, year)
                } catch (e: Exception) {
                    null
                }
            }
        }

        companion object {

            // Default format
            val default = VGSDateFormat.MM_DD_YYYY

            fun parseInputToDateFormat(input: String?): VGSDateFormat? {
                // Try to parse to each of the formats
                return if (MM_DD_YYYY.dateFromInput(input) != null) {
                    MM_DD_YYYY
                } else if (DD_MM_YYYY.dateFromInput(input) != null) {
                    DD_MM_YYYY
                } else if (YYYY_MM_DD.dateFromInput(input) != null) {
                    YYYY_MM_DD
                } else {
                    null
                }
            }
        }
    }

    private var inputFormat = VGSDateFormat.default
    private var outputFormat = VGSDateFormat.default
    private var formatter: DatePickerFormatter? = null
    private val selectedDate: VGSDate? = null

    internal var inputPattern: String?
        get() {
            return inputFormat.displayFormat
        }
        set(value) {
            // Try to parse the date format
            val parsedFormat = VGSDateFormat.parseInputToDateFormat(value)
            inputFormat = when (parsedFormat) {
                null -> VGSDateFormat.default
                else -> parsedFormat
            }
            isListeningPermitted = true
            formatter?.setMask(inputFormat.formatPattern)
            isListeningPermitted = false
        }

    internal var outputPattern: String?
        get() {
            return outputFormat.displayFormat
        }
        set(value) {
            // Try to parse the date format
            val parsedFormat = VGSDateFormat.parseInputToDateFormat(value)
            outputFormat = when (parsedFormat) {
                null -> VGSDateFormat.default
                else -> parsedFormat
            }
        }

    internal var datePickerMode: DatePickerMode = DatePickerMode.INPUT
    internal var pickerMode: Int
        set(value) {
            datePickerMode = DatePickerMode.values()[value]
            when (datePickerMode) {
                DatePickerMode.CALENDAR, DatePickerMode.SPINNER -> {
                    isActive = false
                }
                DatePickerMode.INPUT, DatePickerMode.DEFAULT -> {
                    datePickerMode = DatePickerMode.INPUT
                    isActive = true
                }
            }
            formatter?.setMode(datePickerMode)
        }
        get() {
            return datePickerMode.ordinal
        }

    private var isActive: Boolean = true
        set(value) {
            field = value
            isCursorVisible = value
            isFocusable = value
            isFocusableInTouchMode = value
            isListeningPermitted = true
            filters = if (value) {
                setOnClickListener(null)
                val filterLength = InputFilter.LengthFilter(inputFormat.formatPattern.length)
                arrayOf(filterLength)
            } else {
                setOnClickListener(this)
                arrayOf()
            }
            isListeningPermitted = false
        }

    //region - Overrides
    override var fieldType: FieldType = FieldType.DATE_RANGE

    override fun applyFieldType() {
       // validator.addRule(TimeGapsValidator(datePattern, minDate, maxDate))
        inputConnection = InputCardExpDateConnection(id, validator)

//        val stateContent = FieldContent.CreditCardExpDateContent().apply {
//            if(!text.isNullOrEmpty() && handleInputMode(text.toString())) {
//                handleOutputFormat(
//                    selectedDate,
//                    fieldDateFormat,
//                    fieldDateOutPutFormat,
//                    fieldDataSerializers
//                )
//            } else {
//                data = text.toString()
//                rawData = data
//            }
//        }
//        val state = collectCurrentState(stateContent)
//
//        inputConnection?.setOutput(state)
//        inputConnection?.setOutputListener(stateListener)
//
//        applyFormatter()
//        applyInputType()
    }

    override fun onClick(v: View?) {
        showDatePickerDialog(datePickerMode)
    }
    //endregion

    //region - Internal methods
    internal fun showDatePickerDialog(
        dialogMode: DatePickerMode,
        ignoreFieldMode: Boolean
    ) {
        if (ignoreFieldMode) {
            showDatePickerDialog(dialogMode)
        } else {
            showDatePickerDialog(datePickerMode)
        }
    }
    //endregion

    //region - Private methods
    private fun showDatePickerDialog(dialogMode: DatePickerMode) {

        val pickerMode: DatePickerMode  = when(dialogMode) {
            DatePickerMode.INPUT -> return
            DatePickerMode.DEFAULT -> datePickerMode
            else -> dialogMode
        }

        val pickerCalendar = GregorianCalendar.getInstance()
        selectedDate?.let {
            pickerCalendar.set(Calendar.DAY_OF_MONTH, it.day)
            pickerCalendar.set(Calendar.MONTH, it.month)
            pickerCalendar.set(Calendar.YEAR, it.year)
        }

        val clickListener = DatePicker.OnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
            pickerCalendar.set(Calendar.YEAR, year)
            pickerCalendar.set(Calendar.MONTH, monthOfYear)
            pickerCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }

        val positiveListener = DialogInterface.OnClickListener { _, _ ->
//            selectedDate.time = tempC.time
//            applyDate()
        }

        val negativeListener = DialogInterface.OnClickListener { _, _ ->
            // Do nothing
        }

        DatePickerBuilder(context, pickerMode)
            // .setMinDate() // TODO
            // setMaxDate() // TODO
            .setCurrentDate(pickerCalendar.timeInMillis)
            .setDayFieldVisibility(true)
            .setOnDateChangedListener(clickListener)
            .setOnPositiveButtonClick(positiveListener)
            .setOnNegativeButtonClick(negativeListener)
            // .setOnVisibilityChangeListener(datePickerVisibilityChangeListener)
            .build()
            .show()
    }
    //endregion
}