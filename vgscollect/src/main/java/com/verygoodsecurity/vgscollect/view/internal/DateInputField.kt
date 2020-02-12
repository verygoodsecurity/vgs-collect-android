package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.DatePicker
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.InputCardExpDateConnection
import com.verygoodsecurity.vgscollect.view.card.text.ExpirationDateTextWatcher
import com.verygoodsecurity.vgscollect.view.date.DatePickerBuilder
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
import com.verygoodsecurity.vgscollect.view.date.validation.TimeGapsValidator
import java.text.SimpleDateFormat
import java.util.*

internal class DateInputField(context: Context): BaseInputField(context), View.OnClickListener {

    private var datePattern:String = "mm/yy"
    private var charLimit = datePattern.length

    private var minDate:Long = 0
    private var maxDate:Long = 0

    private val selectedDate = Calendar.getInstance()

    private val dateLimitationFormat = SimpleDateFormat("mm/dd/yyyy", Locale.getDefault())
    private var fieldDateFormat:SimpleDateFormat? = null

    private var datePickerMode:DatePickerMode = DatePickerMode.SPINNER
    private var isDaysVisible = true

    override var fieldType: FieldType = FieldType.CARD_EXPIRATION_DATE

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if(isRTL()) {
            hasRTL = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutDirection = View.LAYOUT_DIRECTION_LTR
                textDirection = View.TEXT_DIRECTION_LTR
            }
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

        val str = text.toString()
        val stateContent = FieldContent.InfoContent().apply {
            this.data = str
        }
        val state = collectCurrentState(stateContent)

        inputConnection?.setOutput(state)
        inputConnection?.setOutputListener(stateListener)

        applyInputType()
    }

    private fun applyInputType() {
        val type = inputType
        if(type == InputType.TYPE_TEXT_VARIATION_PASSWORD || type == InputType.TYPE_NUMBER_VARIATION_PASSWORD) {
            inputType = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            inputType = InputType.TYPE_CLASS_DATETIME
        }
        refreshInput()
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

    internal fun setDatePattern(pattern:String?) {
        datePattern = if(pattern.isNullOrEmpty()) {
            "mm/yy"
        } else {
            pattern
        }
        fieldDateFormat = SimpleDateFormat(datePattern, Locale.getDefault())
    }

    internal fun setDatePickerMode(mode:Int) {
        datePickerMode = DatePickerMode.values()[mode]

        when(datePickerMode) {
            DatePickerMode.CALENDAR -> setIsActive(false)
            DatePickerMode.SPINNER -> setIsActive(false)
            DatePickerMode.INPUT -> setIsActive(true)
        }
    }

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

    internal fun setDaysVisibility(state:Boolean) {
        isDaysVisible = state
    }
}