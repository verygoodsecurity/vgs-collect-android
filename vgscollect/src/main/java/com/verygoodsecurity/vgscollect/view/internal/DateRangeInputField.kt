package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.content.DialogInterface
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.DatePicker
import com.verygoodsecurity.vgscollect.core.model.VGSDate
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.handleOutputFormat
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.formatter.rules.FormatMode
import com.verygoodsecurity.vgscollect.view.date.DatePickerBuilder
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
import com.verygoodsecurity.vgscollect.view.date.connection.InputDateRangeConnection
import com.verygoodsecurity.vgscollect.view.date.formatter.DateRangePickerFormatter
import com.verygoodsecurity.vgscollect.view.date.formatter.FlexibleDateRangeFormatter
import com.verygoodsecurity.vgscollect.view.date.formatter.StrictDateRangeFormatter
import com.verygoodsecurity.vgscollect.view.date.formatter.VGSDateFormat
import com.verygoodsecurity.vgscollect.view.date.validation.DateRangeValidator
import java.util.*

internal class DateRangeInputField(context: Context) : BaseInputField(context),
    View.OnClickListener {

    private var inputDivider: String = VGSDateFormat.defaultDivider
    private var outputDivider: String = VGSDateFormat.defaultDivider
    private var startDate: VGSDate? = null
    private var endDate: VGSDate? = null
    private var inputFormat = VGSDateFormat.default
    private var outputFormat = VGSDateFormat.default
    private var formatter: DateRangePickerFormatter? = null
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
            formatter?.dateFormatter = inputFormat
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
            formatter?.pickerMode = datePickerMode
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

    private var formatterMode = FormatMode.STRICT
    internal var formatterModeRaw: Int
        set(value) {
            with(FormatMode.values()[value]) {
                formatterMode = this
            }
        }
        get() {
            return formatterMode.ordinal
        }

    //region - Overrides
    override var fieldType: FieldType = FieldType.DATE_RANGE

    override fun applyFieldType() {
        validator.addRule(DateRangeValidator(inputFormat, startDate, endDate))
        inputConnection = InputDateRangeConnection(id, validator)

        val stateContent = FieldContent.DateRangeContent().apply {
            val inputDate = inputFormat.dateFromInput(text.toString())
            if (!text.isNullOrEmpty() && inputDate != null) {
                handleOutputFormat(
                    selectedDate,
                    inputDivider,
                    outputDivider,
                    inputFormat,
                    outputFormat,
                    null
                )
            } else {
                data = text.toString()
                rawData = data
            }
        }
        val state = collectCurrentState(stateContent)

        inputConnection?.setOutput(state)
        inputConnection?.setOutputListener(stateListener)

        applyFormatter()
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
    private fun applyFormatter() {
        val formatter: DateRangePickerFormatter = when(formatterMode) {
            FormatMode.STRICT -> StrictDateRangeFormatter(inputFormat, datePickerMode, inputDivider, this)
            FormatMode.FLEXIBLE -> FlexibleDateRangeFormatter(inputFormat, datePickerMode)
        }

        this.formatter = with(formatter) {
            applyNewTextWatcher(this as? TextWatcher)
            this
        }
    }

    private fun showDatePickerDialog(dialogMode: DatePickerMode) {

        val pickerMode: DatePickerMode = when (dialogMode) {
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

        val clickListener =
            DatePicker.OnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
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