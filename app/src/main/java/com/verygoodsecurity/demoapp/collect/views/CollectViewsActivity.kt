package com.verygoodsecurity.demoapp.collect.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.button.MaterialButton
import com.verygoodsecurity.api.blinkcard.VGSBlinkCardIntentBuilder
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.core.BaseDemoActivity
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.BrandParams
import com.verygoodsecurity.vgscollect.view.card.CardBrand
import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.validation.rules.PersonNameRule
import com.verygoodsecurity.vgscollect.view.card.validation.rules.VGSInfoRule
import com.verygoodsecurity.vgscollect.view.core.serializers.VGSExpDateSeparateSerializer
import com.verygoodsecurity.vgscollect.widget.CardVerificationCodeEditText
import com.verygoodsecurity.vgscollect.widget.ExpirationDateEditText
import com.verygoodsecurity.vgscollect.widget.PersonNameEditText
import com.verygoodsecurity.vgscollect.widget.SSNEditText
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
import com.verygoodsecurity.vgscollect.widget.VGSEditText

private const val TAG = "CollectViewsActivity"

/**
 * Demonstrates how to integrate VGS Collect SDK using prebuilt Collect Views.
 *
 * This screen shows:
 * - How to initialize and configure [VGSCollect]
 * - How to bind Collect Views to the form
 * - How to configure validation rules and serializers
 * - How to listen for field state changes
 * - How to submit data securely to a VGS proxy
 * - How to attach files to a submit request
 * - How to integrate BlinkCard scanner
 *
 * The activity uses "tied" views (XML-based binding) and sends collected
 * data to the configured VGS Vault via proxy.
 *
 * 📘 Official documentation:
 * https://docs.verygoodsecurity.com/vault/developer-tools/vgs-collect/android-sdk/index
 *
 * @see VGSCollect
 * @see VGSRequest
 * @see VgsCollectResponseListener
 */
class CollectViewsActivity : BaseDemoActivity(R.layout.activity_collect_views) {

    /**
     * Lazy initialization of [VGSCollect].
     *
     * Must be initialized with Activity context.
     */
    override val form: VGSCollect by lazy {
        VGSCollect(
            this@CollectViewsActivity,
            id,
            environment
        )
    }

    private val vgsTiedCardHolder: PersonNameEditText by lazy { findViewById(R.id.vgsTiedCardHolder) }
    private val vgsTiedCardNumber: VGSCardNumberEditText by lazy { findViewById(R.id.vgsTiedCardNumber) }
    private val vgsTiedExpiry: ExpirationDateEditText by lazy { findViewById(R.id.vgsTiedExpiry) }
    private val vgsTiedCvc: CardVerificationCodeEditText by lazy { findViewById(R.id.vgsTiedCvc) }
    private val vgsTiedPostalCode: VGSEditText by lazy { findViewById(R.id.vgsTiedPostalCode) }
    private val vgsTiedCity: VGSEditText by lazy { findViewById(R.id.vgsTiedCity) }
    private val vgsTiedSsn: SSNEditText by lazy { findViewById(R.id.vgsTiedSsn) }

    private val fileManageButton: MaterialButton by lazy { findViewById(R.id.mbFilesManage) }

    /**
     * Creates card scanning intent.
     *
     * Scanned values automatically populate corresponding Collect Views
     * based on their field names.
     */
    override fun createScanIntent(): Intent {
        return VGSBlinkCardIntentBuilder(this)
            .setCardHolderFieldName(vgsTiedCardHolder.getFieldName())
            .setCardNumberFieldName(vgsTiedCardNumber.getFieldName())
            .setExpirationDateFieldName(vgsTiedExpiry.getFieldName())
            .setCVCFieldName(vgsTiedCvc.getFieldName())
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCollectForm()
    }

