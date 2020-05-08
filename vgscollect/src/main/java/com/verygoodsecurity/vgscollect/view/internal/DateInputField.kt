package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.DatePicker
import androidx.core.widget.addTextChangedListener
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.handleOutputFormat
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.InputCardExpDateConnection
import com.verygoodsecurity.vgscollect.view.card.text.ExpirationDateTextWatcher
import com.verygoodsecurity.vgscollect.view.date.DatePickerBuilder
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
import com.verygoodsecurity.vgscollect.view.date.validation.TimeGapsValidator
import com.verygoodsecurity.vgscollect.view.date.validation.isInputDatePatternValid
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/** @suppress */
internal class DateInputField(context: Context): BaseInputField(context), View.OnClickListener {

    private var datePattern:String = "MM/yyyy"
    private var outputPattern:String = datePattern

    private var charLimit = datePattern.length

    private var minDate:Long = 0
    private var maxDate:Long = 0

    private val selectedDate = Calendar.getInstance()

    private val dateLimitationFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    private var fieldDateFormat:SimpleDateFormat? = null
    private var fieldDateOutPutFormat:SimpleDateFormat? = null

    private var datePickerMode:DatePickerMode = DatePickerMode.SPINNER
    private var isDaysVisible = true

    override var fieldType: FieldType = FieldType.CARD_EXPIRATION_DATE

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if(isRTL()) {
            hasRTL = true
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            textDirection = View.TEXT_DIRECTION_LTR
            gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        }
    }

    override fun onAttachedToWindow() {
        isListeningPermitted = true
        applyFieldType()
        super.onAttachedToWindow()
        isListeningPermitted = false
    }

    override fun applyFieldType() {
        val timeGapsValidator = TimeGapsValidator(datePattern, minDate, maxDate)

        inputConnection = InputCardExpDateConnection(id, timeGapsValidator)

        val stateContent = FieldContent.CreditCardExpDateContent().apply {
            handleOutputFormat(selectedDate, fieldDateFormat, fieldDateOutPutFormat)
        }
        val state = collectCurrentState(stateContent)

        inputConnection?.setOutput(state)
        inputConnection?.setOutputListener(stateListener)

        applyInputType()
    }

    override fun setupInputConnectionListener() {
        val handler = Handler(Looper.getMainLooper())
        addTextChangedListener {
            inputConnection?.getOutput()?.
            content = FieldContent.CreditCardExpDateContent().apply {
                if(datePickerMode == DatePickerMode.INPUT) {
                    try {
                        handleInputMode(it.toString())
                        handleOutputFormat(selectedDate, fieldDateFormat, fieldDateOutPutFormat)
                    } catch (e: ParseException) {
                        data = it.toString()
                        rawData = data
                    }
                } else {
                    handleOutputFormat(selectedDate, fieldDateFormat, fieldDateOutPutFormat)
                }
            }

            handler.removeCallbacks(inputConnection)
            handler.postDelayed(inputConnection, 200)
        }
    }

    private fun handleInputMode(str:String) {
        val currentDate = fieldDateFormat!!.parse(str)
        selectedDate.time = currentDate
        selectedDate.set(Calendar.DAY_OF_MONTH, selectedDate.getActualMaximum(Calendar.DATE))
        selectedDate.set(Calendar.HOUR, 23)
        selectedDate.set(Calendar.MINUTE, 59)
        selectedDate.set(Calendar.SECOND, 59)
        selectedDate.set(Calendar.MILLISECOND, 999)
    }

    override fun onClick(v: View?) {
        showDatePickerDialog()
    }

    private fun showDatePickerDialog() {
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

        DatePickerBuilder(context, datePickerMode)
            .setMinDate(minDate)
            .setMaxDate(maxDate)
            .setCurrentDate(tempC.timeInMillis)
            .setDayFieldVisibility(isDaysVisible)
            .setOnDateChangedListener(ls)
            .setOnPositiveButtonClick(pos)
            .setOnNegativeButtonClick(neg)
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

    internal fun setOutputPattern(pattern:String?) {
        outputPattern = if(pattern.isNullOrEmpty() ||
            (pattern.contains('T') && !pattern.contains("'T'"))) {
            datePattern
        } else {
            pattern
        }

        fieldDateOutPutFormat = SimpleDateFormat(outputPattern, Locale.getDefault())
    }

    internal fun setDatePattern(pattern:String?) {
        datePattern = when {
            pattern.isNullOrEmpty() -> "MM/yyyy"
            datePickerMode == DatePickerMode.INPUT && pattern.isInputDatePatternValid().not() -> "MM/yyyy"
            else -> pattern
        }

        isDaysVisible = datePattern.contains("dd")
        fieldDateFormat = SimpleDateFormat(datePattern, Locale.getDefault())
    }

    internal fun getDatePattern():String? = datePattern

    internal fun setDatePickerMode(mode:Int) {
        datePickerMode = DatePickerMode.values()[mode]

        when(datePickerMode) {
            DatePickerMode.CALENDAR -> setIsActive(false)
            DatePickerMode.SPINNER -> setIsActive(false)
            DatePickerMode.INPUT -> {
                val p = datePattern
                setDatePattern(p)
                setIsActive(true)
            }
        }
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
            applyNewTextWatcher(ExpirationDateTextWatcher(datePattern))
            val filterLength = InputFilter.LengthFilter(charLimit)
            filters = arrayOf(filterLength)
        } else {
            charLimit = 255

            setOnClickListener(this)
            filters = arrayOf()
            applyNewTextWatcher(null)
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
}