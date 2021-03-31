package com.verygoodsecurity.vgscollect.view.core.serializers

import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.api.client.extension.logException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Represents [com.verygoodsecurity.vgscollect.widget.ExpirationDateEditText] date split serializer.
 * Note: [com.verygoodsecurity.vgscollect.widget.ExpirationDateEditText] fieldName & outputPattern will be ignored.
 *
 * @constructor primary constructor.
 * @param monthFieldName - this field name will be used for month in request json.
 * @param yearFieldName - this field name will be used for year in request json.
 * @param monthFormat - format, for example: MMM.
 * @param yearFormat - format, for example: yyyy.
 */
class VGSExpDateSeparateSerializer constructor(
    private val monthFieldName: String,
    private val yearFieldName: String,
    private val monthFormat: String,
    private val yearFormat: String
) : FieldDataSerializer<VGSExpDateSeparateSerializer.Params, List<Pair<String, String>>>() {

    private lateinit var monthSDF: SimpleDateFormat
    private lateinit var yearSDF: SimpleDateFormat

    override fun serialize(params: Params): List<Pair<String, String>> {
        return try {
            val date = SimpleDateFormat(params.dateFormat, Locale.getDefault()).parse(params.date)
            if (date == null) {
                VGSCollectLogger.debug(
                    VGSExpDateSeparateSerializer::class.java.simpleName,
                    "Can't parse date!"
                )
                return emptyList()
            }
            listOf(
                monthFieldName to getMonthFormat().format(date),
                yearFieldName to getYearFormat().format(date)
            )
        } catch (e: Exception) {
            logException(e)
            emptyList()
        }
    }

    private fun getMonthFormat(): SimpleDateFormat {
        return if (this::monthSDF.isInitialized) {
            monthSDF
        } else {
            try {
                SimpleDateFormat(monthFormat, Locale.getDefault())
            } catch (e: Exception) {
                logException(e)
                SimpleDateFormat(DEFAULT_MONTH_FORMAT, Locale.getDefault())
            }
        }
    }

    private fun getYearFormat(): SimpleDateFormat {
        return if (this::yearSDF.isInitialized) {
            yearSDF
        } else {
            try {
                SimpleDateFormat(yearFormat, Locale.getDefault())
            } catch (e: Exception) {
                logException(e)
                SimpleDateFormat(DEFAULT_YEAR_FORMAT, Locale.getDefault())
            }
        }
    }

    companion object {

        private const val DEFAULT_MONTH_FORMAT = "MM"
        private const val DEFAULT_YEAR_FORMAT = "yyyy"
    }

    data class Params(val date: String, val dateFormat: String?)
}