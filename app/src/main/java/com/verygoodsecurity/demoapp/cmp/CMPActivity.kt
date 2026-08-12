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
import com.verygoodsecurity.vgscollect.core.VgsCardAttributesLookupListener
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
 * - How to initialize [VGSCollect] session with authorization
 * - How to bind tied card fields (PAN, expiration date, CVC)
 * - How to request an access token before CMP operations
 * - How to call CMP `createCard` and handle submit response
 * - How to listen for card attributes lookup events
 *
 * Unlike other demo activities that use the synchronous [VGSCollect] constructor,
 * this activity uses [VGSCollect.session] for async initialization with auth.
 *
 * 📘 Official documentation:
 * https://docs.verygoodsecurity.com/vault/developer-tools/vgs-collect/android-sdk/index
 *
 * @see VGSCollect
 * @see VgsCollectResponseListener
 * @see VgsCardAttributesLookupListener
 */
class CMPActivity : BaseDemoActivity(R.layout.cmp_activity) {

    /**
     * Async initialization of [VGSCollect].
     *
     * Unlike other demo activities, CMP requires session-based initialization
     * via [VGSCollect.session], so the form starts as null and is set once the
     * session completes. The custom setter triggers [setupCollect] from the base class.
     */
    override var form: VGSCollect? = null
        set(value) {
            field = value
            value?.let { setupCollect(it) }
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
        initSession()
    }

    /**
     * Releases VGSCollect resources.
     *
     * Must be called to:
     * - Clear internal references
     * - Prevent memory leaks
     * - Properly dispose field bindings
     */
    override fun onDestroy() {
        super.onDestroy()
        form?.onDestroy()
    }

    /**
     * Initializes a [VGSCollect] session with authorization.
     *
     * This handles the async session creation flow:
     * 1. Request an access token via [NetworkHelper]
     * 2. Initialize [VGSCollect] session with the token
     * 3. On success, configure the collect form
     */
    private fun initSession() {
        VGSCollect.session(
            this,
            id,
            "test-a-1-collect-form",
            environment,
            authHandler = { onComplete ->
                NetworkHelper.accessToken(
                    onSuccess = onComplete,
                    onError = {
                        Log.e(TAG, "Access token error: $it")
                    }
                )
            },
            onSuccess = {
                Log.d(TAG, "Session initialized successfully")
                form = it
                initCollectForm(it)
            },
            onError = { code, message ->
                Log.e(TAG, "Session error: code=$code, message=$message")
            }
        )
    }

    /**
     * Initializes and configures the VGS Collect integration.
     *
     * Integration flow:
     * 1. Handle submit response
     * 2. Listen for card attributes lookup events
     * 3. Bind fields to form
     * 4. Setup create card action
     */
    private fun initCollectForm(form: VGSCollect) {
        // ==========================================================
        // STEP 1: Handle submit response
        // ==========================================================
        form.addOnResponseListeners(object : VgsCollectResponseListener {
            override fun onResponse(response: VGSResponse?) {
                setLoading(false)

                when (response) {
                    is VGSResponse.SuccessResponse -> {
                        Log.d(TAG, "Create card success: ${response.body}")
                    }

                    is VGSResponse.ErrorResponse -> {
                        Log.e(TAG, "Create card error: ${response.errorCode}")
                    }

                    else -> Unit
                }
            }
        })

        // ==========================================================
        // STEP 2: Listen for card attributes lookup events
        // ==========================================================
        form.setCardAttributesLookupListener(object : VgsCardAttributesLookupListener {

            override fun onStart() {
                Log.d(TAG, "Card attributes lookup started")
            }

            override fun onSuccess(code: Int, body: String) {
                Log.d(TAG, "Card attributes lookup success: code=$code, body=$body")
            }

            override fun onFailure(code: Int, body: String?, message: String?) {
                Log.e(TAG, "Card attributes lookup failure: code=$code, body=$body, message=$message")
            }
        })

        // ==========================================================
        // STEP 3: Bind fields (REQUIRED)
        // ==========================================================
        // Only bound fields are included in request payload.
        // If a view is not bound, its data will NOT be sent.
        form.cardNumber(cardNumberInput)
        form.cardExpirationDate(expiryInput)
        form.cardCVC(cvcInput)

        // ==========================================================
        // STEP 4: Setup actions
        // ==========================================================
        createCardButton.setOnClickListener {
            setLoading(true)
            form.createCard()
        }
    }
}