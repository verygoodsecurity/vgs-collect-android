package com.verygoodsecurity.vgscollect.view.core.serializers

import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.api.client.extension.logException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Represents [com.verygoodsecurity.vgscollect.widget.ExpirationDateEditText] date split serializer.
 * Note: [com.verygoodsecurity.vgscollect.widget.ExpirationDateEditText] fieldName will be ignored.
 *
 * @constructor primary constructor.
 * @param monthFieldName - this field name will be used for month in request json.
 * @param yearFieldName - this field name will be used for year in request json.
 */
class VGSExpDateSeparateSerializer constructor(
    private val monthFieldName: String,
    private val yearFieldName: String,
) : FieldDataSerializer<VGSExpDateSeparateSerializer.Params, List<Pair<String, String>>>() {

    private lateinit var monthSDF: SimpleDateFormat
    private lateinit var yearSDF: SimpleDateFormat

    override fun serialize(params: Params): List<Pair<String, String>> {
        return try {
            val date = SimpleDateFormat(params.dateFormat, Locale.US).parse(params.date)
            if (date == null) {
                VGSCollectLogger.debug(
                    VGSExpDateSeparateSerializer::class.java.simpleName,
                    "Can't parse date!"
                )
                return emptyList()
            }
            listOf(
                monthFieldName to getMonthFormat(params.dateFormat).format(date),
                yearFieldName to getYearFormat(params.dateFormat).format(date)
            )
        } catch (e: Exception) {
            logException(e)
            emptyList()
        }
    }

    private fun getMonthFormat(dateFormat: String?): SimpleDateFormat {
        return if (this::monthSDF.isInitialized) {
            monthSDF
        } else {
            try {
                val monthFormat = when {
                    dateFormat?.contains("MMMM") == true -> "MMMM"
                    dateFormat?.contains("MMM") == true -> "MMM"
                    dateFormat?.contains("MM") == true -> "MM"
                    dateFormat?.contains("M") == true -> "M"
                    else -> DEFAULT_MONTH_FORMAT
                }
                SimpleDateFormat(monthFormat, Locale.US)
            } catch (e: Exception) {
                logException(e)
                SimpleDateFormat(DEFAULT_MONTH_FORMAT, Locale.US)
            }
        }
    }

    private fun getYearFormat(dateFormat: String?): SimpleDateFormat {
        return if (this::yearSDF.isInitialized) {
            yearSDF
        } else {
            try {
                val yearFormat = when {
                    dateFormat?.contains("yyyy") == true -> "yyyy"
                    dateFormat?.contains("yy") == true -> "yy"
                    else -> DEFAULT_YEAR_FORMAT
                }
                SimpleDateFormat(yearFormat, Locale.US)
            } catch (e: Exception) {
                logException(e)
                SimpleDateFormat(DEFAULT_YEAR_FORMAT, Locale.US)
            }
        }
    }

    companion object {

        private const val DEFAULT_MONTH_FORMAT = "MM"
        private const val DEFAULT_YEAR_FORMAT = "yyyy"
    }

    data class Params(val date: String, val dateFormat: String?)
}