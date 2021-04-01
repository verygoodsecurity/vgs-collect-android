package com.verygoodsecurity.vgscollect.view.internal

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.text.InputFilter
import android.text.InputType
import android.text.format.DateUtils
import android.view.Gravity
import android.view.View
import android.view.autofill.AutofillValue
import android.widget.DatePicker
import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.handleOutputFormat
import com.verygoodsecurity.vgscollect.core.storage.DependencyType
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.conection.InputCardExpDateConnection
import com.verygoodsecurity.vgscollect.view.card.formatter.date.BaseDateFormatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.DatePickerFormatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.FlexibleDateFormatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.StrictExpirationDateFormatter
import com.verygoodsecurity.vgscollect.view.card.formatter.rules.FormatMode
import com.verygoodsecurity.vgscollect.view.core.serializers.FieldDataSerializer
import com.verygoodsecurity.vgscollect.view.date.DatePickerBuilder
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
import com.verygoodsecurity.vgscollect.view.date.validation.TimeGapsValidator
import com.verygoodsecurity.vgscollect.view.date.validation.isInputDatePatternValid
import com.verygoodsecurity.vgscollect.widget.ExpirationDateEditText
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/** @suppress */
internal class DateInputField(context: Context): BaseInputField(context), View.OnClickListener {

    companion object {
        private const val MM_YYYY = "MM/yyyy"
        private const val MM_YY = "MM/yy"
        private const val DD = "dd"
        private const val SDF = "MM/dd/yyyy"
    }

    private var datePattern:String = MM_YYYY
    private var outputPattern:String = datePattern

    private var formatterMode = FormatMode.STRICT
    private var formatter: DatePickerFormatter? = null

    private var charLimit = datePattern.length

    private var minDate:Long = 0
    private var maxDate:Long = 0

    private val selectedDate = Calendar.getInstance()

    private val dateLimitationFormat = SimpleDateFormat(SDF, Locale.US)
    private var fieldDateFormat:SimpleDateFormat? = null
    private var fieldDateOutPutFormat:SimpleDateFormat? = null
    private var fieldDataSerializers: List<FieldDataSerializer<*, *>>? = null

    private var datePickerMode:DatePickerMode = DatePickerMode.INPUT
    private var isDaysVisible = true

    private var datePickerVisibilityChangeListener:ExpirationDateEditText.OnDatePickerVisibilityChangeListener? = null

    override var fieldType: FieldType = FieldType.CARD_EXPIRATION_DATE

    init {
        minDate = System.currentTimeMillis()
        maxDate = minDate + DateUtils.YEAR_IN_MILLIS * 20
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if(isRTL()) {
            hasRTL = true
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            textDirection = View.TEXT_DIRECTION_LTR
            gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        }
    }

