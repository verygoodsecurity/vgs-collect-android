package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.util.AttributeSet
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.widget.core.DateEditText
import com.verygoodsecurity.vgscollect.widget.core.VisibilityChangeListener
import java.util.concurrent.TimeUnit

/**
 * Provides a user interface element for date input. The range of dates supported by this field is not configurable.
 *
 * @since 1.0.7
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
     * Interface definition for a callback to be invoked when the DatePicker Dialog changes
     * visibility.
     */
    interface OnDatePickerVisibilityChangeListener: VisibilityChangeListener
}