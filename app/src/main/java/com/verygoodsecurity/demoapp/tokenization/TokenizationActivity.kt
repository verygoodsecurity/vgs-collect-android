package com.verygoodsecurity.demoapp.tokenization

import android.animation.LayoutTransition
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
import com.verygoodsecurity.api.cardio.ScanActivity
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.StartActivity
import com.verygoodsecurity.demoapp.databinding.ActivityTokenizationBinding
import com.verygoodsecurity.demoapp.databinding.CardInputLayoutBinding
import com.verygoodsecurity.demoapp.databinding.CodeExampleLayoutBinding
import com.verygoodsecurity.demoapp.tokenization.settings.TokenizationSettingsActivity
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultStorageType
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.widget.VGSTextInputLayout
import io.github.kbiakov.codeview.adapters.Options
import io.github.kbiakov.codeview.highlight.ColorThemeData
import io.github.kbiakov.codeview.highlight.SyntaxColors
import org.json.JSONObject
import kotlin.properties.Delegates

class TokenizationActivity : AppCompatActivity(), InputFieldView.OnTextChangedListener,
    VgsCollectResponseListener {

    companion object {

        private const val SCAN_REQUEST_CODE = 1
    }

    private val defaultHintTextColor by lazy { ContextCompat.getColor(this, R.color.fiord) }
    private val defaultInputBackgroundColor by lazy {
        ContextCompat.getColor(this, R.color.fiord_20)
    }
    private val errorHintTextColor by lazy { ContextCompat.getColor(this, R.color.brown) }
    private val errorInputBackgroundColor by lazy {
        ContextCompat.getColor(this, R.color.vanillaIce)
    }

    private var collect: VGSCollect? = null

    private var response: String? by Delegates.observable(null) { _, _, new ->
        binding.mbReset.isVisible = !new.isNullOrBlank()
        updateCodeExample(response)
    }

    private lateinit var binding: ActivityTokenizationBinding
    private lateinit var codeExampleBinding: CodeExampleLayoutBinding
    private lateinit var cardViewBinding: CardInputLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTokenizationBinding.inflate(layoutInflater)
        codeExampleBinding = CodeExampleLayoutBinding.bind(binding.root)
        cardViewBinding = binding.includeCardView
        setContentView(binding.root)
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
        @Suppress("DEPRECATION") super.onActivityResult(requestCode, resultCode, data)
        collect?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        collect?.onDestroy()
        collect = null
    }

    override fun onTextChange(view: InputFieldView, isEmpty: Boolean) {
        with(cardViewBinding) {
            val (title, layout) = (when (view.id) {
                R.id.vgsTiedCardHolder -> mtvCardHolderHint to vgsTilCardHolder
                R.id.vgsTiedCardNumber -> mtvCardNumberHint to vgsTilCardNumber
                R.id.vgsTiedExpiry -> mtvExpiryHint to vgsTilExpiry
                R.id.vgsTiedCvc -> mtvCvcHint to vgsTilCvc
                else -> throw IllegalArgumentException("Not implemented.")
            })
            setInputValid(title, layout)
        }
    }

    override fun onResponse(response: VGSResponse?) {
        Log.d(this::class.java.simpleName, response.toString())
        setLoading(false)
        this.response = response?.body
        if (response is VGSResponse.ErrorResponse) {
            showSnackBar("Code: ${response.code}, ${response.localizeMessage}")
        }
    }

    private fun initCollect() {
        with(intent?.extras) {
            collect = VGSCollect(
                this@TokenizationActivity,
                this?.getString(StartActivity.KEY_BUNDLE_VAULT_ID) ?: "",
                this?.getString(StartActivity.KEY_BUNDLE_ENVIRONMENT) ?: ""
            )
            collect?.addOnResponseListeners(this@TokenizationActivity)
        }
    }

    private fun initViews() {
        binding.clRoot.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        bindViews()
        configureTokenization()
        initTextChangeListener()
        initClickListeners()
        initCodeExampleView()
        updateCodeExample(null)
    }

    private fun bindViews() {
        collect?.bindView(cardViewBinding.vgsTiedCardHolder)
        collect?.bindView(cardViewBinding.vgsTiedCardNumber)
        collect?.bindView(cardViewBinding.vgsTiedExpiry)
        collect?.bindView(cardViewBinding.vgsTiedCvc)
    }

    private fun configureTokenization() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        cardViewBinding.vgsTiedCardHolder.setEnabledTokenization(
            preferences.getBoolean(
                getString(R.string.tokenization_card_holder_enabled_key), true
            )
        )
        preferences.getString(getString(R.string.tokenization_card_holder_storage_key), null)?.let {
            cardViewBinding.vgsTiedCardHolder.setVaultStorageType(parseStorage(it))
        }
        preferences.getString(getString(R.string.tokenization_card_holder_alias_format_key), null)
            ?.let {
                cardViewBinding.vgsTiedCardHolder.setVaultAliasFormat(parseAliasFormat(it))
            }

        preferences.getString(getString(R.string.tokenization_card_number_alias_format_key), null)
            ?.let {
                cardViewBinding.vgsTiedCardNumber.setVaultAliasFormat(parseAliasFormat(it))
            }

        cardViewBinding.vgsTiedExpiry.setEnabledTokenization(
            preferences.getBoolean(
                getString(R.string.tokenization_expiry_enabled_key), true
            )
        )
        preferences.getString(getString(R.string.tokenization_expiry_storage_key), null)?.let {
            cardViewBinding.vgsTiedExpiry.setVaultStorageType(parseStorage(it))
        }
        preferences.getString(getString(R.string.tokenization_expiry_alias_format_key), null)?.let {
            cardViewBinding.vgsTiedExpiry.setVaultAliasFormat(parseAliasFormat(it))
        }
    }

    private fun initTextChangeListener() {
        cardViewBinding.vgsTiedCardHolder.addOnTextChangeListener(this)
        cardViewBinding.vgsTiedCardNumber.addOnTextChangeListener(this)
        cardViewBinding.vgsTiedExpiry.addOnTextChangeListener(this)
        cardViewBinding.vgsTiedCvc.addOnTextChangeListener(this)
    }

    private fun initClickListeners() {
        binding.mbTokenize.setOnClickListener {
            runIfInputsValid {
                tokenize()
            }
        }
        codeExampleBinding.ivCopyCodeExample.setOnClickListener { copyResponseToClipboard() }
        binding.mbReset.setOnClickListener { resetView() }
    }

    private fun initCodeExampleView() {
        val syntaxColor = ContextCompat.getColor(this, R.color.veryLightGray)
        val bgColor = ContextCompat.getColor(this, R.color.blackPearl)
        val lineNumberColor = ContextCompat.getColor(this, R.color.nobel)
        codeExampleBinding.cvResponse.setOptions(
            Options(
                context = this.applicationContext, theme = ColorThemeData(
                    SyntaxColors(
                        string = syntaxColor,
                        punctuation = syntaxColor,
                    ),
                    numColor = lineNumberColor,
                    bgContent = bgColor,
                    bgNum = bgColor,
                    noteColor = syntaxColor,
                )
            )
        )
        codeExampleBinding.cvResponse.alpha = 1f
    }

    private fun updateCodeExample(response: String?) {
        codeExampleBinding.cvResponse.setCode(formatJson(response))
    }

    private fun tokenize() {
        setLoading(true)
        collect?.tokenize()
    }

    private fun copyResponseToClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("", formatJson(response)))
        showSnackBar("Tokenized card response copied.")
    }

    private fun resetView() {
        response = null
    }

    private fun scanCard() {
        val intent = Intent(this, ScanActivity::class.java).apply {
            putExtra(ScanActivity.SCAN_CONFIGURATION, hashMapOf<String?, Int>().apply {
                this[cardViewBinding.vgsTiedCardNumber.getFieldName()] = ScanActivity.CARD_NUMBER
                this[cardViewBinding.vgsTiedCardHolder.getFieldName()] = ScanActivity.CARD_HOLDER
                this[cardViewBinding.vgsTiedExpiry.getFieldName()] = ScanActivity.CARD_EXP_DATE
                this[cardViewBinding.vgsTiedCvc.getFieldName()] = ScanActivity.CARD_CVC
            })
        }
        @Suppress("DEPRECATION") startActivityForResult(intent, SCAN_REQUEST_CODE)
    }

    private fun openSettings() {
        TokenizationSettingsActivity.start(this)
    }

    private fun runIfInputsValid(action: () -> Unit) {
        with(cardViewBinding) {
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
    }

    private fun setInputValid(title: MaterialTextView, layout: VGSTextInputLayout) {
        title.setTextColor(defaultHintTextColor)
        layout.setBoxBackgroundColor(defaultInputBackgroundColor)
    }

    private fun setInputInvalid(title: MaterialTextView, layout: VGSTextInputLayout) {
        title.setTextColor(errorHintTextColor)
        layout.setBoxBackgroundColor(errorInputBackgroundColor)
    }

    private fun setLoading(isLoading: Boolean) {
        binding.viewOverlay.isVisible = isLoading
        binding.progressBar.isVisible = isLoading
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).apply {
            anchorView = binding.mbTokenize
            animationMode = Snackbar.ANIMATION_MODE_SLIDE
        }.show()
    }

    private fun formatJson(json: String?): String = try {
        JSONObject(json ?: "").toString(4)
    } catch (e: Exception) {
        ""
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