package com.verygoodsecurity.vgscollect.view.internal

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.InputCardExpDateConnection
import com.verygoodsecurity.vgscollect.view.card.text.ExpirationDateTextWatcher
import com.verygoodsecurity.vgscollect.view.card.validation.CardExpDateValidator
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
import java.text.SimpleDateFormat
import java.util.*

internal class DateInputField(context: Context): BaseInputField(context), View.OnClickListener {

    private var datePattern:String = "mm/yy"

    private val selectedDate = Calendar.getInstance()
    private val sdf:SimpleDateFormat = SimpleDateFormat("MM/yy")

    private var datePickerMode:DatePickerMode = DatePickerMode.SPINNER

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
        setOnClickListener(this)
        applyFieldType()
        super.onAttachedToWindow()
        isListeningPermitted = false
    }

    override fun applyFieldType() {
        validator = CardExpDateValidator()
        inputConnection = InputCardExpDateConnection(id, validator)

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
        isListeningPermitted = true
        when(datePickerMode) {
            DatePickerMode.CALENDAR -> setupCalendarDialog()
            DatePickerMode.SPINNER -> setupSpinnerDialog()
            DatePickerMode.INPUT -> applyTextStyle()
        }

        isListeningPermitted = false
        dialog?.show()
    }


    private fun applyTextStyle() {
        applyNewTextWatcher(ExpirationDateTextWatcher)
        val filterLength = InputFilter.LengthFilter(7)
        filters = arrayOf(filterLength)
    }


    private var dialog: Dialog? = null
    private fun setupCalendarDialog() {
        filters = arrayOf()
        applyNewTextWatcher(null)
        dialog = DatePickerDialog(context,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                applyDate()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.getActualMinimum(Calendar.DAY_OF_MONTH)
        )
    }

    private fun setupSpinnerDialog() {
        filters = arrayOf()
        applyNewTextWatcher(null)
        val tempC = Calendar.getInstance()

        val v = LayoutInflater.from(context).inflate(R.layout.vgs_datepicker_layout, null) as DatePicker

        dialog = AlertDialog.Builder(context)
            .setView(v)
            .setPositiveButton("Done") { d, v ->
                selectedDate.time = tempC.time
                applyDate()
            }
            .setNegativeButton("cancel", null)
            .create()

        val dp = v.findViewById<DatePicker>(R.id.datePickerControl)
//        dp.minDate = System.currentTimeMillis()
//        dp?.findViewById<ViewGroup>(  //todo move to expirationDate Field
//            Resources.getSystem().getIdentifier("day", "id", "android")
//        )?.visibility = View.GONE

        dp.init(selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ) { _, year, monthOfYear, dayOfMonth ->
            tempC.set(Calendar.YEAR, year)
            tempC.set(Calendar.MONTH, monthOfYear)
        }
    }

    private fun applyDate() {
        val strDate = sdf.format(selectedDate.time)
        setText(strDate)
    }

    internal fun setDatePattern(pattern:String?) {
        datePattern = if(pattern.isNullOrEmpty()) {
            "mm/yy"
        } else {
            pattern
        }
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
    }
}