package com.verygoodsecurity.vgscollect.view.date

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.widget.core.VisibilityChangeListener
import java.util.Calendar

/** @suppress */
internal class DatePickerBuilder(private val context: Context, mode: DatePickerMode) {
    private val layout: ViewGroup
    private val datePickerControl: DatePicker
    private var currentDate: Calendar? = null
    private var minDate: Long? = null
    private var maxDate: Long? = null
    private var listener: DatePicker.OnDateChangedListener? = null
    private var positiveListener: DialogInterface.OnClickListener? = null
    private var negativeListener: DialogInterface.OnClickListener? = null
    private var visibilityChangeListener: VisibilityChangeListener? = null

    private var isDateVisible = true

    init {
        val layoutId = when (mode) {
            DatePickerMode.CALENDAR -> if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                // This is a bug in Android 5.0 and up that occurs when the datepicker is in its new material style calendar mode.
                R.layout.vgs_datepicker_spinner_layout
            } else {
                R.layout.vgs_datepicker_calendar_layout
            }

            DatePickerMode.SPINNER -> R.layout.vgs_datepicker_spinner_layout
            else -> R.layout.vgs_datepicker_calendar_layout
        }


        layout = LayoutInflater.from(context).inflate(layoutId, null) as ViewGroup
        datePickerControl = layout.findViewById(R.id.datePickerControl)
    }

    fun setDayFieldVisibility(state: Boolean): DatePickerBuilder {
        isDateVisible = state
        return this
    }

    fun setMinDate(minDate: Long): DatePickerBuilder {
        this.minDate = minDate
        return this
    }

    fun setMaxDate(maxDate: Long): DatePickerBuilder {
        this.maxDate = maxDate
        return this
    }

    fun setCurrentDate(date: Long): DatePickerBuilder {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        currentDate = calendar
        return this
    }

    fun setOnDateChangedListener(listener: DatePicker.OnDateChangedListener): DatePickerBuilder {
        this.listener = listener
        return this
    }

    fun setOnPositiveButtonClick(listener: DialogInterface.OnClickListener): DatePickerBuilder {
        positiveListener = listener
        return this
    }

    fun setOnNegativeButtonClick(listener: DialogInterface.OnClickListener): DatePickerBuilder {
        negativeListener = listener
        return this
    }

    fun build(): Dialog {
        if (currentDate == null) {
            currentDate = Calendar.getInstance()
        }

        val dialog = AlertDialog.Builder(context)
            .setView(layout)
            .setTitle(context.getString(R.string.vgs_date_picker_title))
            .setPositiveButton(
                context.getString(R.string.vgs_date_picker_positive_button),
                positiveListener
            )
            .setNegativeButton(
                context.getString(R.string.vgs_date_picker_negative_button),
                negativeListener
            )
            .create()

        setupTimeGaps()
        setupFieldVisibility()
        setupPickerControl()

        dialog.setOnDismissListener {
            visibilityChangeListener?.onDismiss()
        }
        dialog.setOnShowListener {
            visibilityChangeListener?.onShow()
        }

        return dialog
    }

    private fun setupPickerControl() {
        val dayOfMonth = if (isDateVisible) {
            currentDate!!.get(Calendar.DAY_OF_MONTH)
        } else {
            currentDate!!.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        datePickerControl.init(
            currentDate!!.get(Calendar.YEAR),
            currentDate!!.get(Calendar.MONTH),
            dayOfMonth,
            listener
        )
    }

    @SuppressLint("DiscouragedApi")
    private fun setupFieldVisibility() {
        if (!isDateVisible) {
            datePickerControl.findViewById<ViewGroup>(
                Resources.getSystem().getIdentifier("day", "id", "android")
            )?.visibility = View.GONE
        }
    }

    private fun setupTimeGaps() {
        if (minDate != null) {
            datePickerControl.minDate = minDate!!
        }
        if (maxDate != null) {
            if (minDate != null && maxDate!! > minDate!! || minDate == null) {
                datePickerControl.maxDate = maxDate!!
            }
        }
    }

    fun setOnVisibilityChangeListener(listener: VisibilityChangeListener?): DatePickerBuilder {
        visibilityChangeListener = listener
        return this
    }
}