    override fun applyFieldType() {
        val timeGapsValidator = TimeGapsValidator(datePattern, minDate, maxDate)

        inputConnection = InputCardExpDateConnection(id, timeGapsValidator)

        val stateContent = FieldContent.CreditCardExpDateContent().apply {
            if(!text.isNullOrEmpty() && handleInputMode(text.toString())) {
                handleOutputFormat(
                    selectedDate,
                    fieldDateFormat,
                    fieldDateOutPutFormat,
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
        val baseFormatter: BaseDateFormatter = when(formatterMode) {
            FormatMode.STRICT -> StrictExpirationDateFormatter(this)
            FormatMode.FLEXIBLE -> FlexibleDateFormatter()
        }

        this.formatter = with(baseFormatter) {
            setMask(datePattern)
            setMode(datePickerMode)
            applyNewTextWatcher(this)
            this
        }
    }

    private fun applyInputType() {
        if(!isValidInputType(inputType)) {
            inputType = InputType.TYPE_CLASS_DATETIME
        }
        refreshInput()
    }

    private fun isValidInputType(type: Int):Boolean {
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
                fieldDateOutPutFormat,
                fieldDataSerializers
            )
        }
        return c
    }

    private fun handleInputMode(str:String):Boolean {
        return try {
            val currentDate = fieldDateFormat!!.parse(str)
            return if(fieldDateFormat!!.format(currentDate) == str) {
                selectedDate.time = currentDate
                selectedDate.set(Calendar.DAY_OF_MONTH, selectedDate.getActualMaximum(Calendar.DATE))
                selectedDate.set(Calendar.HOUR, 23)
                selectedDate.set(Calendar.MINUTE, 59)
                selectedDate.set(Calendar.SECOND, 59)
                selectedDate.set(Calendar.MILLISECOND, 999)
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
        if(ignoreFieldMode) {
            showDatePickerDialog(dialogMode)
        } else {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        showDatePickerDialog(datePickerMode)
    }

    private fun showDatePickerDialog(dialogMode: DatePickerMode) {
        val mode:DatePickerMode  = when(dialogMode) {
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

        DatePickerBuilder(context, mode)
            .setMinDate(minDate)
            .setMaxDate(maxDate)
            .setCurrentDate(tempC.timeInMillis)
            .setDayFieldVisibility(isDaysVisible)
            .setOnDateChangedListener(ls)
            .setOnPositiveButtonClick(pos)
            .setOnNegativeButtonClick(neg)
            .setOnVisibilityChangeListener(datePickerVisibilityChangeListener)
            .build()
            .show()
    }

    private fun applyDate() {
        if(!isDaysVisible) {
            val dayLast = selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayLast)
        }
        val strDate = fieldDateFormat?.format(selectedDate.time)?:""
        setText(strDate)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        if(datePickerMode == DatePickerMode.SPINNER ||
            datePickerMode == DatePickerMode.CALENDAR) {
            try {
                fieldDateFormat?.parse(text.toString())
                super.setText(text, type)
            } catch (e: ParseException) { }
        } else {
            super.setText(text, type)
        }
    }

    internal fun setOutputPattern(pattern:String?) {
        outputPattern = if(pattern.isNullOrEmpty() ||
            (pattern.contains('T') && !pattern.contains("'T'"))) {
            datePattern
        } else {
            pattern
        }

        fieldDateOutPutFormat = SimpleDateFormat(outputPattern, Locale.US)
    }

    internal fun setDatePattern(pattern:String?) {
        datePattern = when {
            pattern.isNullOrEmpty() -> MM_YYYY
            datePickerMode == DatePickerMode.INPUT && pattern.isInputDatePatternValid().not() -> MM_YYYY
            else -> pattern
        }

        isDaysVisible = datePattern.contains(DD)
        fieldDateFormat = SimpleDateFormat(datePattern, Locale.US)

        isListeningPermitted = true

        formatter?.setMask(datePattern)
        isListeningPermitted = false
    }

    internal fun getDatePattern():String? = datePattern

    internal fun setDatePickerMode(mode:Int) {
        val pickerMode = DatePickerMode.values()[mode]

        when(pickerMode) {
            DatePickerMode.CALENDAR -> setupDialogMode(pickerMode)
            DatePickerMode.SPINNER -> setupDialogMode(pickerMode)
            DatePickerMode.DEFAULT -> setupInputMode()
            DatePickerMode.INPUT -> setupInputMode()
        }
        formatter?.setMode(datePickerMode)
    }

    private fun setupDialogMode(pickerMode: DatePickerMode) {
        datePickerMode = pickerMode
        setIsActive(false)
    }

    private fun setupInputMode() {
        datePickerMode = DatePickerMode.INPUT
        val p = datePattern
        setDatePattern(p)
        setIsActive(true)
    }

    internal fun getDatePickerMode() = datePickerMode

    private fun setIsActive(isActive:Boolean) {
        isCursorVisible = isActive
        isFocusable = isActive
        isFocusableInTouchMode = isActive
        isListeningPermitted = true
        if(isActive) {
            charLimit = datePattern.length

            setOnClickListener(null)
            val filterLength = InputFilter.LengthFilter(charLimit)
            filters = arrayOf(filterLength)
        } else {
            charLimit = 255

            setOnClickListener(this)
            filters = arrayOf()
        }

        isListeningPermitted = false
    }

    fun setMaxDate(date: String) {
        maxDate = dateLimitationFormat.parse(date).time
    }

    fun setMinDate(date: String) {
        minDate = dateLimitationFormat.parse(date).time
    }

    fun setMinDate(date: Long) {
        minDate = date
    }

    override fun setInputType(type: Int) {
        val validType = validateInputType(type)
        super.setInputType(validType)
        refreshInput()
    }

    private fun validateInputType(type: Int):Int {
        return when(type) {
            InputType.TYPE_CLASS_TEXT -> type
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> type
            InputType.TYPE_CLASS_DATETIME -> type
            InputType.TYPE_CLASS_NUMBER -> InputType.TYPE_CLASS_DATETIME
            InputType.TYPE_NUMBER_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD -> type
            else -> InputType.TYPE_CLASS_TEXT
        }
    }

    internal fun setDatePickerVisibilityListener(listener: ExpirationDateEditText.OnDatePickerVisibilityChangeListener?) {
        datePickerVisibilityChangeListener = listener
    }

    internal fun setFieldDataSerializers(serializers: List<FieldDataSerializer<*, *>>?) {
        this.fieldDataSerializers = serializers
    }

    override fun setupAutofill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE,
                View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_MONTH,
                View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DAY,
                View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_YEAR)
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
    private fun parseTextDate(value: AutofillValue):AutofillValue {
        val str = value.textValue.toString()
        return if(str.length == datePattern.length) {
            value
        } else {
            val newDateStr = value.textValue.toString().handleDate(MM_YY, datePattern)
            if(newDateStr.isNullOrEmpty()) {
                value
            } else {
                AutofillValue.forText(newDateStr)
            }
        }
    }

    private fun String.handleDate(incomePattern: String, outcomePattern: String):String? {
        return try {
            val income = SimpleDateFormat(incomePattern, Locale.US)
            val currentDate = income.parse(this)
            val selectedDate = Calendar.getInstance()
            selectedDate.time = currentDate
            selectedDate.set(Calendar.DAY_OF_MONTH, selectedDate.getActualMaximum(Calendar.DATE))
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
        if(dependency.dependencyType == DependencyType.TEXT) {
            applyTextValue(dependency.value)
        } else {
            super.dispatchDependencySetting(dependency)
        }
    }

    private fun applyTextValue(value: Any) {
        when(value) {
            is Long -> fieldDateFormat?.let{ setText(it.format(Date(value))) }
            is String -> setText(value)
        }
    }

    internal fun setFormatterMode(mode:Int) {
        with(FormatMode.values()[mode]) {
            formatterMode = this
        }
    }

    internal fun getFormatterMode():Int = formatterMode.ordinal
}