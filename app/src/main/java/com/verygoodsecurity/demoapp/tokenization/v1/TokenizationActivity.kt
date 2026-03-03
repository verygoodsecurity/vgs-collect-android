package com.verygoodsecurity.demoapp.tokenization.v1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.preference.PreferenceManager
import com.google.android.material.button.MaterialButton
import com.verygoodsecurity.api.blinkcard.VGSBlinkCardIntentBuilder
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.core.BaseDemoActivity
import com.verygoodsecurity.demoapp.tokenization.settings.TokenizationSettingsActivity
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.network.tokenization.VGSTokenizationRequest
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultStorageType
import com.verygoodsecurity.vgscollect.widget.CardVerificationCodeEditText
import com.verygoodsecurity.vgscollect.widget.ExpirationDateEditText
import com.verygoodsecurity.vgscollect.widget.PersonNameEditText
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText

private const val TAG = "V1 TokenizationActivity"

/**
 * Demonstrates **VGS Collect V1 tokenization flow**.
 *
 * This Activity showcases how to:
 *
 * 1. Initialize [VGSCollect]
 * 2. Bind secure Collect Views
 * 3. Configure tokenization options dynamically
 * 4. Submit data using [VGSTokenizationRequest]
 * 5. Handle success/error responses
 *
 * 🔐 Unlike V2 (`createAliases()`), this version explicitly calls
 * [VGSCollect.tokenize] with a [VGSTokenizationRequest].
 *
 * Configuration is controlled via shared preferences
 * (see [TokenizationSettingsActivity]).
 *
 * 📘 Official documentation:
 * https://docs.verygoodsecurity.com/vault/developer-tools/vgs-collect/android-sdk/index
 *
 * @see VGSCollect
 * @see VGSTokenizationRequest
 * @see VgsCollectResponseListener
 */
class TokenizationActivity : BaseDemoActivity(R.layout.activity_tokenization) {

    /**
     * Lazy initialization of [VGSCollect].
     *
     * Must be initialized with Activity context.
     */
    override val form: VGSCollect by lazy {
        VGSCollect(
            this@TokenizationActivity,
            id,
            environment
        )
    }

    private val vgsTiedCardHolder: PersonNameEditText by lazy { findViewById(R.id.vgsTiedCardHolder) }
    private val vgsTiedCardNumber: VGSCardNumberEditText by lazy { findViewById(R.id.vgsTiedCardNumber) }
    private val vgsTiedExpiry: ExpirationDateEditText by lazy { findViewById(R.id.vgsTiedExpiry) }
    private val vgsTiedCvc: CardVerificationCodeEditText by lazy { findViewById(R.id.vgsTiedCvc) }

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
     * Re-applies tokenization configuration every time
     * the Activity resumes (in case settings changed).
     */
    override fun onResume() {
        super.onResume()
        configureTokenization()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.tokenization_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.scan -> true.also { scanResultLauncher.launch(createScanIntent()) }

            R.id.settings -> true.also { TokenizationSettingsActivity.start(this) }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Initializes and configures the VGS Collect integration.
     *
     * Integration flow:
     * 1. Handle submit response
     * 4. Bind fields to form
     * 5. Setup tokenize action
     */
    private fun initCollectForm() {
        // ==========================================================
        // STEP 1: Handle tokenize response
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
        // STEP 2: Bind fields (REQUIRED)
        // ==========================================================
        form.bindView(vgsTiedCardHolder)
        form.bindView(vgsTiedCardNumber)
        form.bindView(vgsTiedExpiry)
        form.bindView(vgsTiedCvc)

        // ==========================================================
        // STEP 5: Setup actions
        // ==========================================================
        findViewById<MaterialButton>(R.id.mbTokenize).setOnClickListener {
            setLoading(true)
            form.tokenize(VGSTokenizationRequest.VGSRequestBuilder().build())
        }
    }

    /**
     * Applies tokenization configuration based on shared preferences.
     *
     * Controls:
     * - Whether tokenization is enabled per field
     * - Storage type (PERSISTENT / VOLATILE)
     * - Alias format (FPE, UUID, RAW_UUID, etc.)
     */
    private fun configureTokenization() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        // ---- Card Holder Configuration ----
        vgsTiedCardHolder.setEnabledTokenization(
            preferences.getBoolean(
                getString(R.string.tokenization_card_holder_enabled_key),
                true
            )
        )
        val cardHolderStorageKey = getString(R.string.tokenization_card_holder_storage_key)
        parseStorage(preferences.getString(cardHolderStorageKey, null))?.let {
            vgsTiedCardHolder.setVaultStorageType(it)
        }
        val cardHolderAlisaFormatKey = getString(R.string.tokenization_card_holder_alias_format_key)
        parseAliasFormat(preferences.getString(cardHolderAlisaFormatKey, null))?.let {
            vgsTiedCardHolder.setVaultAliasFormat(it)
        }

        // ---- Card Number Configuration ----
        val cardNumberAlisaFormatKey = getString(R.string.tokenization_card_holder_alias_format_key)
        parseAliasFormat(preferences.getString(cardNumberAlisaFormatKey, null))?.let {
            vgsTiedCardNumber.setVaultAliasFormat(it)
        }

        // ---- Expiration Date Configuration ----
        vgsTiedExpiry.setEnabledTokenization(
            preferences.getBoolean(
                getString(R.string.tokenization_expiry_enabled_key),
                true
            )
        )
        val expiryStorageKey = getString(R.string.tokenization_expiry_storage_key)
        parseStorage(preferences.getString(expiryStorageKey, null))?.let {
            vgsTiedExpiry.setVaultStorageType(it)
        }
        val expiryAliasFormatKey = getString(R.string.tokenization_expiry_storage_key)
        parseAliasFormat(preferences.getString(expiryAliasFormatKey, null))?.let {
            vgsTiedExpiry.setVaultAliasFormat(it)
        }
    }

