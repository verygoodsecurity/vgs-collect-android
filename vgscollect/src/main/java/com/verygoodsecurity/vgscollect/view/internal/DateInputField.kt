package com.verygoodsecurity.vgscollect.view.internal

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.view.autofill.AutofillValue
import android.widget.DatePicker
import com.verygoodsecurity.vgscollect.core.model.setMaximumTime
import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.handleOutputFormat
import com.verygoodsecurity.vgscollect.core.storage.DependencyType
import com.verygoodsecurity.vgscollect.view.card.conection.InputCardExpDateConnection
import com.verygoodsecurity.vgscollect.view.card.formatter.date.BaseDateFormatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.DatePickerFormatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.FlexibleDateFormatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.StrictExpirationDateFormatter
import com.verygoodsecurity.vgscollect.view.card.formatter.rules.FormatMode
import com.verygoodsecurity.vgscollect.view.core.serializers.FieldDataSerializer
import com.verygoodsecurity.vgscollect.view.date.*
import com.verygoodsecurity.vgscollect.view.date.validation.TimeGapsValidator
import com.verygoodsecurity.vgscollect.widget.core.VisibilityChangeListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/** @suppress */
internal abstract class DateInputField(context: Context) : BaseInputField(context), View.OnClickListener {

    //region - Abstract properties
    internal abstract var inputDateFormat: DateFormat
    internal abstract var inclusiveRangeValidation: Boolean
    internal abstract var datePickerMinDate: Long?
    internal abstract var datePickerMaxDate: Long?
    //endregion

    //region - Properties
    private val selectedDate = Calendar.getInstance()
    private var fieldDateFormat: SimpleDateFormat? = null
    private var fieldDateOutputFormat: SimpleDateFormat? = null
    internal var minDate: Long? = null
        set(value) {
            field = value
            updateTimeGapsValidator()
        }
    internal var maxDate: Long? = null
        set(value) {
            field = value
            updateTimeGapsValidator()
        }
    private var formatterMode = FormatMode.STRICT
    private var formatter: DatePickerFormatter? = null
    private var fieldDataSerializers: List<FieldDataSerializer<*, *>>? = null
    private var datePickerMode: DatePickerMode = DatePickerMode.INPUT
    private var datePickerVisibilityChangeListener: VisibilityChangeListener? = null
    private var timeGapsValidator: TimeGapsValidator? = null

