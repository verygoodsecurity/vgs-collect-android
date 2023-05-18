package com.verygoodsecurity.vgscollect.view.core.serializers

import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.api.client.extension.logException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Represents [com.verygoodsecurity.vgscollect.widget.RangeDateEditText] date split serializer.
 * Note: [com.verygoodsecurity.vgscollect.widget.RangeDateEditText] fieldName will be ignored.
 *
 * @constructor primary constructor.
 * @param dayFieldName - this field name will be used for day in request json.
 * @param monthFieldName - this field name will be used for month in request json.
 * @param yearFieldName - this field name will be used for year in request json.
 */
class VGSDateRangeSeparateSerializer constructor(
    private val dayFieldName: String,
    private val monthFieldName: String,
    private val yearFieldName: String
) : FieldDataSerializer<VGSDateRangeSeparateSerializer.Params, List<Pair<String, String>>>() {

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
                dayFieldName to getDayFormat().format(date),
                monthFieldName to getMonthFormat(params.dateFormat).format(date),
                yearFieldName to getYearFormat(params.dateFormat).format(date)
            )
        } catch (e: Exception) {
            logException(e)
            emptyList()
        }
    }

    data class Params(val date: String, val dateFormat: String?)
}