package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.os.Build
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.autofill.AutofillValue
import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.handleOutputFormat
import com.verygoodsecurity.vgscollect.core.storage.DependencyType
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.formatter.rules.FormatMode
import com.verygoodsecurity.vgscollect.view.core.serializers.FieldDataSerializer
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
import com.verygoodsecurity.vgscollect.view.date.connection.InputDateRangeConnection
import com.verygoodsecurity.vgscollect.view.date.formatter.*
import com.verygoodsecurity.vgscollect.view.date.formatter.DateRangePickerFormatter
import com.verygoodsecurity.vgscollect.view.date.formatter.StrictDateRangeFormatter
import com.verygoodsecurity.vgscollect.view.date.validation.DateRangeValidator
import com.verygoodsecurity.vgscollect.widget.OnDatePickerVisibilityChangeListener
import java.util.*

internal class DateRangeInputField(context: Context) : BaseInputField(context),
    View.OnClickListener {

    //region - Private properties
    private var inputFormat: VGSDateFormat = VGSDateFormat.default
    private var outputFormat: VGSDateFormat = VGSDateFormat.default
    private var formatterMode = FormatMode.STRICT
    private var formatter: DateRangePickerFormatter? = null
    internal var datePickerMode: DatePickerMode = DatePickerMode.INPUT
    private var selectedDate: Date? = null
    internal var datePickerVisibilityChangeListener: OnDatePickerVisibilityChangeListener? = null
    internal var fieldDataSerializers: List<FieldDataSerializer<*, *>>? = null
    private var startDate: Date? = null
    private var endDate: Date? = null

    private var isActive: Boolean = true
        set(value) {
            field = value
            isCursorVisible = value
            isFocusable = value
            isFocusableInTouchMode = value
            isListeningPermitted = true
            filters = if (value) {
                setOnClickListener(null)
                val filterLength = InputFilter.LengthFilter(inputFormat.pattern.length)
                arrayOf(filterLength)
            } else {
                setOnClickListener(this)
                arrayOf()
            }
            isListeningPermitted = false
        }
    //endregion

    //region - Internal properties and methods accessed from `InputFieldView`
    internal var inputFormatRaw: String?
        get() {
            return inputFormat.format
        }
        set(value) {
            // Try to parse the date format
            val parsedFormat = VGSDateFormat.parsePatternToDateFormat(value)
            inputFormat = when (parsedFormat) {
                null -> VGSDateFormat.default
                else -> parsedFormat
            }
            isListeningPermitted = true
            formatter?.dateFormatter = inputFormat
            isListeningPermitted = false
        }

    internal var outputFormatRaw: String?
        get() {
            return outputFormat.format
        }
        set(value) {
            // Try to parse the date format
            val parsedFormat = VGSDateFormat.parsePatternToDateFormat(value)
            outputFormat = when (parsedFormat) {
                null -> VGSDateFormat.default
                else -> parsedFormat
            }
        }

    internal var startDateRaw: String? = null
        set(value) {
            startDate = inputFormat.dateFromString(value)
        }

    internal var endDateRaw: String? = null
        set(value) {
            endDate = inputFormat.dateFromString(value)
        }

    internal var formatterModeRaw: Int
        set(value) {
            with(FormatMode.values()[value]) {
                formatterMode = this
            }
        }
        get() {
            return formatterMode.ordinal
        }

    internal var datePickerModeRaw: Int
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

    //region - Overrides
    override var fieldType: FieldType = FieldType.DATE_RANGE

    override fun applyFieldType() {
        validator.addRule(DateRangeValidator(inputFormat, startDate, endDate))
        inputConnection = InputDateRangeConnection(id, validator)

        val stateContent = FieldContent.DateRangeContent().apply {
            val inputDate = inputFormat.dateFromString(text.toString())
            if (!text.isNullOrEmpty() && inputDate != null) {
                handleOutputFormat(
                    selectedDate,
                    inputFormat,
                    outputFormat,
                    fieldDataSerializers
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
        applyInputType()
    }

    override fun onClick(v: View?) {
        showDatePickerDialog(datePickerMode)
    }

    override fun updateTextChanged(str: String) {
        inputConnection?.also {
            with(it.getOutput()) {
                if (str.isNotEmpty()) {
                    hasUserInteraction = true
                }
                content = createDateRangeContent(str)
            }
            it.run()
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        var formattedText = text
        if (!text.isNullOrEmpty()) {
            val inputDate = inputFormat.dateFromString(text.toString())
            if (inputDate != null) {
                selectedDate = inputDate
                formattedText = inputFormat.stringFromDate(inputDate)
            }
        }
        super.setText(formattedText, type)
    }

    override fun setInputType(type: Int) {
        val validType = validateInputType(type)
        super.setInputType(validType)
        refreshInput()
    }

    override fun setupAutofill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(
                View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE,
                View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_MONTH,
                View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DAY,
                View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_YEAR
            )
        }
    }

    override fun autofill(value: AutofillValue?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            when {
                value == null -> {}
                value.isDate -> {
//                    val tempDate = VGSDateFormat
//                    selectedDate.time = Date(value.dateValue)
                }
                value.isText -> {
//                    val newValue = parseTextDate(value)
//                    super.autofill(newValue)
                }
                else -> {
                    super.autofill(value)
                }
            }
        }
    }

    override fun dispatchDependencySetting(dependency: Dependency) {
        if (dependency.dependencyType == DependencyType.TEXT) {
            applyTextValue(dependency.value)
        } else {
            super.dispatchDependencySetting(dependency)
        }
    }

    private fun applyTextValue(value: Any) {
//        when(value) {
//            is Long -> fieldDateFormat?.let{ setText(it.format(Date(value))) }
//            is String -> setText(value)
//        }
    }
    //endregion

    //region - Private methods
    private fun applyFormatter() {
        val formatter: DateRangePickerFormatter = when (formatterMode) {
            FormatMode.STRICT -> StrictDateRangeFormatter(
                inputFormat,
                datePickerMode,
                VGSDateFormat.divider,
                this
            )
            FormatMode.FLEXIBLE -> FlexibleDateRangeFormatter(inputFormat, datePickerMode)
        }

        this.formatter = with(formatter) {
            applyNewTextWatcher(this as? TextWatcher)
            this
        }
    }

    private fun applyInputType() {
        val validInputType = inputType == InputType.TYPE_CLASS_TEXT ||
                inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                inputType == InputType.TYPE_CLASS_DATETIME

        if (!validInputType) {
            inputType = InputType.TYPE_CLASS_DATETIME
        }
        refreshInput()
    }

    private fun validateInputType(type: Int): Int {
        return when (type) {
            InputType.TYPE_CLASS_TEXT -> type
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> type
            InputType.TYPE_CLASS_DATETIME -> type
            InputType.TYPE_CLASS_NUMBER -> InputType.TYPE_CLASS_DATETIME
            InputType.TYPE_NUMBER_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD -> type
            else -> InputType.TYPE_CLASS_TEXT
        }
    }

    private fun createDateRangeContent(str: String): FieldContent {
        val c = FieldContent.DateRangeContent()
        when {
            text.isNullOrEmpty() -> {
                c.data = str
                c.rawData = str
            }
            !handleInputMode(str) -> {
                c.data = str
                c.rawData = str
            }
            else -> c.handleOutputFormat(
                selectedDate,
                inputFormat,
                outputFormat,
                fieldDataSerializers
            )
        }
        return c
    }

    private fun handleInputMode(str: String): Boolean {
        val currentDate = inputFormat.dateFromString(str)
        return if (currentDate != null) {
            selectedDate = currentDate
            true
        } else {
            false
        }
    }

    private fun showDatePickerDialog(dialogMode: DatePickerMode) {
/*
        val pickerMode: DatePickerMode = when (dialogMode) {
            DatePickerMode.INPUT -> return
            DatePickerMode.DEFAULT -> datePickerMode
            else -> dialogMode
        }

        var pickerDate: VGSDate? = null
        val clickListener = DatePicker.OnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            pickerDate = VGSDate.createDate(dayOfMonth, monthOfYear + 1, year)
        }

        val positiveListener = DialogInterface.OnClickListener { _, _ ->
            selectedDate = pickerDate
            applyDate()
        }

        val negativeListener = DialogInterface.OnClickListener { _, _ ->
            // Do nothing
        }

        val pickerBuilder = DatePickerBuilder(context, pickerMode).apply {
            startDate?.let { startDate ->
                setMinDate(startDate.timeInMillis)
            }
            endDate?.let { endDate ->
                setMaxDate(endDate.timeInMillis)
            }
            selectedDate?.let { selectedDate ->
                setCurrentDate(selectedDate.timeInMillis)
            }
            setDayFieldVisibility(true)
            setOnDateChangedListener(clickListener)
            setOnPositiveButtonClick(positiveListener)
            setOnNegativeButtonClick(negativeListener)
            setOnVisibilityChangeListener(datePickerVisibilityChangeListener)
        }
        pickerBuilder.build().show()
 */
    }

    private fun applyDate() {
        selectedDate?.let {
            val strDate = inputFormat.stringFromDate(it)
            setText(strDate)
        } ?: run {
            setText("")
        }
    }
    //endregion
}