    override fun applyFieldType() {
        timeGapsValidator = TimeGapsValidator(inputDateFormat, inclusiveRangeValidation, minDate, maxDate).also {
            validator.addRule(it)
        }
        inputConnection = InputCardExpDateConnection(id, validator)

        val stateContent = FieldContent.CreditCardExpDateContent().apply {
            if (!text.isNullOrEmpty() && handleInputMode(text.toString())) {
                handleOutputFormat(
                    selectedDate,
                    fieldDateFormat,
                    fieldDateOutputFormat,
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

    private fun applyFormatter() {
        val baseFormatter: BaseDateFormatter = when (formatterMode) {
            FormatMode.STRICT -> StrictExpirationDateFormatter(this)
            FormatMode.FLEXIBLE -> FlexibleDateFormatter()
        }

        this.formatter = with(baseFormatter) {
            setMask(inputDateFormat.format)
            setMode(datePickerMode)
            applyNewTextWatcher(this)
            this
        }
    }

    private fun applyInputType() {
        if (!isValidInputType(inputType)) {
            inputType = InputType.TYPE_CLASS_DATETIME
        }
        refreshInput()
    }

    private fun isValidInputType(type: Int): Boolean {
        return type == InputType.TYPE_CLASS_TEXT ||
                type == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                type == InputType.TYPE_CLASS_DATETIME
    }

    override fun updateTextChanged(str: String) {
        inputConnection?.also {
            with(it.getOutput()) {
                if (str.isNotEmpty()) {
                    hasUserInteraction = true
                }
                content = createCreditCardExpDateContent(str)
            }
            it.run()
        }
    }

    private fun createCreditCardExpDateContent(str: String): FieldContent? {
        val c = FieldContent.CreditCardExpDateContent()
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
                fieldDateFormat,
                fieldDateOutputFormat,
                fieldDataSerializers
            )
        }
        c.vaultStorage = vaultStorage
        c.vaultAliasFormat = vaultAliasFormat
        return c
    }

    private fun handleInputMode(str: String): Boolean {
        return try {
            val currentDate = fieldDateFormat?.parse(str) ?: return false
            return if (fieldDateFormat!!.format(currentDate) == str) {
                selectedDate.time = currentDate
                if (!inputDateFormat.daysVisible) {
                    selectedDate.set(
                        Calendar.DAY_OF_MONTH,
                        selectedDate.getActualMaximum(Calendar.DATE)
                    )
                }
                selectedDate.setMaximumTime()
                true
            } else {
                false
            }
        } catch (e: ParseException) {
            false
        }
    }

    override fun onClick(v: View?) {
        showDatePickerDialog()
    }

    internal fun showDatePickerDialog(
        dialogMode: DatePickerMode,
        ignoreFieldMode: Boolean
    ) {
        if (ignoreFieldMode) {
            showDatePickerDialog(dialogMode)
        } else {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        showDatePickerDialog(datePickerMode)
    }

    private fun showDatePickerDialog(dialogMode: DatePickerMode) {
        val mode: DatePickerMode = when (dialogMode) {
            DatePickerMode.INPUT -> return
            DatePickerMode.DEFAULT -> datePickerMode
            else -> dialogMode
        }

        val tempC = Calendar.getInstance()
        tempC.time = selectedDate.time

        val ls = DatePicker.OnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
            tempC.set(Calendar.YEAR, year)
            tempC.set(Calendar.MONTH, monthOfYear)
            tempC.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }

        val pos = DialogInterface.OnClickListener { dialog, which ->
            selectedDate.time = tempC.time
            applyDate()
        }
        val neg = DialogInterface.OnClickListener { dialog, which -> }

        val pickerMinDate = when {
            minDate != null -> minDate
            datePickerMinDate != null -> datePickerMinDate
            else -> null
        }

        val pickerMaxDate = when {
            maxDate != null -> maxDate
            datePickerMaxDate != null -> datePickerMaxDate
            else -> null
        }

        val pickerBuilder = DatePickerBuilder(context, mode).apply {
            pickerMinDate?.let {
                setMinDate(it)
            }
            pickerMaxDate?.let {
                setMaxDate(it)
            }
            setCurrentDate(tempC.timeInMillis)
            setDayFieldVisibility(inputDateFormat.daysVisible)
            setOnDateChangedListener(ls)
            setOnPositiveButtonClick(pos)
            setOnNegativeButtonClick(neg)
            setOnVisibilityChangeListener(datePickerVisibilityChangeListener)
        }
        pickerBuilder.build().show()
    }

    private fun applyDate() {
        if (!inputDateFormat.daysVisible) {
            selectedDate.set(
                Calendar.DAY_OF_MONTH,
                selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH)
            )
        }
        val strDate = fieldDateFormat?.format(selectedDate.time) ?: ""
        setText(strDate)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        if (!text.isNullOrEmpty() && (datePickerMode == DatePickerMode.SPINNER || datePickerMode == DatePickerMode.CALENDAR)) {
            try {
                fieldDateFormat?.parse(text.toString())
                super.setText(text, type)
            } catch (e: ParseException) {
            }
        } else {
            super.setText(text, type)
        }
    }

    internal fun setOutputFormat(pattern: String?, default: String) {
        val format = if (
            pattern.isNullOrEmpty() ||
            (pattern.contains('T') && !pattern.contains("'T'"))) {
            default
        } else {
            pattern
        }

        fieldDateOutputFormat = SimpleDateFormat(format, Locale.US)
    }

    internal fun getInputFormat(): String {
        return inputDateFormat.format
    }

    internal fun setInputFormat(pattern: String?) {
        val parsedFormat = DateFormat.parsePatternToDateFormat(pattern)
        if (parsedFormat != null) {
            inputDateFormat = parsedFormat
        }

        fieldDateFormat = SimpleDateFormat(inputDateFormat.format, Locale.US)
        isListeningPermitted = true
        formatter?.setMask(inputDateFormat.format)
        isListeningPermitted = false
    }

    internal fun setDatePickerMode(mode: Int) {
        datePickerMode = DatePickerMode.values()[mode]

        when (datePickerMode) {
            DatePickerMode.CALENDAR, DatePickerMode.SPINNER -> {
                setIsActive(false)
            }
            DatePickerMode.INPUT, DatePickerMode.DEFAULT -> {
                datePickerMode = DatePickerMode.INPUT
                setIsActive(true)
            }
        }
        formatter?.setMode(datePickerMode)
    }

    internal fun getDatePickerMode() = datePickerMode

    private fun setIsActive(isActive: Boolean) {
        isCursorVisible = isActive
        isFocusable = isActive
        isFocusableInTouchMode = isActive
        isListeningPermitted = true
        if (isActive) {
            setOnClickListener(null)
            val filterLength = InputFilter.LengthFilter(inputDateFormat.size)
            filters = arrayOf(filterLength)
        } else {
            setOnClickListener(this)
            filters = arrayOf()
        }

        isListeningPermitted = false
    }

    private fun updateTimeGapsValidator() {
        timeGapsValidator?.let { validator.removeRule(it) }
        timeGapsValidator = TimeGapsValidator(inputDateFormat, inclusiveRangeValidation, minDate, maxDate).also {
            validator.addRule(it)
        }
    }

    override fun setInputType(type: Int) {
        val validType = validateInputType(type)
        super.setInputType(validType)
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

    internal fun setDatePickerVisibilityListener(listener: VisibilityChangeListener?) {
        datePickerVisibilityChangeListener = listener
    }

    internal fun setFieldDataSerializers(serializers: List<FieldDataSerializer<*, *>>?) {
        this.fieldDataSerializers = serializers
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
                value.isDate -> selectedDate.time = Date(value.dateValue)
                value.isText -> {
                    val newValue = parseTextDate(value)
                    super.autofill(newValue)
                }
                else -> {
                    super.autofill(value)
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private fun parseTextDate(value: AutofillValue): AutofillValue {
        val str = value.textValue.toString()
        return if (str.length == inputDateFormat.size) {
            value
        } else {
            // TODO: NEED HELP with the format "MM/yy"
            val newDateStr = value.textValue.toString().handleDate("MM/yy", inputDateFormat.format)
            if (newDateStr.isNullOrEmpty()) {
                value
            } else {
                AutofillValue.forText(newDateStr)
            }
        }
    }

    private fun String.handleDate(incomePattern: String, outcomePattern: String): String? {
        return try {
            val income = SimpleDateFormat(incomePattern, Locale.US)
            val currentDate = income.parse(this)
            val selectedDate = Calendar.getInstance()
            selectedDate.time = currentDate
            if (!inputDateFormat.daysVisible) {
                selectedDate.set(
                    Calendar.DAY_OF_MONTH,
                    selectedDate.getActualMaximum(Calendar.DATE)
                )
            }
            selectedDate.set(Calendar.HOUR, 23)
            selectedDate.set(Calendar.MINUTE, 59)
            selectedDate.set(Calendar.SECOND, 59)
            selectedDate.set(Calendar.MILLISECOND, 999)
            val outcome = SimpleDateFormat(outcomePattern, Locale.US)
            outcome.format(selectedDate.time)
        } catch (e: ParseException) {
            null
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
        when (value) {
            is Long -> fieldDateFormat?.let { setText(it.format(Date(value))) }
            is String -> setText(value)
        }
    }

    internal fun setFormatterMode(mode: Int) {
        with(FormatMode.values()[mode]) {
            formatterMode = this
        }
    }

    internal fun getFormatterMode(): Int = formatterMode.ordinal
}