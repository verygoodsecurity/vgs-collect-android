package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.util.AttributeSet
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.widget.core.DateEditText
import com.verygoodsecurity.vgscollect.widget.core.VisibilityChangeListener
import java.util.concurrent.TimeUnit

/**
 * A user interface element for inputting a card's expiration date.
 */
class ExpirationDateEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : DateEditText(FieldType.CARD_EXPIRATION_DATE, context, attrs, defStyleAttr) {

    init {
        val minDate = System.currentTimeMillis()
        setMinDate(minDate)
        val daysInTwentyYears = 365L * 20
        val twentyYearsInMillis = TimeUnit.DAYS.toMillis(daysInTwentyYears)
        setMaxDate(minDate + twentyYearsInMillis)
    }

    /**
     * A listener for receiving notifications about the visibility of the date picker dialog.
     */
    interface OnDatePickerVisibilityChangeListener: VisibilityChangeListener
}