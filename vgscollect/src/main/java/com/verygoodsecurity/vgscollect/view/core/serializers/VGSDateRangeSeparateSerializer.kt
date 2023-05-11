package com.verygoodsecurity.vgscollect.view.core.serializers

import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.view.date.formatter.VGSDateFormat

class VGSDateRangeSeparateSerializer constructor(
    private val dayFieldName: String,
    private val monthFieldName: String,
    private val yearFieldName: String
) : FieldDataSerializer<VGSDateRangeSeparateSerializer.Params, List<Pair<String, String>>>() {

    override fun serialize(params: Params): List<Pair<String, String>> {
        val format = VGSDateFormat.parsePatternToDateFormat(params.dateFormat)
        val dateComponents = format?.dateComponentsString(params.date)
        return if (dateComponents == null) {
            VGSCollectLogger.debug(
                VGSExpDateSeparateSerializer::class.java.simpleName,
                "Can't parse date!"
            )
            emptyList()
        } else {
            listOf(
                dayFieldName to dateComponents.day,
                monthFieldName to dateComponents.month,
                yearFieldName to dateComponents.year
            )
        }
    }

    data class Params(val date: String, val dateFormat: String?)
}