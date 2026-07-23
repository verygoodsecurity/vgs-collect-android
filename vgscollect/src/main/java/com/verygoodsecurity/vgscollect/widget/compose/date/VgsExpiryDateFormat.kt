package com.verygoodsecurity.vgscollect.widget.compose.date

import com.verygoodsecurity.vgscollect.widget.compose.util.format
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Card expiry date format used by [com.verygoodsecurity.vgscollect.widget.compose.state.VgsExpiryTextFieldState].
 *
 * Pick one for `inputDateFormat` (how the user types) and one for
 * `outputDateFormat` (how the value is sent to VGS). They can differ — for
 * example, accept `MM/yy` from the user but submit `yyyy/MM`.
 *
 * @property dateFormat underlying [SimpleDateFormat] pattern.
 * @property mask input mask derived from [dateFormat], where each digit is `#`.
 */
sealed class VgsExpiryDateFormat(val dateFormat: String) {

    val mask: String = dateFormat.replace('m', '#', true).replace('y', '#', true)

    val maskCharsCount: Int = mask.count { it == '#' }

    /** Parses [text] using this format, returning `null` when [text] is invalid. */
    fun parse(text: String): Date? {
        val formatted = text.format(mask)
        return try {
            SimpleDateFormat(dateFormat, Locale.US).apply {
                isLenient = false
            }.parse(formatted)
        } catch (_: Exception) {
            null
        }
    }

    /** Formats [date] using this format, returning `null` when [date] is `null` or invalid. */
    fun format(date: Date?): String? {
        return try {
            date?.let {
                SimpleDateFormat(dateFormat, Locale.US).apply {
                    isLenient = false
                }.format(date)
            }
        } catch (_: Exception) {
            null
        }
    }

    /** Reformats [text] from this format into [targetFormat], or `null` when [text] cannot be parsed. */
    fun convert(text: String, targetFormat: VgsExpiryDateFormat): String? {
        return targetFormat.format(parse(text))
    }

    /** Two-digit month / two-digit year (e.g. `12/27`). */
    object MonthShortYear : VgsExpiryDateFormat(dateFormat = "MM/yy")

    /** Two-digit month / four-digit year (e.g. `12/2027`). */
    object MonthLongYear : VgsExpiryDateFormat(dateFormat = "MM/yyyy")

    /** Two-digit year / two-digit month (e.g. `27/12`). */
    object ShortYearMonth : VgsExpiryDateFormat(dateFormat = "yy/MM")

    /** Four-digit year / two-digit month (e.g. `2027/12`). */
    object LongYearMonth : VgsExpiryDateFormat(dateFormat = "yyyy/MM")
}