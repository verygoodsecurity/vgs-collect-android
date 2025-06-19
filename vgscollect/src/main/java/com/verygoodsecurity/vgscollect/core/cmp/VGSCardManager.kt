@file:Suppress("unused")

package com.verygoodsecurity.vgscollect.core.cmp

import android.content.Context
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.core.serializers.VGSExpDateSeparateSerializer
import com.verygoodsecurity.vgscollect.widget.ExpirationDateEditText
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText

typealias VGSCardManagerResponseListener = VgsCollectResponseListener

class VGSCardManager(
    context: Context,
    accountId: String,
    environment: String
) {

    private companion object {

        const val PATH = "/card"
        const val AUTH_HEADER_NAME = "Authorization"
        const val AUTH_HEADER_VALUE = "Bearer %s"

        const val PAN_FIELD_NAME = "data.attributes.pan"
        const val EXPIRY_FIELD_NAME = "data.attributes.expiry"
        const val EXPIRY_MONTH_FIELD_NAME = "data.attributes.exp_month"
        const val EXPIRY_YEAR_FIELD_NAME = "data.attributes.exp_year"
    }

    private val collect: VGSCollect = VGSCollect(
        context = context,
        vaultId = null,
        accountId = accountId,
        environment = environment,
        suffix = null,
        url = null,
    )

    fun addOnResponseListeners(listener: VGSCardManagerResponseListener) {
        collect.addOnResponseListeners(listener)
    }

    fun removeOnResponseListener(listener: VGSCardManagerResponseListener) {
        collect.removeOnResponseListener(listener)
    }

    fun bindView(view: InputFieldView) {
        collect.bindView(validateView(view))
    }

    fun unbindView(view: InputFieldView) {
        collect.unbindView(view)
    }

    fun createCard(accessToken: String) {
        if (accessToken.isEmpty()) {
            VGSCollectLogger.warn(message = "Can't create card, access token is empty!")
            return
        }
        collect.asyncSubmit(
            VGSRequest.VGSRequestBuilder()
                .setMethod(HTTPMethod.POST)
                .setPath(PATH)
                .setCustomHeader(mapOf(AUTH_HEADER_NAME to AUTH_HEADER_VALUE.format(accessToken)))
                .setFormat(VGSHttpBodyFormat.API_JSON)
                .build()
        )
    }

    private fun validateView(view: InputFieldView): InputFieldView? {
        return when (view) {
            is VGSCardNumberEditText -> setFieldName(view)
            is ExpirationDateEditText -> setFieldName(view)
            else -> null
        }
    }

    private fun setFieldName(view: VGSCardNumberEditText) = view.apply {
        setFieldName(PAN_FIELD_NAME)
    }

    private fun setFieldName(view: ExpirationDateEditText) = view.apply {
        setFieldName(EXPIRY_FIELD_NAME)
        setSerializer(
            VGSExpDateSeparateSerializer(
                monthFieldName = EXPIRY_MONTH_FIELD_NAME,
                yearFieldName = EXPIRY_YEAR_FIELD_NAME
            )
        )
    }
}