package com.verygoodsecurity.vgscollect.view.internal.core

import android.content.Context
import android.content.DialogInterface
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.widget.DatePicker
import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.handleOutputFormat
import com.verygoodsecurity.vgscollect.core.storage.DependencyType
import com.verygoodsecurity.vgscollect.util.extension.setMaximumTime
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.conection.InputDateConnection
import com.verygoodsecurity.vgscollect.view.card.formatter.date.BaseDateFormatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.DatePickerFormatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.FlexibleDateFormatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.FlexibleDateRangeFormatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.StrictDateRangeFormatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.StrictExpirationDateFormatter
import com.verygoodsecurity.vgscollect.view.card.formatter.rules.FormatMode
import com.verygoodsecurity.vgscollect.view.core.serializers.FieldDataSerializer
import com.verygoodsecurity.vgscollect.view.date.DatePickerBuilder
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
import com.verygoodsecurity.vgscollect.view.date.DateRangeFormat
import com.verygoodsecurity.vgscollect.view.date.validation.TimeGapsValidator
import com.verygoodsecurity.vgscollect.view.date.validation.isInputDatePatternValid
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.view.internal.ExpirationDateInputField
import com.verygoodsecurity.vgscollect.widget.core.VisibilityChangeListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** @suppress */
internal abstract class DateInputField(context: Context) : BaseInputField(context),
    View.OnClickListener {

    //region - Abstract properties
    internal abstract var inputDatePattern: String
    internal abstract var inclusiveRangeValidation: Boolean
    internal abstract var datePickerMinDate: Long?
    internal abstract var datePickerMaxDate: Long?
    internal abstract var isDaysVisible: Boolean
    //endregion

    //region - Properties
    private var formatterMode = FormatMode.STRICT
    private var formatter: DatePickerFormatter? = null
    internal open var minDate: Long? = null
        set(value) {
            field = value
            updateTimeGapsValidator()
        }
    internal open var maxDate: Long? = null
        set(value) {
            field = value
            updateTimeGapsValidator()
        }
    protected val selectedDate: Calendar = Calendar.getInstance()
    protected var datePickerMode: DatePickerMode = DatePickerMode.INPUT
    private var timeGapsValidator: TimeGapsValidator? = null
    private var fieldDateFormat: SimpleDateFormat? = null
    private var fieldDateOutputFormat: SimpleDateFormat? = null
    private var fieldDataSerializers: List<FieldDataSerializer<*, *>>? = null
    private var datePickerVisibilityChangeListener: VisibilityChangeListener? = null
    //endregion

    abstract fun validateDatePattern(pattern: String?): String

    override fun applyFieldType() {
        updateTimeGapsValidator()
        inputConnection = InputDateConnection(id, validator)

        val stateContent = FieldContent.DateContent().apply {
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
        inputConnection?.addOutputListener(stateListener)

        applyFormatter()
        applyInputType()
    }

    private fun applyFormatter() {
        val baseFormatter: BaseDateFormatter = when (formatterMode) {
            FormatMode.STRICT -> {
                if (fieldType == FieldType.CARD_EXPIRATION_DATE) {
                    StrictExpirationDateFormatter(this)
                } else {
                    StrictDateRangeFormatter(this)
                }
            }
            FormatMode.FLEXIBLE -> {
                if (fieldType == FieldType.CARD_EXPIRATION_DATE) {
                    FlexibleDateFormatter()
                } else {
                    FlexibleDateRangeFormatter()
                }
            }
        }

        this.formatter = with(baseFormatter) {
            setMask(inputDatePattern)
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
                content = createDateContent(str)
            }
            it.run()
        }
    }

    private fun createDateContent(str: String): FieldContent {
        val c = FieldContent.DateContent()
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
        c.isEnabledTokenization = isEnabledTokenization
        return c
    }

    private fun handleInputMode(str: String): Boolean {
        return try {
            val currentDate = fieldDateFormat?.parse(str) ?: return false
            return if (fieldDateFormat!!.format(currentDate) == str) {
                selectedDate.time = currentDate
                if (!isDaysVisible) {
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

        val ls = DatePicker.OnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            tempC.set(Calendar.YEAR, year)
            tempC.set(Calendar.MONTH, monthOfYear)
            tempC.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }

        val pos = DialogInterface.OnClickListener { _, _ ->
            selectedDate.time = tempC.time
            applyDate()
        }
        val neg = DialogInterface.OnClickListener { _, _ -> }

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
            setDayFieldVisibility(isDaysVisible)
            setOnDateChangedListener(ls)
            setOnPositiveButtonClick(pos)
            setOnNegativeButtonClick(neg)
            setOnVisibilityChangeListener(datePickerVisibilityChangeListener)
        }
        pickerBuilder.build().show()
    }

    private fun applyDate() {
        if (!isDaysVisible) {
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
                // Do nothing
            }
        } else {
            super.setText(text, type)
        }
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

    internal fun setOutputPattern(pattern: String?) {
        val outputPattern = if (pattern.isNullOrEmpty() ||
            (pattern.contains('T') && !pattern.contains("'T'"))
        ) {
            inputDatePattern
        } else {
            pattern
        }

        fieldDateOutputFormat = SimpleDateFormat(outputPattern, Locale.US)
    }

    internal fun setDatePattern(pattern: String?) {
        inputDatePattern = validateDatePattern(pattern)
        fieldDateFormat = SimpleDateFormat(inputDatePattern, Locale.US)
        isListeningPermitted = true
        formatter?.setMask(inputDatePattern)
        isListeningPermitted = false
    }

    internal fun getDatePattern(): String = inputDatePattern

    internal fun setDatePickerMode(mode: Int) {

        when(val pickerMode = DatePickerMode.values()[mode]) {
            DatePickerMode.CALENDAR, DatePickerMode.SPINNER -> setupDialogMode(pickerMode)
            DatePickerMode.DEFAULT, DatePickerMode.INPUT -> setupInputMode()
        }

        formatter?.setMode(datePickerMode)
    }

    private fun setupDialogMode(pickerMode: DatePickerMode) {
        datePickerMode = pickerMode
        setIsActive(false)
    }

    private fun setupInputMode() {
        datePickerMode = DatePickerMode.INPUT
        val p = inputDatePattern
        setDatePattern(p)
        setIsActive(true)
    }

    internal fun getDatePickerMode() = datePickerMode

    private fun setIsActive(isActive: Boolean) {
        isCursorVisible = isActive
        isFocusable = isActive
        isFocusableInTouchMode = isActive
        isListeningPermitted = true
        filters = if (isActive) {
            setOnClickListener(null)
            val filterLength = InputFilter.LengthFilter(inputDatePattern.length)
            arrayOf(filterLength)
        } else {
            setOnClickListener(this)
            arrayOf()
        }

        isListeningPermitted = false
    }

    override fun setInputType(type: Int) {
        val validType = validateInputType(type)
        super.setInputType(validType)
        refreshInput()
    }

    protected fun updateTimeGapsValidator() {
        timeGapsValidator?.let { validator.removeRule(it) }
        timeGapsValidator = TimeGapsValidator(
            inputDatePattern,
            isDaysVisible,
            inclusiveRangeValidation,
            minDate,
            maxDate
        ).also {
            validator.addRule(it)
        }
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