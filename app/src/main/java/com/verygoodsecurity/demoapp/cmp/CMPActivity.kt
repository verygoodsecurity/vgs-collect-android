package com.verygoodsecurity.demoapp.cmp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.button.MaterialButton
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.core.BaseDemoActivity
import com.verygoodsecurity.demoapp.utils.NetworkHelper
import com.verygoodsecurity.demoapp.utils.accessToken
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.util.extension.cardCVC
import com.verygoodsecurity.vgscollect.util.extension.cardExpirationDate
import com.verygoodsecurity.vgscollect.util.extension.cardNumber
import com.verygoodsecurity.vgscollect.widget.CardVerificationCodeEditText
import com.verygoodsecurity.vgscollect.widget.ExpirationDateEditText
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText

private const val TAG = "CMPActivity"

/**
 * Demonstrates CMP card creation flow using [VGSCollect] with Collect Views.
 *
 * This screen shows:
 * - How to initialize [VGSCollect] in an Activity
 * - How to bind tied card fields (PAN, expiration date, CVC)
 * - How to request an access token before CMP operations
 * - How to call CMP `createCard` and handle submit response
 *
 * 📘 Official documentation:
 * https://docs.verygoodsecurity.com/vault/developer-tools/vgs-collect/android-sdk/index
 *
 * @see VGSCollect
 * @see VgsCollectResponseListener
 */
class CMPActivity : BaseDemoActivity(R.layout.cmp_activity) {

    /**
     * Lazy initialization of [VGSCollect].
     *
     * Must be initialized with Activity context.
     */
    override val form: VGSCollect by lazy {
        VGSCollect(
            context = this@CMPActivity,
            id = id,
            environment = environment
        )
    }

    private val cardNumberInput: VGSCardNumberEditText by lazy { findViewById(R.id.vgsTiedPan) }
    private val expiryInput: ExpirationDateEditText by lazy { findViewById(R.id.vgsTiedExpiry) }
    private val cvcInput: CardVerificationCodeEditText by lazy { findViewById(R.id.vgsTiedCvc) }

    private val createCardButton: MaterialButton by lazy { findViewById(R.id.mbCreateCard) }

    override fun createScanIntent(): Intent? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCollectForm()
    }

    /**
     * Configures CMP form behavior.
     *
     * Setup flow:
     * 1. Observe create-card response from [VGSCollect]
     * 2. Bind tied card input views to the collector
     * 3. Request access token and trigger `createCard`
     */
    private fun initCollectForm() {
        // ==========================================================
        // STEP 1: Handle submit response
        // ==========================================================
        form.addOnResponseListeners(object : VgsCollectResponseListener {
            override fun onResponse(response: VGSResponse?) {
                setLoading(false)
                println(response)
            }
        })

        // ==========================================================
        // STEP 4: Bind fields (REQUIRED)
        // ==========================================================
        // Only bound fields are included in request payload.
        // If a view is not bound, its data will NOT be sent.
        form.cardNumber(cardNumberInput)
        form.cardExpirationDate(expiryInput)
        form.cardCVC(cvcInput)

        // ==========================================================
        // STEP 5: Setup actions
        // ==========================================================
        createCardButton.setOnClickListener {
            setLoading(true)
            // Access token is REQUIRED for CMP.
            NetworkHelper.accessToken(
                onSuccess = { token ->
                    form.createCard(token)
                },
                onError = {
                    Log.d(TAG, "Get access token error: $it")
                }
            )
        }
    }
}