    /**
     * Propagates activity result to [VGSCollect].
     *
     * Required for:
     * - Handling card scanner scan results
     * - Handling file attachment results
     */
    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        form.onActivityResult(requestCode, resultCode, data)
        updateFilesManageButtonState()
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
        form.onDestroy()
    }

    /**
     * Initializes and configures the VGS Collect integration.
     *
     * Integration flow:
     * 1. Handle submit response
     * 2. Observe field validation state
     * 3. Configure field validation & serialization
     * 4. Bind fields to form
     * 5. Setup submit and file actions
     */
    private fun initCollectForm() {
        // ==========================================================
        // STEP 1: Handle submit response
        // ==========================================================
        form.addOnResponseListeners(object : VgsCollectResponseListener {
            override fun onResponse(response: VGSResponse?) {
                setLoading(false)

                when (response) {
                    is VGSResponse.SuccessResponse -> {
                        Log.d(TAG, "Submit success: ${response.body}")
                    }

                    is VGSResponse.ErrorResponse -> {
                        Log.e(TAG, "Submit error: ${response.errorCode}")
                    }

                    else -> Unit
                }
            }
        })

        // ==========================================================
        // STEP 2: Observe field state changes
        // ==========================================================
        form.addOnFieldStateChangeListener(object : OnFieldStateChangeListener {

            override fun onStateChange(state: FieldState) {
                // Triggered whenever any bound field changes its state.
                // Useful for:
                // - Enabling/disabling submit button
                // - Showing validation errors
                // - Tracking form completeness
                Log.d(TAG, "onStateChange: $state")
            }
        })

        // ==========================================================
        // STEP 3: Configure Collect Views
        // ==========================================================
        // Optional: Cardholder validation
        vgsTiedCardHolder.setRule(
            PersonNameRule.ValidationBuilder()
                .setAllowableMinLength(3, "Card holder name is too short.")
                .setAllowableMaxLength(20, "Card holder name is too long.")
                .build()
        )

        // Optional: Custom card brand example
        vgsTiedCardNumber.addCardBrand(
            CardBrand(
                "^7777",
                "<BRAND_NAME>",
                R.drawable.ic_custom_brand,
                BrandParams(
                    "#### #### #### ####",
                    ChecksumAlgorithm.LUHN,
                    rangeNumber = arrayOf(16),
                    rangeCVV = arrayOf(3)
                )
            )
        )

        // Optional: Expiration date serializer
        // Serializes expiration date into separate month/year fields:
        //
        // {
        //   "card": {
        //     "expiry": {
        //       "month": "MM",
        //       "year": "YY"
        //     }
        //   }
        // }
        vgsTiedExpiry.setSerializer(
            VGSExpDateSeparateSerializer(
                "card.expiry.month",
                "card.expiry.year",
            )
        )

        // Optional: custom postal code validation.
        vgsTiedPostalCode.setRule(
            VGSInfoRule.ValidationBuilder()
                .setRegex("^[0-9]{5}(?:-[0-9]{4})?$", "Invalid postal code.")
                .build()
        )

        // ==========================================================
        // STEP 4: Bind fields (REQUIRED)
        // ==========================================================
        // Only bound fields are included in submit payload.
        // If a view is not bound, its data will NOT be sent.
        form.bindView(vgsTiedCardHolder)
        form.bindView(vgsTiedCardNumber)
        form.bindView(vgsTiedExpiry)
        form.bindView(vgsTiedPostalCode)
        form.bindView(vgsTiedCvc)
        form.bindView(vgsTiedCity)
        form.bindView(vgsTiedSsn)

        // ==========================================================
        // STEP 5: Setup actions
        // ==========================================================
        findViewById<MaterialButton>(R.id.mbSubmit).setOnClickListener {
            setLoading(true)
            submitForm()
        }

        fileManageButton.setOnClickListener { handleFileManageClicked() }
    }

    /**
     * Builds and submits request to VGS proxy.
     *
     * Important:
     * - All bound fields are automatically collected.
     * - Validation runs before submission.
     *
     * asyncSubmit() sends data to the configured Vault route.
     */
    private fun submitForm() {
        val request: VGSRequest = VGSRequest.VGSRequestBuilder()
            .setMethod(HTTPMethod.POST)
            .setPath(path)
            // Optional: Add custom headers to request
            .setCustomHeader(mapOf("custom-header-name" to "value"))
            // Optional: Add additional JSON fields
            .setCustomData(mapOf("extraData" to "value"))
            .build()
        form.asyncSubmit(request)
    }

    /**
     * Attaches or detaches a file for the form submission.
     * Files are sent together with collected data as multipart request.
     */
    private fun handleFileManageClicked() {
        val provider = form.getFileProvider()
        if (provider.getAttachedFiles().isNotEmpty()) {
            provider.detachAll()
            updateFilesManageButtonState()
            Log.d(TAG, "All files detached.")
        } else {
            provider.attachFile(this@CollectViewsActivity, "<FILE_NAME>")
        }
    }

    /**
     * Updates the "Attach/Detach File" button text based on current attachments.
     *
     * - Shows "Attach" if no files are attached.
     * - Shows "Detach" if there are attached files.
     *
     * Called whenever files are attached or detached.
     */
    private fun updateFilesManageButtonState() {
        fileManageButton.text = if (form.getFileProvider().getAttachedFiles().isEmpty()) {
            getString(R.string.attach_btn_title)
        } else {
            getString(R.string.detach_btn_title)
        }
    }
}
