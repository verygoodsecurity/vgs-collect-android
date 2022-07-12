package com.verygoodsecurity.demoapp.tokenization

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.verygoodsecurity.api.cardio.ScanActivity
import com.verygoodsecurity.demoapp.BuildConfig
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.StartActivity
import com.verygoodsecurity.demoapp.activity_case.VGSCollectActivity
import com.verygoodsecurity.demoapp.tokenization.model.TokenizedCard
import com.verygoodsecurity.demoapp.tokenization.settings.TokenizationSettingsActivity
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.network.tokenization.VGSTokenizationRequest
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultStorageType
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.widget.VGSTextInputLayout
import kotlinx.android.synthetic.main.activity_tokenization.*
import kotlin.properties.Delegates

class TokenizationActivity :
    AppCompatActivity(R.layout.activity_tokenization),
    InputFieldView.OnTextChangedListener, VgsCollectResponseListener {

    private val defaultTextColor by lazy { ContextCompat.getColor(this, R.color.fiord) }
    private val defaultBackgroundColor by lazy { ContextCompat.getColor(this, R.color.fiord_20) }
    private val errorTextColor by lazy { ContextCompat.getColor(this, R.color.brown) }
    private val errorBackgroundColor by lazy { ContextCompat.getColor(this, R.color.vanillaIce) }

    private lateinit var collect: VGSCollect

    private var response: String? by Delegates.observable(null) { _, _, new ->
        mbReset.isVisible = !new.isNullOrBlank()
        updateCardView(
            try {
                Gson().fromJson(new, TokenizedCard::class.java)
            } catch (e: Exception) {
                showSnackBar(e.message ?: "Can't parse response.")
                null
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCollect()
        initViews()
    }

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
            R.id.scan -> {
                scanCard()
                true
            }
            R.id.settings -> {
                openSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        collect.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        collect.onDestroy()
    }

    override fun onTextChange(view: InputFieldView, isEmpty: Boolean) {
        val (title, layout) = (when (view.id) {
            R.id.vgsTiedCardHolder -> mtvCardHolderHint to vgsTilCardHolder
            R.id.vgsTiedCardNumber -> mtvCardNumberHint to vgsTilCardNumber
            R.id.vgsTiedExpiry -> mtvExpiryHint to vgsTilExpiry
            R.id.vgsTiedCvc -> mtvCvcHint to vgsTilCvc
            else -> throw IllegalArgumentException("Not implemented.")
        })
        setInputValid(title, layout)
    }

    override fun onResponse(response: VGSResponse?) {
        Log.d(this::class.java.simpleName, response.toString())
        setLoading(false)
        when (response) {
            is VGSResponse.SuccessResponse -> this.response = response.body
            is VGSResponse.ErrorResponse -> showSnackBar("Code: ${response.code}, ${response.localizeMessage}")
            null -> {}
        }
    }

    private fun initCollect() {
        with(intent?.extras) {
            collect = VGSCollect(
                this@TokenizationActivity,
                this?.getString(StartActivity.KEY_BUNDLE_VAULT_ID) ?: "",
                this?.getString(StartActivity.KEY_BUNDLE_ENVIRONMENT) ?: ""
            )
            collect.addOnResponseListeners(this@TokenizationActivity)
        }
    }

    private fun initViews() {
        bindViews()
        configureTokenization()
        initTextChangeListener()
        initClickListeners()
    }

    private fun bindViews() {
        collect.bindView(vgsTiedCardHolder)
        collect.bindView(vgsTiedCardNumber)
        collect.bindView(vgsTiedExpiry)
        collect.bindView(vgsTiedCvc)
    }

    private fun configureTokenization() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        vgsTiedCardHolder.setEnabledTokenization(
            preferences.getBoolean(
                getString(R.string.tokenization_card_holder_enabled_key),
                true
            )
        )
        preferences.getString(getString(R.string.tokenization_card_holder_storage_key), null)?.let {
            vgsTiedCardHolder.setVaultStorageType(parseStorage(it))
        }
        preferences.getString(getString(R.string.tokenization_card_holder_alias_format_key), null)
            ?.let {
                vgsTiedCardHolder.setVaultAliasFormat(parseAliasFormat(it))
            }

        preferences.getString(getString(R.string.tokenization_card_number_alias_format_key), null)
            ?.let {
                vgsTiedCardNumber.setVaultAliasFormat(parseAliasFormat(it))
            }

        vgsTiedExpiry.setEnabledTokenization(
            preferences.getBoolean(
                getString(R.string.tokenization_expiry_enabled_key),
                true
            )
        )
        preferences.getString(getString(R.string.tokenization_expiry_storage_key), null)?.let {
            vgsTiedExpiry.setVaultStorageType(parseStorage(it))
        }
        preferences.getString(getString(R.string.tokenization_expiry_alias_format_key), null)
            ?.let {
                vgsTiedExpiry.setVaultAliasFormat(parseAliasFormat(it))
            }
    }

    private fun initTextChangeListener() {
        vgsTiedCardHolder.addOnTextChangeListener(this)
        vgsTiedCardNumber.addOnTextChangeListener(this)
        vgsTiedExpiry.addOnTextChangeListener(this)
        vgsTiedCvc.addOnTextChangeListener(this)
    }

    private fun initClickListeners() {
        mbTokenize.setOnClickListener {
            runIfInputsValid {
                tokenize()
            }
        }
        mcvCard.setOnClickListener { copyResponseToClipboard() }
        mbReset.setOnClickListener { resetView() }
    }

    private fun updateCardView(card: TokenizedCard?) {
        val number =
            Regex("(\\d{4})(\\d{4})(\\d{4})(\\d{4})").replace(card?.number ?: "", "\$1 \$2 \$3 \$4")
        mtvCardNumber.text = number
        mtvCardHolder.text = card?.holder
        mtvExpiry.text = card?.expiry
    }

    private fun tokenize() {
        setLoading(true)
        collect.tokenize(
            VGSTokenizationRequest.VGSRequestBuilder()
                .setRouteId(BuildConfig.ROUTE_ID)
                .build()
        )
    }

    private fun copyResponseToClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("", response))
        showSnackBar("Tokenized card response copied.")
    }

    private fun resetView() {
        vgsTiedCardHolder.setText(null)
        vgsTiedCardNumber.setText(null)
        vgsTiedExpiry.setText(null)
        vgsTiedCvc.setText(null)
        response = null
    }

    private fun scanCard() {
        val intent = Intent(this, ScanActivity::class.java).apply {
            putExtra(ScanActivity.SCAN_CONFIGURATION, hashMapOf<String?, Int>().apply {
                this[vgsTiedCardNumber?.getFieldName()] = ScanActivity.CARD_NUMBER
                this[vgsTiedCardHolder?.getFieldName()] = ScanActivity.CARD_HOLDER
                this[vgsTiedExpiry?.getFieldName()] = ScanActivity.CARD_EXP_DATE
                this[vgsTiedCvc?.getFieldName()] = ScanActivity.CARD_CVC
            })
        }
        @Suppress("DEPRECATION")
        startActivityForResult(intent, VGSCollectActivity.USER_SCAN_REQUEST_CODE)
    }

    private fun openSettings() {
        TokenizationSettingsActivity.start(this)
    }

    private fun runIfInputsValid(action: () -> Unit) {
        var isValid = true
        if (vgsTiedCardHolder.getState()?.isValid == false) {
            setInputInvalid(mtvCardHolderHint, vgsTilCardHolder)
            isValid = false
        }
        if (vgsTiedCardNumber.getState()?.isValid == false) {
            setInputInvalid(mtvCardNumberHint, vgsTilCardNumber)
            isValid = false
        }
        if (vgsTiedExpiry.getState()?.isValid == false) {
            setInputInvalid(mtvExpiryHint, vgsTilExpiry)
            isValid = false
        }
        if (vgsTiedCvc.getState()?.isValid == false) {
            setInputInvalid(mtvCvcHint, vgsTilCvc)
            isValid = false
        }
        if (isValid) action.invoke()
    }

    private fun setInputValid(title: MaterialTextView, layout: VGSTextInputLayout) {
        title.setTextColor(defaultTextColor)
        layout.setBoxBackgroundColor(defaultBackgroundColor)
    }

    private fun setInputInvalid(title: MaterialTextView, layout: VGSTextInputLayout) {
        title.setTextColor(errorTextColor)
        layout.setBoxBackgroundColor(errorBackgroundColor)
    }

    private fun setLoading(isLoading: Boolean) {
        viewOverlay.isVisible = isLoading
        progressBar?.isVisible = isLoading
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).apply {
            anchorView = mbTokenize
            animationMode = Snackbar.ANIMATION_MODE_SLIDE
        }.show()
    }

    private fun parseStorage(storage: String): VGSVaultStorageType = when (storage) {
        VGSVaultStorageType.PERSISTENT.name -> VGSVaultStorageType.PERSISTENT
        VGSVaultStorageType.VOLATILE.name -> VGSVaultStorageType.VOLATILE
        else -> throw IllegalArgumentException("Not implemented!")
    }

    private fun parseAliasFormat(format: String): VGSVaultAliasFormat = when (format) {
        VGSVaultAliasFormat.UUID.name -> VGSVaultAliasFormat.UUID
        VGSVaultAliasFormat.FPE_SIX_T_FOUR.name -> VGSVaultAliasFormat.FPE_SIX_T_FOUR
        VGSVaultAliasFormat.NUM_LENGTH_PRESERVING.name -> VGSVaultAliasFormat.NUM_LENGTH_PRESERVING
        else -> throw IllegalArgumentException("Not implemented!")
    }
}