    /**
     * Converts stored string value into [VGSVaultStorageType].
     *
     * @param storage String representation from preferences
     * @return Matching enum or null if invalid
     */
    private fun parseStorage(storage: String?): VGSVaultStorageType? = when (storage) {
        VGSVaultStorageType.PERSISTENT.name -> VGSVaultStorageType.PERSISTENT
        VGSVaultStorageType.VOLATILE.name -> VGSVaultStorageType.VOLATILE
        else -> null
    }

    /**
     * Converts stored string value into [VGSVaultAliasFormat].
     *
     * @param format String representation from preferences
     * @return Matching enum or null if invalid
     */
    private fun parseAliasFormat(format: String?): VGSVaultAliasFormat? = when (format) {
        VGSVaultAliasFormat.FPE_ACC_NUM_T_FOUR.name -> VGSVaultAliasFormat.FPE_ACC_NUM_T_FOUR
        VGSVaultAliasFormat.FPE_ALPHANUMERIC_ACC_NUM_T_FOUR.name -> VGSVaultAliasFormat.FPE_ALPHANUMERIC_ACC_NUM_T_FOUR
        VGSVaultAliasFormat.FPE_SIX_T_FOUR.name -> VGSVaultAliasFormat.FPE_SIX_T_FOUR
        VGSVaultAliasFormat.FPE_SSN_T_FOUR.name -> VGSVaultAliasFormat.FPE_SSN_T_FOUR
        VGSVaultAliasFormat.FPE_T_FOUR.name -> VGSVaultAliasFormat.FPE_T_FOUR
        VGSVaultAliasFormat.NUM_LENGTH_PRESERVING.name -> VGSVaultAliasFormat.NUM_LENGTH_PRESERVING
        VGSVaultAliasFormat.PFPT.name -> VGSVaultAliasFormat.PFPT
        VGSVaultAliasFormat.RAW_UUID.name -> VGSVaultAliasFormat.RAW_UUID
        VGSVaultAliasFormat.UUID.name -> VGSVaultAliasFormat.UUID
        VGSVaultAliasFormat.GENERIC_T_FOUR.name -> VGSVaultAliasFormat.GENERIC_T_FOUR
        VGSVaultAliasFormat.ALPHANUMERIC_SIX_T_FOUR.name -> VGSVaultAliasFormat.ALPHANUMERIC_SIX_T_FOUR
        VGSVaultAliasFormat.ALPHANUMERIC_LENGTH_PRESERVING.name -> VGSVaultAliasFormat.ALPHANUMERIC_LENGTH_PRESERVING
        VGSVaultAliasFormat.ALPHANUMERIC_LENGTH_PRESERVING_T_FOUR.name -> VGSVaultAliasFormat.ALPHANUMERIC_LENGTH_PRESERVING_T_FOUR
        VGSVaultAliasFormat.ALPHANUMERIC_SSN_T_FOUR.name -> VGSVaultAliasFormat.ALPHANUMERIC_SSN_T_FOUR
        VGSVaultAliasFormat.ALPHANUMERIC_LENGTH_PRESERVING_SIX_T_FOUR.name -> VGSVaultAliasFormat.ALPHANUMERIC_LENGTH_PRESERVING_SIX_T_FOUR
        else -> null
    }
}