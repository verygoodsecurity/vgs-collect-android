package com.verygoodsecurity.vgscollect.util

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import com.verygoodsecurity.vgscollect.R
import java.util.*

fun showExpirationDialog(context:Context) {
    val c = Calendar.getInstance()
    val tempC = Calendar.getInstance()
    val v = LayoutInflater.from(context).inflate(R.layout.vgs_datepicker_layout, null)

    val dialog = AlertDialog.Builder(context)
        .setView(v)
        .setPositiveButton("Done") { d, v ->
            c.time = tempC.time
            Log.e("test", "new Date: ${c.time}")
        }
        .setNegativeButton("cancel", null).create()

    val dp = v.findViewById<DatePicker>(R.id.datePickerControl)
    dp.minDate = System.currentTimeMillis()
    dp?.findViewById<ViewGroup>(
        Resources.getSystem().getIdentifier("day", "id", "android")
    )?.visibility = View.GONE

    dp.init(c.get(Calendar.YEAR),
        c.get(Calendar.MONTH),
        c.get(Calendar.DAY_OF_MONTH)
    ) { _, year, monthOfYear, dayOfMonth ->
        tempC.set(year, monthOfYear, dayOfMonth)
    }

    dialog.show()
}